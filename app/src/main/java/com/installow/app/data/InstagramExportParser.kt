package com.installow.app.data

import com.installow.app.model.ParsedExportData
import com.installow.app.model.ParsedUserEntry
import com.installow.app.util.SecurityManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PushbackInputStream
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.zip.ZipInputStream

object InstagramExportParser {

    private const val MAX_ZIP_ENTRIES = 2500
    private const val MAX_SINGLE_ENTRY_BYTES = 20 * 1024 * 1024
    private const val MAX_TOTAL_EXTRACTED_BYTES = 80L * 1024L * 1024L
    private const val MAX_USERS_PER_BUCKET = 200_000

    private enum class TargetBucket {
        FOLLOWERS,
        FOLLOWING,
        PENDING_REQUESTS,
        CLOSE_FRIENDS,
        RECENTLY_UNFOLLOWED,
        UNKNOWN
    }

    private val IG_HANDLE_URL_REGEX =
        Regex("""https?://(?:www\.)?instagram\.com/(?:_u/)?([A-Za-z0-9._]{1,30})/?(?:\?.*)?$""", RegexOption.IGNORE_CASE)

    private val HTML_LINK_REGEX =
        Regex("""href=["']https?://(?:www\.)?instagram\.com/(?:_u/)?([A-Za-z0-9._]{1,30})/?["'][^>]*>([^<]*)</a>""", RegexOption.IGNORE_CASE)

    private val RESERVED_PATH_SEGMENTS = setOf(
        "p", "reel", "reels", "stories", "explore", "accounts", "about", "developer",
        "legal", "privacy", "terms", "direct", "tv", "directory", "_u", "web"
    )

    fun parseStreamAuto(fileName: String, rawStream: InputStream): ParsedExportData {
        val pushback = PushbackInputStream(BufferedInputStream(rawStream), 4)
        val header = ByteArray(4)
        val readCount = pushback.read(header)
        if (readCount <= 0) return ParsedExportData()
        pushback.unread(header, 0, readCount)

        val hasPkSignature = readCount >= 2 && header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
        val isZip = fileName.endsWith(".zip", ignoreCase = true) || hasPkSignature

        return if (isZip) {
            parseZipStream(pushback)
        } else {
            val content = readBoundedUtf8(pushback, maxBytes = MAX_SINGLE_ENTRY_BYTES) ?: return ParsedExportData()
            parseSingleFile(fileName, content)
        }
    }

