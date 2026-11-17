package com.installow.app.data

import android.content.Context
import com.installow.app.model.AccountAnnotation
import com.installow.app.model.AccountWorkspace
import com.installow.app.model.DataSourceType
import com.installow.app.model.LanguagePreference
import com.installow.app.model.ListAccessStatus
import com.installow.app.model.Snapshot
import com.installow.app.model.ThemePreference
import com.installow.app.util.SecurityManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

data class PersistedAppState(
    val workspaces: List<AccountWorkspace>,
    val activeWorkspaceId: String,
    val themePreference: ThemePreference,
    val languagePreference: LanguagePreference,
    val hideWhitelistedInNonFollowers: Boolean
)

class LocalRepository(private val context: Context) {

    private val ioLock = Any()

    private val encryptedStateFile: File
        get() = File(context.filesDir, "installow_state_v2.enc")

    private val legacyPlainStateFile: File
        get() = File(context.filesDir, "installow_state_v1.json")

    fun loadState(): PersistedAppState? = synchronized(ioLock) {
        val encFile = encryptedStateFile
        if (encFile.exists()) {
            val decrypted = runCatching {
                val plainBytes = SecurityManager.decryptBytes(encFile.readBytes())
                parseStateJson(String(plainBytes, StandardCharsets.UTF_8))
            }.getOrNull()
            if (decrypted != null) {
                if (legacyPlainStateFile.exists()) {
                    runCatching { legacyPlainStateFile.delete() }
                }
                return@synchronized decrypted
            }
        }

        val legacyFile = legacyPlainStateFile
        if (legacyFile.exists()) {
            val migrated = runCatching {
                parseStateJson(legacyFile.readText(StandardCharsets.UTF_8))
            }.getOrNull()
            if (migrated != null) {
                saveState(migrated)
                runCatching { legacyFile.delete() }
                return@synchronized migrated
            }
        }

        null
    }

    fun saveState(state: PersistedAppState) = synchronized(ioLock) {
        try {
            val root = serializeStateJson(state)
            val plainBytes = root.toString().toByteArray(StandardCharsets.UTF_8)
            val encryptedBytes = SecurityManager.encryptBytes(plainBytes)

            val targetFile = encryptedStateFile
            val tmpFile = File(context.filesDir, "installow_state_v2.enc.tmp")
            tmpFile.writeBytes(encryptedBytes)
            if (!tmpFile.renameTo(targetFile)) {
                tmpFile.copyTo(targetFile, overwrite = true)
                tmpFile.delete()
            }

            if (legacyPlainStateFile.exists()) {
                legacyPlainStateFile.delete()
            }
        } catch (_: Exception) {
            // Keep app resilient if disk write fails
        }
    }

