package com.installow.app.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.compose.runtime.Immutable
import com.installow.app.model.AppLanguage
import com.installow.app.ui.i18n.stringsFor
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

@Immutable
data class SecurityCheckItem(
    val id: String,
    val title: String,
    val detail: String,
    val passed: Boolean
)

object SecurityManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "installow_master_key_v1"
    private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_BITS = 128
    private const val FORMAT_VERSION: Byte = 1

    private val ALLOWED_ROOT_DOMAINS = setOf(
        "instagram.com",
        "facebook.com",
        "meta.com"
    )

    private val INTENT_FALLBACK_REGEX = Regex("""(?:^|[;#])S\.browser_fallback_url=([^;]+)""")
    private val CSV_CONTROL_REGEX = Regex("""[\r\n\t,]+""")
    private val FORMULA_PREFIX_CHARS = setOf('=', '+', '-', '@', '|', '%')
    private val secureRandom = SecureRandom()

    fun generateSessionToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun constantTimeEquals(a: String?, b: String?): Boolean {
        if (a.isNullOrEmpty() || b.isNullOrEmpty()) return false
        val bytesA = a.toByteArray(StandardCharsets.UTF_8)
        val bytesB = b.toByteArray(StandardCharsets.UTF_8)
        return MessageDigest.isEqual(bytesA, bytesB)
    }

    fun isAllowedWebSyncUrl(rawUrl: String?): Boolean {
        if (rawUrl.isNullOrBlank()) return false
        val trimmed = rawUrl.trim()
        val uri = try {
            URI(trimmed)
        } catch (_: Exception) {
            return false
        }

        val scheme = uri.scheme?.lowercase() ?: return false
        if (scheme != "https") return false
        if (!uri.userInfo.isNullOrEmpty()) return false
        if (uri.port != -1 && uri.port != 443) return false

        val host = uri.host?.lowercase()?.trimEnd('.') ?: return false
        if (host.isEmpty()) return false

        return host in ALLOWED_ROOT_DOMAINS ||
            ALLOWED_ROOT_DOMAINS.any { root -> host.endsWith(".$root") }
    }

    fun extractSafeIntentFallbackUrl(intentUrl: String?): String? {
        if (intentUrl.isNullOrBlank()) return null
        val trimmed = intentUrl.trim()
        if (!trimmed.startsWith("intent:", ignoreCase = true)) return null

        val match = INTENT_FALLBACK_REGEX.find(trimmed) ?: return null
        val encoded = match.groupValues.getOrNull(1) ?: return null
        val decoded = runCatching {
            URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
        }.getOrNull() ?: return null

        return if (isAllowedWebSyncUrl(decoded)) decoded else null
    }

    fun sanitizeCsvCell(raw: String): String {
        val cleaned = raw.replace(CSV_CONTROL_REGEX, " ").trim()
        if (cleaned.isEmpty()) return ""
        return if (cleaned[0] in FORMULA_PREFIX_CHARS) {
            "'$cleaned"
        } else {
            cleaned
        }
    }

    fun isSafeZipEntryName(name: String?): Boolean {
        if (name.isNullOrBlank()) return false
        if (name.contains('\u0000')) return false
        val normalized = name.replace('\\', '/')
        if (normalized.startsWith("/")) return false
        if (Regex("""^[A-Za-z]:""").containsMatchIn(normalized)) return false
        val segments = normalized.split('/')
        return segments.none { it == ".." || it == "." }
    }

    fun encryptBytes(plainBytes: ByteArray): ByteArray {
        val key = getOrCreateAesKey()
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val cipherText = cipher.doFinal(plainBytes)
        val output = ByteArray(2 + iv.size + cipherText.size)
        output[0] = FORMAT_VERSION
        output[1] = iv.size.toByte()
        System.arraycopy(iv, 0, output, 2, iv.size)
        System.arraycopy(cipherText, 0, output, 2 + iv.size, cipherText.size)
        return output
    }

    fun decryptBytes(cipherBytes: ByteArray): ByteArray {
        require(cipherBytes.size > 4) { "Invalid encrypted payload" }
        require(cipherBytes[0] == FORMAT_VERSION) { "Unsupported encryption format version" }
        val ivLen = cipherBytes[1].toInt() and 0xFF
        require(ivLen in 12..32 && cipherBytes.size > 2 + ivLen) { "Invalid IV length" }
        val iv = cipherBytes.copyOfRange(2, 2 + ivLen)
        val payload = cipherBytes.copyOfRange(2 + ivLen, cipherBytes.size)
        val key = getOrCreateAesKey()
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(payload)
    }

    private fun getOrCreateAesKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existing = (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
        if (existing != null) return existing

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun clearWebViewSessionData(context: Context) {
        runCatching {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.removeSessionCookies(null)
            cookieManager.flush()
        }
        runCatching {
            WebStorage.getInstance().deleteAllData()
        }
        runCatching {
            context.cacheDir?.deleteRecursively()
        }
    }

    fun runSecuritySelfChecks(language: AppLanguage = AppLanguage.TR): List<SecurityCheckItem> {
        val strings = stringsFor(language)
        val checks = mutableListOf<SecurityCheckItem>()

        // 1. HTTPS & Domain Whitelist doğrulaması
        val urlPass = isAllowedWebSyncUrl("https://www.instagram.com/accounts/login/") &&
            isAllowedWebSyncUrl("https://l.instagram.com/?u=test") &&
            !isAllowedWebSyncUrl("http://www.instagram.com/") &&
            !isAllowedWebSyncUrl("https://evil-instagram.com/login") &&
            !isAllowedWebSyncUrl("https://instagram.com@phishing.site/") &&
            !isAllowedWebSyncUrl("javascript:alert(1)") &&
            !isAllowedWebSyncUrl("file:///etc/hosts")
        checks += SecurityCheckItem(
            id = "url_whitelist",
            title = strings.checkUrlTitle,
            detail = if (urlPass) strings.checkUrlPass else strings.checkUrlFail,
            passed = urlPass
        )

        // 2. Intent Fallback koruması
        val safeIntent = "intent://instagram.com/#Intent;S.browser_fallback_url=https%3A%2F%2Fwww.instagram.com%2Faccounts%2Flogin%2F;end"
        val evilIntent = "intent://evil/#Intent;S.browser_fallback_url=https%3A%2F%2Fevil-instagram.com%2Fsteal;end"
        val intentPass = extractSafeIntentFallbackUrl(safeIntent) == "https://www.instagram.com/accounts/login/" &&
            extractSafeIntentFallbackUrl(evilIntent) == null
        checks += SecurityCheckItem(
            id = "intent_guard",
            title = strings.checkIntentTitle,
            detail = if (intentPass) strings.checkIntentPass else strings.checkIntentFail,
            passed = intentPass
        )

        // 3. Zip Slip & Path Traversal koruması
        val zipPass = isSafeZipEntryName("connections/followers_and_following/followers_1.json") &&
            !isSafeZipEntryName("../../system/build.prop") &&
            !isSafeZipEntryName("..\\..\\evil.json") &&
            !isSafeZipEntryName("/etc/passwd")
        checks += SecurityCheckItem(
            id = "zip_slip",
            title = strings.checkZipTitle,
            detail = if (zipPass) strings.checkZipPass else strings.checkZipFail,
            passed = zipPass
        )

        // 4. CSV Formula Injection koruması
        val csvPass = sanitizeCsvCell("=CMD|'/C calc'!A0").startsWith("'=CMD") &&
            sanitizeCsvCell("+1+1").startsWith("'+") &&
            sanitizeCsvCell("@SUM(1,2)").startsWith("'@") &&
            !sanitizeCsvCell("normal_user\r\n=evil").contains('\n')
        checks += SecurityCheckItem(
            id = "csv_injection",
            title = strings.checkCsvTitle,
            detail = if (csvPass) strings.checkCsvPass else strings.checkCsvFail,
            passed = csvPass
        )

        // 5. Kriptografik Nonce Bridge doğrulaması
        val t1 = generateSessionToken()
        val t2 = generateSessionToken()
        val tokenPass = t1.length == 64 && t1 != t2 &&
            constantTimeEquals(t1, t1) &&
            !constantTimeEquals(t1, t2) &&
            !constantTimeEquals(t1, null)
        checks += SecurityCheckItem(
            id = "bridge_nonce",
            title = strings.checkNonceTitle,
            detail = if (tokenPass) strings.checkNoncePass else strings.checkNonceFail,
            passed = tokenPass
        )

        // 6. AES-256-GCM Şifreleme Testi
        val aesResult = runCatching {
            val sample = "installow_security_probe_v132".toByteArray(StandardCharsets.UTF_8)
            val encrypted = encryptBytes(sample)
            val decrypted = decryptBytes(encrypted)
            decrypted.contentEquals(sample) && !encrypted.contentEquals(sample)
        }
        val aesPass = aesResult.getOrDefault(false)
        checks += SecurityCheckItem(
            id = "aes_gcm",
            title = strings.checkAesTitle,
            detail = if (aesPass) {
                strings.checkAesPass
            } else {
                "${strings.checkAesFailPrefix}${aesResult.exceptionOrNull()?.localizedMessage ?: strings.unknownErrorText}"
            },
            passed = aesPass
        )

        return checks
    }
}