    fun parseZipStream(inputStream: InputStream): ParsedExportData {
        var combined = ParsedExportData()
        var entryCount = 0
        var totalExtractedBytes = 0L

        ZipInputStream(inputStream).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                entryCount++
                if (entryCount > MAX_ZIP_ENTRIES) break

                if (!entry.isDirectory && SecurityManager.isSafeZipEntryName(entry.name)) {
                    val lower = entry.name.lowercase(Locale.ROOT)
                    val isRelevantExtension = lower.endsWith(".json") || lower.endsWith(".html")
                    val bucket = detectBucketFromPath(lower)
                    if (isRelevantExtension && bucket != TargetBucket.UNKNOWN) {
                        val remainingAllowed = (MAX_TOTAL_EXTRACTED_BYTES - totalExtractedBytes)
                            .coerceAtMost(MAX_SINGLE_ENTRY_BYTES.toLong())
                            .toInt()
                        if (remainingAllowed <= 0) break

                        val rawBytes = readBoundedBytes(zis, maxBytes = remainingAllowed)
                        if (rawBytes != null) {
                            totalExtractedBytes += rawBytes.size
                            val content = String(rawBytes, StandardCharsets.UTF_8)
                            val single = parseSingleFile(entry.name, content)
                            combined = merge(combined, single)
                        }
                    }
                }
                zis.closeEntry()
                if (totalExtractedBytes >= MAX_TOTAL_EXTRACTED_BYTES) break
                entry = zis.nextEntry
            }
        }
        return combined
    }

    fun parseSingleFile(fileName: String, rawContent: String): ParsedExportData {
        val lowerName = fileName.lowercase(Locale.ROOT)
        val trimmed = rawContent.trimStart('\uFEFF', ' ', '\n', '\r', '\t')
        if (trimmed.isEmpty()) return ParsedExportData()

        return if (lowerName.endsWith(".html") || trimmed.startsWith("<")) {
            parseHtmlContent(fileName, rawContent)
        } else {
            parseJsonContent(fileName, trimmed)
        }
    }

    fun merge(a: ParsedExportData, b: ParsedExportData): ParsedExportData {
        val mergedFollowers = LinkedHashMap(a.followers)
        b.followers.forEach { (k, v) ->
            val prev = mergedFollowers[k]
            mergedFollowers[k] = if (prev == null || v.timestampSec > prev.timestampSec) v else prev
        }

        val mergedFollowing = LinkedHashMap(a.following)
        b.following.forEach { (k, v) ->
            val prev = mergedFollowing[k]
            mergedFollowing[k] = if (prev == null || v.timestampSec > prev.timestampSec) v else prev
        }

        val mergedPending = LinkedHashMap(a.pendingRequests)
        b.pendingRequests.forEach { (k, v) ->
            val prev = mergedPending[k]
            mergedPending[k] = if (prev == null || v.timestampSec > prev.timestampSec) v else prev
        }

        val mergedUnfollowed = LinkedHashMap(a.recentlyUnfollowed)
        b.recentlyUnfollowed.forEach { (k, v) ->
            val prev = mergedUnfollowed[k]
            mergedUnfollowed[k] = if (prev == null || v.timestampSec > prev.timestampSec) v else prev
        }

        return ParsedExportData(
            followers = mergedFollowers,
            following = mergedFollowing,
            pendingRequests = mergedPending,
            closeFriends = a.closeFriends + b.closeFriends,
            recentlyUnfollowed = mergedUnfollowed,
            sourceFilesFound = (a.sourceFilesFound + b.sourceFilesFound).distinct()
        )
    }

    fun parseJsonContent(fileName: String, content: String): ParsedExportData {
        val bucketHint = detectBucketFromPath(fileName.lowercase(Locale.ROOT))
        val followers = linkedMapOf<String, ParsedUserEntry>()
        val following = linkedMapOf<String, ParsedUserEntry>()
        val pending = linkedMapOf<String, ParsedUserEntry>()
        val closeFriends = linkedSetOf<String>()
        val recentlyUnfollowed = linkedMapOf<String, ParsedUserEntry>()

        when {
            content.startsWith("[") -> {
                val arr = JSONArray(content)
                val entries = extractEntriesFromArray(arr)
                when (bucketHint) {
                    TargetBucket.FOLLOWING -> entries.forEach { following[it.username] = it }
                    TargetBucket.PENDING_REQUESTS -> entries.forEach { pending[it.username] = it }
                    TargetBucket.CLOSE_FRIENDS -> entries.forEach { closeFriends.add(it.username) }
                    TargetBucket.RECENTLY_UNFOLLOWED -> entries.forEach { recentlyUnfollowed[it.username] = it }
                    else -> entries.forEach { followers[it.username] = it }
                }
            }
            content.startsWith("{") -> {
                val root = JSONObject(content)
                val keys = root.keys().asSequence().toList()
                var matchedKnownKey = false

                for (key in keys) {
                    val arr = root.optJSONArray(key) ?: continue
                    val entries = extractEntriesFromArray(arr)
                    val lowerKey = key.lowercase(Locale.ROOT)
                    when {
                        "following" in lowerKey && "hashtag" !in lowerKey -> {
                            matchedKnownKey = true
                            entries.forEach { following[it.username] = it }
                        }
                        "follower" in lowerKey -> {
                            matchedKnownKey = true
                            entries.forEach { followers[it.username] = it }
                        }
                        "follow_requests" in lowerKey || "pending" in lowerKey || "sent" in lowerKey -> {
                            matchedKnownKey = true
                            entries.forEach { pending[it.username] = it }
                        }
                        "close_friends" in lowerKey -> {
                            matchedKnownKey = true
                            entries.forEach { closeFriends.add(it.username) }
                        }
                        "unfollowed" in lowerKey -> {
                            matchedKnownKey = true
                            entries.forEach { recentlyUnfollowed[it.username] = it }
                        }
                    }
                }

                if (!matchedKnownKey) {
                    for (key in keys) {
                        val arr = root.optJSONArray(key) ?: continue
                        val entries = extractEntriesFromArray(arr)
                        when (bucketHint) {
                            TargetBucket.FOLLOWING -> entries.forEach { following[it.username] = it }
                            TargetBucket.PENDING_REQUESTS -> entries.forEach { pending[it.username] = it }
                            TargetBucket.CLOSE_FRIENDS -> entries.forEach { closeFriends.add(it.username) }
                            TargetBucket.RECENTLY_UNFOLLOWED -> entries.forEach { recentlyUnfollowed[it.username] = it }
                            else -> entries.forEach { followers[it.username] = it }
                        }
                    }
                }
            }
        }

        val foundAny = followers.isNotEmpty() || following.isNotEmpty() ||
            pending.isNotEmpty() || closeFriends.isNotEmpty() || recentlyUnfollowed.isNotEmpty()

        return ParsedExportData(
            followers = followers,
            following = following,
            pendingRequests = pending,
            closeFriends = closeFriends,
            recentlyUnfollowed = recentlyUnfollowed,
            sourceFilesFound = if (foundAny) listOf(fileName.substringAfterLast('/')) else emptyList()
        )
    }

    fun parseHtmlContent(fileName: String, content: String): ParsedExportData {
        val bucket = detectBucketFromPath(fileName.lowercase(Locale.ROOT))
        val parsed = linkedMapOf<String, ParsedUserEntry>()

        for (match in HTML_LINK_REGEX.findAll(content)) {
            if (parsed.size >= MAX_USERS_PER_BUCKET) break
            val handle = normalizeUsername(match.groupValues[1]) ?: continue
            if (handle in RESERVED_PATH_SEGMENTS) continue
            val linkText = fixInstagramEncoding(match.groupValues[2].trim())
            val displayName = if (linkText.equals(handle, ignoreCase = true)) "" else linkText
            parsed[handle] = ParsedUserEntry(
                username = handle,
                displayName = displayName,
                profileUrl = "https://www.instagram.com/$handle/",
                timestampSec = 0L
            )
        }

        if (parsed.isEmpty()) return ParsedExportData()

        val label = listOf(fileName.substringAfterLast('/'))
        return when (bucket) {
            TargetBucket.FOLLOWING -> ParsedExportData(following = parsed, sourceFilesFound = label)
            TargetBucket.PENDING_REQUESTS -> ParsedExportData(pendingRequests = parsed, sourceFilesFound = label)
            TargetBucket.CLOSE_FRIENDS -> ParsedExportData(closeFriends = parsed.keys, sourceFilesFound = label)
            TargetBucket.RECENTLY_UNFOLLOWED -> ParsedExportData(recentlyUnfollowed = parsed, sourceFilesFound = label)
            else -> ParsedExportData(followers = parsed, sourceFilesFound = label)
        }
    }

    private fun extractEntriesFromArray(arr: JSONArray): List<ParsedUserEntry> {
        val cap = arr.length().coerceAtMost(MAX_USERS_PER_BUCKET)
        val result = ArrayList<ParsedUserEntry>(cap)
        for (i in 0 until cap) {
            val item = arr.optJSONObject(i) ?: continue
            val parsed = extractEntryFromObject(item)
            if (parsed != null) {
                result.add(parsed)
            }
        }
        return result
    }

    private fun extractEntryFromObject(obj: JSONObject): ParsedUserEntry? {
        val titleRaw = fixInstagramEncoding(obj.optString("title", "").trim())
        val stringList = obj.optJSONArray("string_list_data")
        val firstMeta = if (stringList != null && stringList.length() > 0) {
            stringList.optJSONObject(0)
        } else {
            null
        }

        val valueRaw = firstMeta?.optString("value", "")?.trim().orEmpty()
        val hrefRaw = firstMeta?.optString("href", "")?.trim().orEmpty()
        val timestampSec = firstMeta?.optLong("timestamp", 0L)
            ?: obj.optLong("timestamp", 0L)

        val directUsername = normalizeUsername(obj.optString("username", ""))
        val valueHandle = normalizeUsername(valueRaw)
        val titleHandle = normalizeUsername(titleRaw)
        val hrefHandle = extractHandleFromUrl(hrefRaw)

        val username = directUsername
            ?: valueHandle
            ?: titleHandle
            ?: hrefHandle
            ?: return null

        val displayName = when {
            titleRaw.isNotEmpty() && !titleRaw.equals(username, ignoreCase = true) -> titleRaw
            else -> fixInstagramEncoding(obj.optString("full_name", "").trim())
        }

        val profileUrl = "https://www.instagram.com/$username/"

        return ParsedUserEntry(
            username = username,
            displayName = displayName,
            profileUrl = profileUrl,
            timestampSec = timestampSec
        )
    }

    private fun extractHandleFromUrl(url: String): String? {
        if (url.isBlank()) return null
        val match = IG_HANDLE_URL_REGEX.find(url.trim()) ?: return null
        val candidate = normalizeUsername(match.groupValues[1]) ?: return null
        return if (candidate in RESERVED_PATH_SEGMENTS) null else candidate
    }

    fun normalizeUsername(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.trim().removePrefix("@").lowercase(Locale.ROOT)
        if (cleaned.isEmpty() || cleaned.length > 30) return null
        if (cleaned.contains(' ') || cleaned.contains('/') || cleaned.contains(':')) return null
        val isValid = cleaned.all { it.isLetterOrDigit() || it == '.' || it == '_' }
        return if (isValid && cleaned !in RESERVED_PATH_SEGMENTS) cleaned else null
    }

    /**
     * Instagram JSON dışa aktarmaları UTF-8 baytlarını Latin-1 olarak kodlayabiliyor
     * (örn: "\u00c3\u00bc" -> "ü"). Bu yardımcı fonksiyon yalnızca gerçek UTF-8
     * mojibake desenleri içerdiğinde düzeltme uygular.
     */
    fun fixInstagramEncoding(raw: String): String {
        if (raw.isEmpty()) return raw
        val hasMojibakeMarker = raw.indices.any { idx ->
            val ch = raw[idx].code
            ch in 0x00C2..0x00F4 && idx + 1 < raw.length && raw[idx + 1].code in 0x0080..0x00BF
        }
        if (!hasMojibakeMarker) return raw
        if (raw.any { it.code > 0x00FF }) return raw

        return try {
            val bytes = raw.toByteArray(StandardCharsets.ISO_8859_1)
            val decoded = String(bytes, StandardCharsets.UTF_8)
            if (decoded.contains('\uFFFD')) raw else decoded
        } catch (_: Exception) {
            raw
        }
    }

    private fun detectBucketFromPath(lowerPath: String): TargetBucket {
        val fileOnly = lowerPath.substringAfterLast('/')
        return when {
            "recently_unfollowed" in fileOnly || "unfollowed" in fileOnly -> TargetBucket.RECENTLY_UNFOLLOWED
            "pending_follow_requests" in fileOnly || "recent_follow_requests" in fileOnly -> TargetBucket.PENDING_REQUESTS
            "close_friends" in fileOnly -> TargetBucket.CLOSE_FRIENDS
            "following_hashtags" in fileOnly || "hashtags" in fileOnly -> TargetBucket.UNKNOWN
            "following" in fileOnly -> TargetBucket.FOLLOWING
            "followers" in fileOnly || "follower" in fileOnly -> TargetBucket.FOLLOWERS
            else -> TargetBucket.UNKNOWN
        }
    }

    private fun readBoundedUtf8(input: InputStream, maxBytes: Int): String? {
        val bytes = readBoundedBytes(input, maxBytes) ?: return null
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun readBoundedBytes(input: InputStream, maxBytes: Int): ByteArray? {
        val buffer = ByteArray(8192)
        val out = ByteArrayOutputStream()
        var total = 0
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            total += read
            if (total > maxBytes) return null
            out.write(buffer, 0, read)
        }
        return out.toByteArray()
    }
}
