package com.installow.app.data

import android.webkit.CookieManager
import com.installow.app.model.ListAccessStatus
import com.installow.app.util.SecurityManager
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

data class PublicLookupResult(
    val username: String,
    val displayName: String,
    val userId: String?,
    val isPrivate: Boolean?,
    val followersCount: Int?,
    val followingCount: Int?,
    val followers: Map<String, Pair<String, Long>> = emptyMap(),
    val following: Map<String, Pair<String, Long>> = emptyMap(),
    val listAccessStatus: ListAccessStatus
)

object InstagramPublicLookup {

    private const val INSTAGRAM_APP_ID = "936619743392459"
    private const val MOBILE_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
    private const val MAX_RESPONSE_BYTES = 2 * 1024 * 1024
    private const val MAX_PUBLIC_LIST_PAGES = 15

    private val OG_DESC_REGEX = Regex(
        """<meta[^>]+property=["']og:description["'][^>]+content=["']([^"']+)["']""",
        RegexOption.IGNORE_CASE
    )
    private val OG_TITLE_REGEX = Regex(
        """<meta[^>]+property=["']og:title["'][^>]+content=["']([^"']+)["']""",
        RegexOption.IGNORE_CASE
    )
    private val FOLLOWERS_COUNT_META_REGEX = Regex(
        """([\d.,]+\s*(?:[KMBkmb]|Mn|Bn|Bin)?)\s*(?:Followers|Takip[çc]i)""",
        RegexOption.IGNORE_CASE
    )
    private val FOLLOWING_COUNT_META_REGEX = Regex(
        """([\d.,]+\s*(?:[KMBkmb]|Mn|Bn|Bin)?)\s*(?:Following|Takip(?![çc]i))""",
        RegexOption.IGNORE_CASE
    )
    private val JSON_FOLLOWED_BY_REGEX = Regex(""""edge_followed_by"\s*:\s*\{\s*"count"\s*:\s*(\d+)""")
    private val JSON_FOLLOW_REGEX = Regex(""""edge_follow"\s*:\s*\{\s*"count"\s*:\s*(\d+)""")
    private val JSON_IS_PRIVATE_REGEX = Regex(""""is_private"\s*:\s*(true|false)""")
    private val JSON_USER_ID_REGEX = Regex(""""(?:profilePage_|id":\s*")(\d{5,20})""")

    fun lookupProfile(rawUsername: String): PublicLookupResult {
        val clean = InstagramExportParser.normalizeUsername(rawUsername)
            ?: return PublicLookupResult(
                username = rawUsername.trim().removePrefix("@"),
                displayName = "",
                userId = null,
                isPrivate = null,
                followersCount = null,
                followingCount = null,
                listAccessStatus = ListAccessStatus.PROFILE_NOT_FOUND
            )

        val cookieHeader = readSafeInstagramCookieHeader()
        val csrfToken = extractCookieValue(cookieHeader, "csrftoken")

        // 1. Web Profile Info JSON endpoint
        val apiProfile = fetchViaWebProfileApi(clean, cookieHeader, csrfToken)
        var parsedProfile = apiProfile.first
        val apiHttpCode = apiProfile.second

        if (apiHttpCode == 404) {
            return PublicLookupResult(
                username = clean,
                displayName = "",
                userId = null,
                isPrivate = null,
                followersCount = null,
                followingCount = null,
                listAccessStatus = ListAccessStatus.PROFILE_NOT_FOUND
            )
        }

        // 2. HTML Meta fallback if JSON API did not yield counts
        if (parsedProfile == null || parsedProfile.followersCount == null) {
            val htmlRes = fetchViaHtmlPage(clean, cookieHeader)
            if (htmlRes.second == 404) {
                return PublicLookupResult(
                    username = clean,
                    displayName = "",
                    userId = null,
                    isPrivate = null,
                    followersCount = null,
                    followingCount = null,
                    listAccessStatus = ListAccessStatus.PROFILE_NOT_FOUND
                )
            }
            val htmlParsed = htmlRes.first
            if (htmlParsed != null) {
                parsedProfile = if (parsedProfile == null) {
                    htmlParsed
                } else {
                    parsedProfile.copy(
                        displayName = parsedProfile.displayName.ifBlank { htmlParsed.displayName },
                        followersCount = parsedProfile.followersCount ?: htmlParsed.followersCount,
                        followingCount = parsedProfile.followingCount ?: htmlParsed.followingCount,
                        isPrivate = parsedProfile.isPrivate ?: htmlParsed.isPrivate,
                        userId = parsedProfile.userId ?: htmlParsed.userId
                    )
                }
            }
        }

        if (parsedProfile == null) {
            return PublicLookupResult(
                username = clean,
                displayName = "",
                userId = null,
                isPrivate = null,
                followersCount = null,
                followingCount = null,
                listAccessStatus = ListAccessStatus.NETWORK_ERROR
            )
        }

        // 3. If private, return counts immediately with PRIVATE_ACCOUNT notice
        if (parsedProfile.isPrivate == true) {
            return parsedProfile.copy(listAccessStatus = ListAccessStatus.PRIVATE_ACCOUNT)
        }

        // 4. Public account: attempt safe read of followers/following if userId is known
        val uid = parsedProfile.userId
        if (!uid.isNullOrBlank()) {
            val followersRes = fetchPublicConnectionList(uid, "followers", cookieHeader, csrfToken)
            val followingRes = fetchPublicConnectionList(uid, "following", cookieHeader, csrfToken)

            if (followersRes != null && followingRes != null) {
                return parsedProfile.copy(
                    followers = followersRes,
                    following = followingRes,
                    listAccessStatus = ListAccessStatus.FULL_DATA
                )
            }
        }

        return parsedProfile.copy(listAccessStatus = ListAccessStatus.LOGIN_REQUIRED_FOR_LISTS)
    }

