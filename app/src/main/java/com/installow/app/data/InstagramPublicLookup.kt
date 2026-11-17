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
    private const val SSR_CRAWLER_UA_PRIMARY = "curl/8.4.0"
    private const val SSR_CRAWLER_UA_FALLBACK = "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)"
    private const val MAX_RESPONSE_BYTES = 2 * 1024 * 1024
    private const val MAX_PUBLIC_LIST_PAGES = 12

    private val OG_TITLE_PROP_FIRST = Regex(
        """<meta[^>]+property\s*=\s*["']og:title["'][^>]+content\s*=\s*["']([^"']+)["']""",
        RegexOption.IGNORE_CASE
    )
    private val OG_TITLE_CONTENT_FIRST = Regex(
        """<meta[^>]+content\s*=\s*["']([^"']+)["'][^>]+property\s*=\s*["']og:title["']""",
        RegexOption.IGNORE_CASE
    )
    private val OG_DESC_PROP_FIRST = Regex(
        """<meta[^>]+(?:property\s*=\s*["']og:description["']|name\s*=\s*["']description["'])[^>]+content\s*=\s*["']([^"']+)["']""",
        RegexOption.IGNORE_CASE
    )
    private val OG_DESC_CONTENT_FIRST = Regex(
        """<meta[^>]+content\s*=\s*["']([^"']+)["'][^>]+(?:property\s*=\s*["']og:description["']|name\s*=\s*["']description["'])""",
        RegexOption.IGNORE_CASE
    )
    private val HTML_TITLE_REGEX = Regex(
        """<title[^>]*>(.*?)</title>""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )
    private val FOLLOWERS_COUNT_META_REGEX = Regex(
        """([\d.,]+\s*(?:[KMBkmb]|Mn|Bn|Bin|Mio|Tsd)?)\s*(?:Followers|Follower|Takip[çc]i|abonn[ée]s|seguidores)""",
        RegexOption.IGNORE_CASE
    )
    private val FOLLOWING_COUNT_META_REGEX = Regex(
        """([\d.,]+\s*(?:[KMBkmb]|Mn|Bn|Bin|Mio|Tsd)?)\s*(?:Following|Takip(?![çc]i)|Gefolgt|abonnements|seguidos)""",
        RegexOption.IGNORE_CASE
    )
    private val JSON_EDGE_FOLLOWED_BY_REGEX = Regex(""""edge_followed_by"\s*:\s*\{\s*"count"\s*:\s*(\d+)""")
    private val JSON_EDGE_FOLLOW_REGEX = Regex(""""edge_follow"\s*:\s*\{\s*"count"\s*:\s*(\d+)""")
    private val JSON_FOLLOWER_COUNT_REGEX = Regex(""""follower_count"\s*:\s*(\d+)""")
    private val JSON_FOLLOWING_COUNT_REGEX = Regex(""""following_count"\s*:\s*(\d+)""")
    private val JSON_IS_PRIVATE_REGEX = Regex(""""is_private"\s*:\s*(true|false)""")
    private val JSON_USER_ID_REGEX = Regex(""""(?:profilePage_|"profile_id"\s*:\s*"|"pk"\s*:\s*")(\d{5,20})""")
    private val JSON_PROFILE_ID_ALT = Regex(""""profilePage_(\d{5,20})""")
    private val JSON_PROFILE_ID_KEY = Regex(""""profile_id"\s*:\s*"(\d{5,20})"""")
    private val JSON_PK_KEY = Regex(""""pk"\s*:\s*"(\d{5,20})"""")
    private val JSON_FULL_NAME_REGEX = Regex(""""full_name"\s*:\s*"([^"\\]*(?:\\.[^"\\]*)*)"""")
    private val NUMERIC_HEX_ENTITY_REGEX = Regex("""&#x([0-9a-fA-F]{1,6});""")
    private val NUMERIC_DEC_ENTITY_REGEX = Regex("""&#(\d{1,7});""")
    private val UNICODE_ESCAPE_REGEX = Regex("""\\u([0-9a-fA-F]{4})""")

    fun sanitizeUsernameOrUrl(rawInput: String): String {
        val trimmed = rawInput.trim()
        val extracted = if (trimmed.contains("instagram.com/", ignoreCase = true)) {
            trimmed.substringAfter("instagram.com/")
                .substringBefore('?')
                .substringBefore('#')
                .trim('/')
                .substringBefore('/')
        } else {
            trimmed
        }
        return extracted.removePrefix("@").trim().lowercase(Locale.ROOT)
    }

    fun lookupProfile(rawUsername: String): PublicLookupResult {
        val clean = InstagramExportParser.normalizeUsername(sanitizeUsernameOrUrl(rawUsername))
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
        val hasAuthenticatedSession = !extractCookieValue(cookieHeader, "sessionid").isNullOrBlank()

        var parsedProfile: PublicLookupResult? = null

        // 1. Only hit web_profile_info first when we actually hold an authenticated WebView session
        if (hasAuthenticatedSession) {
            val apiRes = fetchViaWebProfileApi(clean, cookieHeader, csrfToken)
            if (apiRes.second == 404) {
                return notFoundResult(clean)
            }
            parsedProfile = apiRes.first
        }

        // 2. Reliable unauthenticated SSR OpenGraph/JSON fetch (works even without login & avoids 429 on web_profile_info)
        if (parsedProfile == null || parsedProfile.followersCount == null) {
            val ssrOutcome = fetchViaSsrHtml(clean)
            when (ssrOutcome.status) {
                HtmlFetchStatus.NOT_FOUND -> if (parsedProfile == null) return notFoundResult(clean)
                HtmlFetchStatus.LOGIN_REDIRECT -> if (parsedProfile == null) {
                    return PublicLookupResult(
                        username = clean,
                        displayName = "",
                        userId = null,
                        isPrivate = null,
                        followersCount = null,
                        followingCount = null,
                        listAccessStatus = ListAccessStatus.LOGIN_REQUIRED_FOR_LISTS
                    )
                }
                HtmlFetchStatus.OK -> {
                    val htmlParsed = ssrOutcome.result
                    if (htmlParsed != null) {
                        parsedProfile = mergeProfiles(parsedProfile, htmlParsed)
                    }
                }
                HtmlFetchStatus.ERROR -> {
                    // Try web_profile_info as secondary fallback if SSR had transient error
                    if (!hasAuthenticatedSession && parsedProfile == null) {
                        val fallbackApi = fetchViaWebProfileApi(clean, null, null)
                        if (fallbackApi.second == 404) return notFoundResult(clean)
                        parsedProfile = fallbackApi.first
                    }
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

        // 3. If account is private, return fetched counts immediately with PRIVATE_ACCOUNT status
        if (parsedProfile.isPrivate == true) {
            return parsedProfile.copy(listAccessStatus = ListAccessStatus.PRIVATE_ACCOUNT)
        }

        // 4. If authenticated via WebView and public, attempt to fetch followers & following lists safely
        val uid = parsedProfile.userId
        if (hasAuthenticatedSession && !uid.isNullOrBlank()) {
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

    private fun notFoundResult(username: String): PublicLookupResult = PublicLookupResult(
        username = username,
        displayName = "",
        userId = null,
        isPrivate = null,
        followersCount = null,
        followingCount = null,
        listAccessStatus = ListAccessStatus.PROFILE_NOT_FOUND
    )

    private fun mergeProfiles(primary: PublicLookupResult?, secondary: PublicLookupResult): PublicLookupResult {
        if (primary == null) return secondary
        return primary.copy(
            displayName = primary.displayName.ifBlank { secondary.displayName },
            followersCount = primary.followersCount ?: secondary.followersCount,
            followingCount = primary.followingCount ?: secondary.followingCount,
            isPrivate = primary.isPrivate ?: secondary.isPrivate,
            userId = primary.userId ?: secondary.userId
        )
    }

    private fun fetchViaWebProfileApi(
        username: String,
        cookieHeader: String?,
        csrfToken: String?
    ): Pair<PublicLookupResult?, Int> {
        val url = "https://www.instagram.com/api/v1/users/web_profile_info/?username=$username"
        val res = executeSecureHttpsGet(
            urlStr = url,
            userAgent = MOBILE_USER_AGENT,
            referer = "https://www.instagram.com/$username/",
            accept = "application/json",
            cookieHeader = cookieHeader,
            csrfToken = csrfToken,
            sendIgHeaders = true
        )
        if (res.code != 200 || res.body.isNullOrBlank()) {
            return null to res.code
        }
        return parseWebProfileJson(username, res.body) to res.code
    }

    private enum class HtmlFetchStatus {
        OK, NOT_FOUND, LOGIN_REDIRECT, ERROR
    }

    private data class HtmlFetchOutcome(
        val status: HtmlFetchStatus,
        val result: PublicLookupResult? = null
    )

    private fun fetchViaSsrHtml(username: String): HtmlFetchOutcome {
        val url = "https://www.instagram.com/$username/"
        val userAgents = listOf(SSR_CRAWLER_UA_PRIMARY, SSR_CRAWLER_UA_FALLBACK)
        var sawLoginWall = false
        var sawEmptyProfileShell = false

        for (ua in userAgents) {
            val res = executeSecureHttpsGet(
                urlStr = url,
                userAgent = ua,
                referer = "https://www.instagram.com/",
                accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                cookieHeader = null,
                csrfToken = null,
                sendIgHeaders = false,
                acceptLanguage = "en-US,en;q=0.9"
            )

            if (res.code == 404) {
                return HtmlFetchOutcome(HtmlFetchStatus.NOT_FOUND)
            }
            if (res.code in 300..399) {
                val loc = res.location.orEmpty()
                if (loc.contains("/accounts/login", ignoreCase = true) ||
                    loc.contains("/challenge", ignoreCase = true)
                ) {
                    sawLoginWall = true
                    continue
                }
            }
            if (res.code != 200 || res.body.isNullOrBlank()) {
                continue
            }

            val parsed = parsePublicHtmlProfile(username, res.body)
            if (parsed != null) {
                return HtmlFetchOutcome(HtmlFetchStatus.OK, parsed)
            }

            val title = HTML_TITLE_REGEX.find(res.body)?.groupValues?.getOrNull(1)?.trim().orEmpty()
            if (title.contains("Login", ignoreCase = true) ||
                title.contains("Giriş", ignoreCase = true)
            ) {
                sawLoginWall = true
            } else if (
                title.equals("Instagram", ignoreCase = true) ||
                res.body.contains("Sorry, this page isn&#039;t available", ignoreCase = true) ||
                res.body.contains("Sorry, this page isn't available", ignoreCase = true)
            ) {
                sawEmptyProfileShell = true
            }
        }

        return when {
            sawEmptyProfileShell && !sawLoginWall -> HtmlFetchOutcome(HtmlFetchStatus.NOT_FOUND)
            sawLoginWall -> HtmlFetchOutcome(HtmlFetchStatus.LOGIN_REDIRECT)
            else -> HtmlFetchOutcome(HtmlFetchStatus.ERROR)
        }
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

            val res = executeSecureHttpsGet(
                urlStr = url,
                userAgent = MOBILE_USER_AGENT,
                referer = "https://www.instagram.com/",
                accept = "application/json",
                cookieHeader = cookieHeader,
                csrfToken = csrfToken,
                sendIgHeaders = true
            )
            if (res.code != 200 || res.body.isNullOrBlank()) {
                return if (collected.isNotEmpty()) collected else null
            }

            val root = runCatching { JSONObject(res.body) }.getOrNull()
                ?: return if (collected.isNotEmpty()) collected else null
            if (root.optBoolean("require_login", false) || root.has("challenge")) {
                return if (collected.isNotEmpty()) collected else null
            }
            val usersArr = root.optJSONArray("users")
                ?: return if (collected.isNotEmpty()) collected else null

            for (i in 0 until usersArr.length()) {
                val u = usersArr.optJSONObject(i) ?: continue
                val handle = InstagramExportParser.normalizeUsername(u.optString("username", "")) ?: continue
                val fullName = u.optString("full_name", "").trim()
                collected[handle] = fullName to nowSec
            }

            val nextMax = root.optString("next_max_id", "")
            if (nextMax.isBlank()) break
            maxId = nextMax
            runCatching { Thread.sleep(450L) }
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
        val jsonFollowers = JSON_FOLLOWER_COUNT_REGEX.find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: JSON_EDGE_FOLLOWED_BY_REGEX.find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val jsonFollowing = JSON_FOLLOWING_COUNT_REGEX.find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: JSON_EDGE_FOLLOW_REGEX.find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val jsonPrivate = JSON_IS_PRIVATE_REGEX.find(html)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull()
        val jsonUserId = JSON_PROFILE_ID_KEY.find(html)?.groupValues?.getOrNull(1)
            ?: JSON_PROFILE_ID_ALT.find(html)?.groupValues?.getOrNull(1)
            ?: JSON_PK_KEY.find(html)?.groupValues?.getOrNull(1)

        val ogDescRaw = OG_DESC_PROP_FIRST.find(html)?.groupValues?.getOrNull(1)
            ?: OG_DESC_CONTENT_FIRST.find(html)?.groupValues?.getOrNull(1)
            ?: ""
        val ogDesc = decodeHtmlAndUnicodeEscapes(ogDescRaw)

        val ogTitleRaw = OG_TITLE_PROP_FIRST.find(html)?.groupValues?.getOrNull(1)
            ?: OG_TITLE_CONTENT_FIRST.find(html)?.groupValues?.getOrNull(1)
            ?: ""
        val ogTitle = decodeHtmlAndUnicodeEscapes(ogTitleRaw)

        val metaFollowers = FOLLOWERS_COUNT_META_REGEX.find(ogDesc)?.groupValues?.getOrNull(1)?.let(::parseCompactCount)
        val metaFollowing = FOLLOWING_COUNT_META_REGEX.find(ogDesc)?.groupValues?.getOrNull(1)?.let(::parseCompactCount)

        val jsonDisplayName = JSON_FULL_NAME_REGEX.find(html)?.groupValues?.getOrNull(1)
            ?.let(::decodeHtmlAndUnicodeEscapes)
            ?.trim()
            .orEmpty()
        val ogDisplayName = ogTitle.substringBefore("(@").trim().takeIf {
            it.isNotEmpty() && !it.equals("Instagram", ignoreCase = true)
        }.orEmpty()
        val displayName = ogDisplayName.ifBlank { jsonDisplayName }

        val finalFollowers = jsonFollowers ?: metaFollowers
        val finalFollowing = jsonFollowing ?: metaFollowing
        if (finalFollowers == null && finalFollowing == null && jsonPrivate == null && jsonUserId == null) {
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
            cleaned.endsWith("MIO") || cleaned.endsWith("MN") || cleaned.endsWith("M") -> 1_000_000.0
            cleaned.endsWith("BN") || cleaned.endsWith("B") -> 1_000_000_000.0
            cleaned.endsWith("TSD") || cleaned.endsWith("BIN") -> 1_000.0
            cleaned.endsWith("K") -> 1_000.0
            else -> 1.0
        }

        val numPart = cleaned
            .replace("MIO", "")
            .replace("TSD", "")
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

    internal fun decodeHtmlAndUnicodeEscapes(raw: String): String {
        if (raw.isEmpty()) return ""
        var text = raw
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&#x27;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
        text = NUMERIC_HEX_ENTITY_REGEX.replace(text) { m ->
            val cp = m.groupValues[1].toIntOrNull(16)
            if (cp != null && cp in 32..0x10FFFF) String(Character.toChars(cp)) else m.value
        }
        text = NUMERIC_DEC_ENTITY_REGEX.replace(text) { m ->
            val cp = m.groupValues[1].toIntOrNull(10)
            if (cp != null && cp in 32..0x10FFFF) String(Character.toChars(cp)) else m.value
        }
        text = UNICODE_ESCAPE_REGEX.replace(text) { m ->
            val cp = m.groupValues[1].toIntOrNull(16)
            if (cp != null) cp.toChar().toString() else m.value
        }
        return text
    }

    private data class HttpFetchResponse(
        val code: Int,
        val body: String?,
        val location: String? = null
    )

    private fun executeSecureHttpsGet(
        urlStr: String,
        userAgent: String,
        referer: String,
        accept: String,
        cookieHeader: String?,
        csrfToken: String?,
        sendIgHeaders: Boolean,
        acceptLanguage: String? = null
    ): HttpFetchResponse {
        if (!SecurityManager.isAllowedWebSyncUrl(urlStr)) return HttpFetchResponse(-1, null)

        var conn: HttpsURLConnection? = null
        return try {
            val url = URL(urlStr)
            conn = (url.openConnection() as? HttpsURLConnection) ?: return HttpFetchResponse(-1, null)
            conn.requestMethod = "GET"
            conn.connectTimeout = 12_000
            conn.readTimeout = 12_000
            conn.instanceFollowRedirects = false
            conn.setRequestProperty("User-Agent", userAgent)
            conn.setRequestProperty("Accept", accept)
            conn.setRequestProperty("Referer", referer)
            if (!acceptLanguage.isNullOrBlank()) {
                conn.setRequestProperty("Accept-Language", acceptLanguage)
            }
            if (sendIgHeaders) {
                conn.setRequestProperty("x-ig-app-id", INSTAGRAM_APP_ID)
                conn.setRequestProperty("x-requested-with", "XMLHttpRequest")
            }
            if (!cookieHeader.isNullOrBlank()) {
                conn.setRequestProperty("Cookie", cookieHeader)
            }
            if (!csrfToken.isNullOrBlank()) {
                conn.setRequestProperty("x-csrftoken", csrfToken)
            }

            val code = conn.responseCode
            val location = conn.getHeaderField("Location")
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
            HttpFetchResponse(code, body, location)
        } catch (_: Exception) {
            HttpFetchResponse(HttpURLConnection.HTTP_UNAVAILABLE, null)
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
