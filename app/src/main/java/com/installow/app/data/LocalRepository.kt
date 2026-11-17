package com.installow.app.data

import android.content.Context
import com.installow.app.model.AccountAnnotation
import com.installow.app.model.DataSourceType
import com.installow.app.model.LanguagePreference
import com.installow.app.model.Snapshot
import com.installow.app.model.ThemePreference
import com.installow.app.util.SecurityManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

data class PersistedAppState(
    val snapshots: List<Snapshot>,
    val annotations: Map<String, AccountAnnotation>,
    val dataSourceType: DataSourceType,
    val themePreference: ThemePreference,
    val languagePreference: LanguagePreference,
    val hideWhitelistedInNonFollowers: Boolean,
    val activeOwnerUsername: String
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
                parseRootJson(String(plainBytes, StandardCharsets.UTF_8))
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
                parseRootJson(legacyFile.readText(StandardCharsets.UTF_8))
            }.getOrNull()
            if (migrated != null) {
                saveState(migrated)
                runCatching { legacyFile.delete() }
                return@synchronized migrated
            }
        }

        null
    }

    private fun parseRootJson(rawJson: String): PersistedAppState? {
        return try {
            val root = JSONObject(rawJson)
            val snapshotsArr = root.optJSONArray("snapshots") ?: JSONArray()
            val snapshots = buildList {
                for (i in 0 until snapshotsArr.length()) {
                    parseSnapshot(snapshotsArr.optJSONObject(i))?.let { add(it) }
                }
            }
            if (snapshots.isEmpty()) return null

            val annotationsObj = root.optJSONObject("annotations") ?: JSONObject()
            val annotations = linkedMapOf<String, AccountAnnotation>()
            for (key in annotationsObj.keys()) {
                val obj = annotationsObj.optJSONObject(key) ?: continue
                annotations[key] = AccountAnnotation(
                    isWhitelisted = obj.optBoolean("isWhitelisted", false),
                    customTag = obj.optString("customTag", ""),
                    customNote = obj.optString("customNote", "")
                )
            }

            val sourceType = runCatching {
                DataSourceType.valueOf(root.optString("dataSourceType", DataSourceType.DEMO.name))
            }.getOrDefault(DataSourceType.DEMO)

            val themePref = runCatching {
                ThemePreference.valueOf(root.optString("themePreference", ThemePreference.DARK.name))
            }.getOrDefault(ThemePreference.DARK)

            val langPref = runCatching {
                LanguagePreference.valueOf(root.optString("languagePreference", LanguagePreference.SYSTEM.name))
            }.getOrDefault(LanguagePreference.SYSTEM)

            PersistedAppState(
                snapshots = snapshots,
                annotations = annotations,
                dataSourceType = sourceType,
                themePreference = themePref,
                languagePreference = langPref,
                hideWhitelistedInNonFollowers = root.optBoolean("hideWhitelistedInNonFollowers", true),
                activeOwnerUsername = root.optString("activeOwnerUsername", "benim.hesabim")
            )
        } catch (_: Exception) {
            null
        }
    }

    fun saveState(state: PersistedAppState) = synchronized(ioLock) {
        try {
            val root = JSONObject()
            val snapshotsToKeep = state.snapshots.takeLast(15)
            val snapshotsArr = JSONArray()
            snapshotsToKeep.forEach { snapshotsArr.put(serializeSnapshot(it)) }
            root.put("snapshots", snapshotsArr)

            val annotationsObj = JSONObject()
            state.annotations.forEach { (username, ann) ->
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
            root.put("annotations", annotationsObj)
            root.put("dataSourceType", state.dataSourceType.name)
            root.put("themePreference", state.themePreference.name)
            root.put("languagePreference", state.languagePreference.name)
            root.put("hideWhitelistedInNonFollowers", state.hideWhitelistedInNonFollowers)
            root.put("activeOwnerUsername", state.activeOwnerUsername)

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
            followerCounts = jsonToMapLong(obj.optJSONObject("followerCounts"))
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