    private fun fetchViaWebProfileApi(
        username: String,
        cookieHeader: String?,
        csrfToken: String?
    ): Pair<PublicLookupResult?, Int> {
        val url = "https://www.instagram.com/api/v1/users/web_profile_info/?username=$username"
        val (code, body) = executeSecureHttpsGet(
            urlStr = url,
            referer = "https://www.instagram.com/$username/",
            accept = "application/json",
            cookieHeader = cookieHeader,
            csrfToken = csrfToken
        )
        if (code != 200 || body.isNullOrBlank()) {
            return null to code
        }
        return parseWebProfileJson(username, body) to code
    }

    private fun fetchViaHtmlPage(
        username: String,
        cookieHeader: String?
    ): Pair<PublicLookupResult?, Int> {
        val url = "https://www.instagram.com/$username/"
        val (code, body) = executeSecureHttpsGet(
            urlStr = url,
            referer = "https://www.instagram.com/",
            accept = "text/html,application/xhtml+xml",
            cookieHeader = cookieHeader,
            csrfToken = null
        )
        if (code != 200 || body.isNullOrBlank()) {
            return null to code
        }
        return parsePublicHtmlProfile(username, body) to code
    }

    private fun fetchPublicConnectionList(
        userId: String,
        kind: String,
        cookieHeader: String?,
        csrfToken: String?
    ): Map<String, Pair<String, Long>>? {
        val collected = linkedMapOf<String, Pair<String, Long>>()
        val nowSec = System.currentTimeMillis() / 1000L
        var maxId: String? = null
        var page = 0

        while (page < MAX_PUBLIC_LIST_PAGES) {
            page++
            var url = "https://www.instagram.com/api/v1/friendships/$userId/$kind/?count=50"
            if (!maxId.isNullOrBlank()) {
                url += "&max_id=$maxId"
            }

            val (code, body) = executeSecureHttpsGet(
                urlStr = url,
                referer = "https://www.instagram.com/",
                accept = "application/json",
                cookieHeader = cookieHeader,
                csrfToken = csrfToken
            )
            if (code != 200 || body.isNullOrBlank()) return null

            val root = runCatching { JSONObject(body) }.getOrNull() ?: return null
            if (root.optBoolean("require_login", false) || root.has("challenge")) return null
            val usersArr = root.optJSONArray("users") ?: return null

            for (i in 0 until usersArr.length()) {
                val u = usersArr.optJSONObject(i) ?: continue
                val handle = InstagramExportParser.normalizeUsername(u.optString("username", "")) ?: continue
                val fullName = u.optString("full_name", "").trim()
                collected[handle] = fullName to nowSec
            }

            val nextMax = root.optString("next_max_id", "")
            if (nextMax.isBlank()) break
            maxId = nextMax
            runCatching { Thread.sleep(600L) }
        }
        return collected
    }

    internal fun parseWebProfileJson(fallbackUsername: String, jsonBody: String): PublicLookupResult? {
        return try {
            val root = JSONObject(jsonBody)
            val user = root.optJSONObject("data")?.optJSONObject("user") ?: return null
            val username = InstagramExportParser.normalizeUsername(user.optString("username", fallbackUsername))
                ?: fallbackUsername
            val fullName = user.optString("full_name", "").trim()
            val userId = user.optString("id", "").ifBlank { null }
            val isPrivate = if (user.has("is_private")) user.optBoolean("is_private") else null
            val followersCount = user.optJSONObject("edge_followed_by")?.optInt("count", -1)?.takeIf { it >= 0 }
            val followingCount = user.optJSONObject("edge_follow")?.optInt("count", -1)?.takeIf { it >= 0 }

            PublicLookupResult(
                username = username,
                displayName = fullName,
                userId = userId,
                isPrivate = isPrivate,
                followersCount = followersCount,
                followingCount = followingCount,
                listAccessStatus = ListAccessStatus.LOGIN_REQUIRED_FOR_LISTS
            )
        } catch (_: Exception) {
            null
        }
    }