    companion object {
        internal fun serializeStateJson(state: PersistedAppState): JSONObject {
            val root = JSONObject()
            val workspacesArr = JSONArray()
            state.workspaces.take(15).forEach { ws ->
                workspacesArr.put(serializeWorkspace(ws))
            }
            root.put("workspaces", workspacesArr)
            root.put("activeWorkspaceId", state.activeWorkspaceId)
            root.put("themePreference", state.themePreference.name)
            root.put("languagePreference", state.languagePreference.name)
            root.put("hideWhitelistedInNonFollowers", state.hideWhitelistedInNonFollowers)
            return root
        }

        internal fun parseStateJson(rawJson: String): PersistedAppState? {
            return try {
                val root = JSONObject(rawJson)
                val themePref = runCatching {
                    ThemePreference.valueOf(root.optString("themePreference", ThemePreference.DARK.name))
                }.getOrDefault(ThemePreference.DARK)

                val langPref = runCatching {
                    LanguagePreference.valueOf(root.optString("languagePreference", LanguagePreference.SYSTEM.name))
                }.getOrDefault(LanguagePreference.SYSTEM)

                val hideWhitelisted = root.optBoolean("hideWhitelistedInNonFollowers", true)

                val workspacesArr = root.optJSONArray("workspaces")
                val workspaces = if (workspacesArr != null && workspacesArr.length() > 0) {
                    buildList {
                        for (i in 0 until workspacesArr.length()) {
                            parseWorkspace(workspacesArr.optJSONObject(i))?.let { add(it) }
                        }
                    }
                } else {
                    // Migrate legacy single-account JSON format
                    val legacyWs = parseLegacySingleWorkspace(root) ?: return null
                    listOf(legacyWs)
                }

                if (workspaces.isEmpty()) return null
                val requestedActiveId = root.optString("activeWorkspaceId", workspaces.first().id)
                val resolvedActiveId = if (workspaces.any { it.id == requestedActiveId }) {
                    requestedActiveId
                } else {
                    workspaces.first().id
                }

                PersistedAppState(
                    workspaces = workspaces,
                    activeWorkspaceId = resolvedActiveId,
                    themePreference = themePref,
                    languagePreference = langPref,
                    hideWhitelistedInNonFollowers = hideWhitelisted
                )
            } catch (_: Exception) {
                null
            }
        }

        private fun parseLegacySingleWorkspace(root: JSONObject): AccountWorkspace? {
            val snapshotsArr = root.optJSONArray("snapshots") ?: JSONArray()
            val snapshots = buildList {
                for (i in 0 until snapshotsArr.length()) {
                    parseSnapshot(snapshotsArr.optJSONObject(i))?.let { add(it) }
                }
            }
            if (snapshots.isEmpty()) return null
            val annotations = parseAnnotations(root.optJSONObject("annotations"))
            val sourceType = runCatching {
                DataSourceType.valueOf(root.optString("dataSourceType", DataSourceType.DEMO.name))
            }.getOrDefault(DataSourceType.DEMO)
            val owner = root.optString("activeOwnerUsername", "benim.hesabim").ifBlank { "benim.hesabim" }
            return AccountWorkspace(
                id = owner.lowercase(),
                ownerUsername = owner,
                dataSourceType = sourceType,
                snapshots = snapshots,
                annotations = annotations
            )
        }

        private fun serializeWorkspace(ws: AccountWorkspace): JSONObject {
            return JSONObject().apply {
                put("id", ws.id)
                put("ownerUsername", ws.ownerUsername)
                put("displayName", ws.displayName)
                put("dataSourceType", ws.dataSourceType.name)
                val snapshotsArr = JSONArray()
                ws.snapshots.takeLast(15).forEach { snapshotsArr.put(serializeSnapshot(it)) }
                put("snapshots", snapshotsArr)
                put("annotations", serializeAnnotations(ws.annotations))
                if (ws.reportedFollowersCount != null) put("reportedFollowersCount", ws.reportedFollowersCount)
                if (ws.reportedFollowingCount != null) put("reportedFollowingCount", ws.reportedFollowingCount)
                if (ws.isPrivateAccount != null) put("isPrivateAccount", ws.isPrivateAccount)
                put("listAccessStatus", ws.listAccessStatus.name)
            }
        }

        private fun parseWorkspace(obj: JSONObject?): AccountWorkspace? {
            if (obj == null) return null
            val owner = obj.optString("ownerUsername", "").ifBlank { return null }
            val id = obj.optString("id", owner.lowercase()).ifBlank { owner.lowercase() }
            val snapshotsArr = obj.optJSONArray("snapshots") ?: JSONArray()
            val snapshots = buildList {
                for (i in 0 until snapshotsArr.length()) {
                    parseSnapshot(snapshotsArr.optJSONObject(i))?.let { add(it) }
                }
            }
            val sourceType = runCatching {
                DataSourceType.valueOf(obj.optString("dataSourceType", DataSourceType.DEMO.name))
            }.getOrDefault(DataSourceType.DEMO)
            val listStatus = runCatching {
                ListAccessStatus.valueOf(obj.optString("listAccessStatus", ListAccessStatus.FULL_DATA.name))
            }.getOrDefault(ListAccessStatus.FULL_DATA)

            return AccountWorkspace(
                id = id,
                ownerUsername = owner,
                displayName = obj.optString("displayName", ""),
                dataSourceType = sourceType,
                snapshots = snapshots,
                annotations = parseAnnotations(obj.optJSONObject("annotations")),
                reportedFollowersCount = if (obj.has("reportedFollowersCount")) obj.optInt("reportedFollowersCount") else null,
                reportedFollowingCount = if (obj.has("reportedFollowingCount")) obj.optInt("reportedFollowingCount") else null,
                isPrivateAccount = if (obj.has("isPrivateAccount")) obj.optBoolean("isPrivateAccount") else null,
                listAccessStatus = listStatus
            )
        }

        private fun serializeAnnotations(annotations: Map<String, AccountAnnotation>): JSONObject {
            val annotationsObj = JSONObject()
            annotations.forEach { (username, ann) ->
                if (ann.isWhitelisted || ann.customTag.isNotBlank() || ann.customNote.isNotBlank()) {
                    annotationsObj.put(
                        username,
                        JSONObject().apply {
                            put("isWhitelisted", ann.isWhitelisted)
                            put("customTag", ann.customTag)
                            put("customNote", ann.customNote)
                        }
                    )
                }
            }
            return annotationsObj
        }

        private fun parseAnnotations(annotationsObj: JSONObject?): Map<String, AccountAnnotation> {
            if (annotationsObj == null) return emptyMap()
            val annotations = linkedMapOf<String, AccountAnnotation>()
            for (key in annotationsObj.keys()) {
                val obj = annotationsObj.optJSONObject(key) ?: continue
                annotations[key] = AccountAnnotation(
                    isWhitelisted = obj.optBoolean("isWhitelisted", false),
                    customTag = obj.optString("customTag", ""),
                    customNote = obj.optString("customNote", "")
                )
            }
            return annotations
        }

        private fun serializeSnapshot(s: Snapshot): JSONObject {
            return JSONObject().apply {
                put("id", s.id)
                put("createdAtMillis", s.createdAtMillis)
                put("sourceLabel", s.sourceLabel)
                put("followers", mapLongToJson(s.followers))
                put("following", mapLongToJson(s.following))
                put("pendingRequests", mapLongToJson(s.pendingRequests))
                put("closeFriends", JSONArray(s.closeFriends.toList()))
                put("displayNames", mapStringToJson(s.displayNames))
                put("followerCounts", mapLongToJson(s.followerCounts))
                if (s.reportedFollowersCount != null) put("reportedFollowersCount", s.reportedFollowersCount)
                if (s.reportedFollowingCount != null) put("reportedFollowingCount", s.reportedFollowingCount)
                if (s.isPrivateAccount != null) put("isPrivateAccount", s.isPrivateAccount)
            }
        }

        private fun parseSnapshot(obj: JSONObject?): Snapshot? {
            if (obj == null) return null
            return Snapshot(
                id = obj.optString("id"),
                createdAtMillis = obj.optLong("createdAtMillis"),
                sourceLabel = obj.optString("sourceLabel", "Kayıtlı Veri"),
                followers = jsonToMapLong(obj.optJSONObject("followers")),
                following = jsonToMapLong(obj.optJSONObject("following")),
                pendingRequests = jsonToMapLong(obj.optJSONObject("pendingRequests")),
                closeFriends = buildSet {
                    val arr = obj.optJSONArray("closeFriends") ?: JSONArray()
                    for (i in 0 until arr.length()) add(arr.optString(i))
                },
                displayNames = jsonToMapString(obj.optJSONObject("displayNames")),
                followerCounts = jsonToMapLong(obj.optJSONObject("followerCounts")),
                reportedFollowersCount = if (obj.has("reportedFollowersCount")) obj.optInt("reportedFollowersCount") else null,
                reportedFollowingCount = if (obj.has("reportedFollowingCount")) obj.optInt("reportedFollowingCount") else null,
                isPrivateAccount = if (obj.has("isPrivateAccount")) obj.optBoolean("isPrivateAccount") else null
            )
        }

        private fun mapLongToJson(map: Map<String, Long>): JSONObject {
            val obj = JSONObject()
            map.forEach { (k, v) -> obj.put(k, v) }
            return obj
        }

        private fun mapStringToJson(map: Map<String, String>): JSONObject {
            val obj = JSONObject()
            map.forEach { (k, v) -> if (v.isNotBlank()) obj.put(k, v) }
            return obj
        }

        private fun jsonToMapLong(obj: JSONObject?): Map<String, Long> {
            if (obj == null) return emptyMap()
            val result = linkedMapOf<String, Long>()
            for (k in obj.keys()) result[k] = obj.optLong(k, 0L)
            return result
        }

        private fun jsonToMapString(obj: JSONObject?): Map<String, String> {
            if (obj == null) return emptyMap()
            val result = linkedMapOf<String, String>()
            for (k in obj.keys()) result[k] = obj.optString(k, "")
            return result
        }
    }
}