    internal fun parsePublicHtmlProfile(username: String, html: String): PublicLookupResult? {
        val jsonFollowers = JSON_FOLLOWED_BY_REGEX.find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val jsonFollowing = JSON_FOLLOW_REGEX.find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val jsonPrivate = JSON_IS_PRIVATE_REGEX.find(html)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull()
        val jsonUserId = JSON_USER_ID_REGEX.find(html)?.groupValues?.getOrNull(1)

        val ogDesc = OG_DESC_REGEX.find(html)?.groupValues?.getOrNull(1)?.let(::decodeBasicHtmlEntities).orEmpty()
        val ogTitle = OG_TITLE_REGEX.find(html)?.groupValues?.getOrNull(1)?.let(::decodeBasicHtmlEntities).orEmpty()

        val metaFollowers = FOLLOWERS_COUNT_META_REGEX.find(ogDesc)?.groupValues?.getOrNull(1)?.let(::parseCompactCount)
        val metaFollowing = FOLLOWING_COUNT_META_REGEX.find(ogDesc)?.groupValues?.getOrNull(1)?.let(::parseCompactCount)

        val displayName = ogTitle.substringBefore("(@").trim().takeIf {
            it.isNotEmpty() && !it.equals("Instagram", ignoreCase = true)
        }.orEmpty()

        val finalFollowers = jsonFollowers ?: metaFollowers
        val finalFollowing = jsonFollowing ?: metaFollowing
        if (finalFollowers == null && finalFollowing == null && jsonPrivate == null) {
            return null
        }

        return PublicLookupResult(
            username = username,
            displayName = displayName,
            userId = jsonUserId,
            isPrivate = jsonPrivate,
            followersCount = finalFollowers,
            followingCount = finalFollowing,
            listAccessStatus = if (jsonPrivate == true) {
                ListAccessStatus.PRIVATE_ACCOUNT
            } else {
                ListAccessStatus.LOGIN_REQUIRED_FOR_LISTS
            }
        )
    }

    internal fun parseCompactCount(raw: String): Int? {
        val cleaned = raw.trim().uppercase(Locale.ROOT)
        if (cleaned.isEmpty()) return null

        val multiplier = when {
            cleaned.endsWith("MN") || cleaned.endsWith("M") -> 1_000_000.0
            cleaned.endsWith("BN") || cleaned.endsWith("B") -> 1_000_000_000.0
            cleaned.endsWith("BIN") -> 1_000.0
            cleaned.endsWith("K") -> 1_000.0
            else -> 1.0
        }

        val numPart = cleaned
            .replace("MN", "")
            .replace("BN", "")
            .replace("BIN", "")
            .replace("M", "")
            .replace("B", "")
            .replace("K", "")
            .trim()

        if (numPart.isEmpty()) return null

        val normalizedNumber = if (multiplier > 1.0) {
            numPart.replace(',', '.')
        } else {
            numPart.replace(".", "").replace(",", "")
        }

        return normalizedNumber.toDoubleOrNull()?.let { (it * multiplier).toInt() }
    }

    private fun decodeBasicHtmlEntities(raw: String): String {
        return raw
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&#x27;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }

    private fun executeSecureHttpsGet(
        urlStr: String,
        referer: String,
        accept: String,
        cookieHeader: String?,
        csrfToken: String?
    ): Pair<Int, String?> {
        if (!SecurityManager.isAllowedWebSyncUrl(urlStr)) return -1 to null

        var conn: HttpsURLConnection? = null
        return try {
            val url = URL(urlStr)
            conn = (url.openConnection() as? HttpsURLConnection) ?: return -1 to null
            conn.requestMethod = "GET"
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            conn.instanceFollowRedirects = false
            conn.setRequestProperty("User-Agent", MOBILE_USER_AGENT)
            conn.setRequestProperty("Accept", accept)
            conn.setRequestProperty("Referer", referer)
            conn.setRequestProperty("x-ig-app-id", INSTAGRAM_APP_ID)
            conn.setRequestProperty("x-requested-with", "XMLHttpRequest")
            if (!cookieHeader.isNullOrBlank()) {
                conn.setRequestProperty("Cookie", cookieHeader)
            }
            if (!csrfToken.isNullOrBlank()) {
                conn.setRequestProperty("x-csrftoken", csrfToken)
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.use { input ->
                val buffer = ByteArray(8192)
                val out = ByteArrayOutputStream()
                var total = 0
                while (true) {
                    val read = input.read(buffer)
                    if (read <= 0) break
                    total += read
                    if (total > MAX_RESPONSE_BYTES) break
                    out.write(buffer, 0, read)
                }
                String(out.toByteArray(), StandardCharsets.UTF_8)
            }
            code to body
        } catch (_: Exception) {
            HttpURLConnection.HTTP_UNAVAILABLE to null
        } finally {
            conn?.disconnect()
        }
    }

    private fun readSafeInstagramCookieHeader(): String? {
        return runCatching {
            CookieManager.getInstance().getCookie("https://www.instagram.com")
        }.getOrNull()
    }

    private fun extractCookieValue(rawCookies: String?, name: String): String? {
        if (rawCookies.isNullOrBlank()) return null
        for (part in rawCookies.split(";")) {
            val trimmed = part.trim()
            val eqIdx = trimmed.indexOf('=')
            if (eqIdx > 0 && trimmed.substring(0, eqIdx).trim() == name) {
                return trimmed.substring(eqIdx + 1).trim()
            }
        }
        return null
    }
}
