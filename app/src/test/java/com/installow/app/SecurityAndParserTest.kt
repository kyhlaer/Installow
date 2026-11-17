package com.installow.app

import com.installow.app.data.InstagramExportParser
import com.installow.app.data.InstagramPublicLookup
import com.installow.app.data.LocalRepository
import com.installow.app.data.PersistedAppState
import com.installow.app.model.AccountAnnotation
import com.installow.app.model.AccountWorkspace
import com.installow.app.model.AppLanguage
import com.installow.app.model.DataSourceType
import com.installow.app.model.LanguagePreference
import com.installow.app.model.ListAccessStatus
import com.installow.app.model.Snapshot
import com.installow.app.model.ThemePreference
import com.installow.app.ui.i18n.resolveAppLanguage
import com.installow.app.ui.i18n.stringsFor
import com.installow.app.util.SecurityManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SecurityAndParserTest {

    @Test
    fun urlWhitelist_allowsOnlyHttpsMetaAndInstagramDomains() {
        assertTrue(SecurityManager.isAllowedWebSyncUrl("https://www.instagram.com/accounts/login/"))
        assertTrue(SecurityManager.isAllowedWebSyncUrl("https://instagram.com/"))
        assertTrue(SecurityManager.isAllowedWebSyncUrl("https://l.instagram.com/?u=123"))
        assertTrue(SecurityManager.isAllowedWebSyncUrl("https://m.facebook.com/login/"))
        assertTrue(SecurityManager.isAllowedWebSyncUrl("https://accountscenter.meta.com/"))

        assertFalse(SecurityManager.isAllowedWebSyncUrl("http://www.instagram.com/"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl("https://evil-instagram.com/login"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl("https://instagram.com.evil.com/"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl("https://instagram.com@evil.com/"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl("https://www.instagram.com:8443/"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl("javascript:alert(1)"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl("file:///etc/passwd"))
        assertFalse(SecurityManager.isAllowedWebSyncUrl(""))
        assertFalse(SecurityManager.isAllowedWebSyncUrl(null))
    }

    @Test
    fun intentFallback_allowsOnlyWhitelistedHttpsFallback() {
        val safeIntent =
            "intent://instagram.com/#Intent;scheme=https;S.browser_fallback_url=https%3A%2F%2Fwww.instagram.com%2Faccounts%2Flogin%2F;end"
        assertEquals(
            "https://www.instagram.com/accounts/login/",
            SecurityManager.extractSafeIntentFallbackUrl(safeIntent)
        )

        val evilDomainIntent =
            "intent://evil/#Intent;S.browser_fallback_url=https%3A%2F%2Fevil-instagram.com%2Fphish;end"
        assertNull(SecurityManager.extractSafeIntentFallbackUrl(evilDomainIntent))

        val httpDowngradeIntent =
            "intent://instagram.com/#Intent;S.browser_fallback_url=http%3A%2F%2Fwww.instagram.com%2F;end"
        assertNull(SecurityManager.extractSafeIntentFallbackUrl(httpDowngradeIntent))

        val jsIntent =
            "intent://x/#Intent;S.browser_fallback_url=javascript%3Aalert(1);end"
        assertNull(SecurityManager.extractSafeIntentFallbackUrl(jsIntent))
    }

    @Test
    fun zipSlipProtection_blocksTraversalPathsInZip() {
        assertTrue(SecurityManager.isSafeZipEntryName("connections/followers_and_following/followers_1.json"))
        assertFalse(SecurityManager.isSafeZipEntryName("../../followers_1.json"))
        assertFalse(SecurityManager.isSafeZipEntryName("..\\..\\following.json"))
        assertFalse(SecurityManager.isSafeZipEntryName("/etc/passwd"))
        assertFalse(SecurityManager.isSafeZipEntryName("C:\\Windows\\System32\\followers.json"))

        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry("../../followers_1.json"))
            val evilJson = """[{"string_list_data":[{"value":"attacker_user","timestamp":1700000000}]}]"""
            zos.write(evilJson.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            zos.putNextEntry(ZipEntry("connections/followers_and_following/followers_1.json"))
            val legitJson = """[{"string_list_data":[{"value":"gercek_kullanici","timestamp":1700000100}]}]"""
            zos.write(legitJson.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()
        }

        val parsed = InstagramExportParser.parseStreamAuto(
            fileName = "archive.zip",
            rawStream = ByteArrayInputStream(baos.toByteArray())
        )
        assertFalse(parsed.followers.containsKey("attacker_user"))
        assertTrue(parsed.followers.containsKey("gercek_kullanici"))
    }

    @Test
    fun csvInjection_neutralizesDangerousFormulaPrefixes() {
        assertEquals("'=CMD|'/C calc'!A0", SecurityManager.sanitizeCsvCell("=CMD|'/C calc'!A0"))
        assertEquals("'+SUM(1 2)", SecurityManager.sanitizeCsvCell("+SUM(1,2)"))
        assertEquals("'-2+3", SecurityManager.sanitizeCsvCell("-2+3"))
        assertEquals("'@IMPORTXML()", SecurityManager.sanitizeCsvCell("@IMPORTXML()"))
        assertEquals("'|calc", SecurityManager.sanitizeCsvCell("|calc"))
        assertEquals("'%0A", SecurityManager.sanitizeCsvCell("%0A"))
        assertEquals("Normal Ad", SecurityManager.sanitizeCsvCell("Normal\r\nAd"))
    }

    @Test
    fun usernameNormalization_andProfileUrlSanitization_blocksXssAndInjections() {
        assertEquals("valid.user_1", InstagramExportParser.normalizeUsername("@Valid.User_1"))
        assertNull(InstagramExportParser.normalizeUsername("<script>alert(1)</script>"))
        assertNull(InstagramExportParser.normalizeUsername("user/../../admin"))
        assertNull(InstagramExportParser.normalizeUsername("javascript:alert(1)"))

        val jsonWithMaliciousHref = """
            {
              "relationships_following": [
                {
                  "title": "Test User",
                  "string_list_data": [
                    {
                      "href": "javascript:alert(document.cookie)",
                      "value": "hedef_hesap",
                      "timestamp": 1700000200
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val parsed = InstagramExportParser.parseJsonContent("following.json", jsonWithMaliciousHref)
        val entry = parsed.following["hedef_hesap"]
        assertNotNull(entry)
        assertEquals("https://www.instagram.com/hedef_hesap/", entry?.profileUrl)
    }

    @Test
    fun sessionTokens_useHighEntropyAndConstantTimeComparison() {
        val tokenA = SecurityManager.generateSessionToken()
        val tokenB = SecurityManager.generateSessionToken()

        assertEquals(64, tokenA.length)
        assertEquals(64, tokenB.length)
        assertNotEquals(tokenA, tokenB)
        assertTrue(SecurityManager.constantTimeEquals(tokenA, tokenA))
        assertFalse(SecurityManager.constantTimeEquals(tokenA, tokenB))
        assertFalse(SecurityManager.constantTimeEquals(tokenA, ""))
        assertFalse(SecurityManager.constantTimeEquals(tokenA, null))
    }

    @Test
    fun languageResolution_supportsFiveLanguagesAndSystemLocale() {
        assertEquals(AppLanguage.TR, resolveAppLanguage(LanguagePreference.TURKISH, Locale.US))
        assertEquals(AppLanguage.EN, resolveAppLanguage(LanguagePreference.ENGLISH, Locale("tr", "TR")))
        assertEquals(AppLanguage.DE, resolveAppLanguage(LanguagePreference.GERMAN, Locale.US))
        assertEquals(AppLanguage.FR, resolveAppLanguage(LanguagePreference.FRENCH, Locale.US))
        assertEquals(AppLanguage.ES, resolveAppLanguage(LanguagePreference.SPANISH, Locale.US))
        assertEquals(AppLanguage.TR, resolveAppLanguage(LanguagePreference.SYSTEM, Locale("tr", "TR")))
        assertEquals(AppLanguage.DE, resolveAppLanguage(LanguagePreference.SYSTEM, Locale.GERMANY))
        assertEquals(AppLanguage.FR, resolveAppLanguage(LanguagePreference.SYSTEM, Locale.FRANCE))
        assertEquals(AppLanguage.ES, resolveAppLanguage(LanguagePreference.SYSTEM, Locale("es", "ES")))
        assertEquals(AppLanguage.EN, resolveAppLanguage(LanguagePreference.SYSTEM, Locale.US))

        val tr = stringsFor(AppLanguage.TR)
        val en = stringsFor(AppLanguage.EN)
        val de = stringsFor(AppLanguage.DE)
        val fr = stringsFor(AppLanguage.FR)
        val es = stringsFor(AppLanguage.ES)
        assertEquals("Panel", tr.navDashboard)
        assertEquals("Home", en.navDashboard)
        assertEquals("Start", de.navDashboard)
        assertEquals("Accueil", fr.navDashboard)
        assertEquals("Inicio", es.navDashboard)
        assertEquals(tr.presetTags.size, en.presetTags.size)
        assertEquals(tr.presetTags.size, de.presetTags.size)
        assertEquals(tr.presetTags.size, fr.presetTags.size)
        assertEquals(tr.presetTags.size, es.presetTags.size)
    }

    @Test
    fun publicLookup_parsesWebProfileJsonAndSsrHtmlEntities_andSanitizesUrls() {
        assertEquals("kyhlaer", InstagramPublicLookup.sanitizeUsernameOrUrl("  @kyhlaer  "))
        assertEquals("kyhlaer", InstagramPublicLookup.sanitizeUsernameOrUrl("https://www.instagram.com/kyhlaer/?igsh=abc"))

        val sampleJson = """
            {
              "data": {
                "user": {
                  "id": "123456789",
                  "username": "kyhlaer",
                  "full_name": "Kyhlaer Dev",
                  "is_private": false,
                  "edge_followed_by": { "count": 4820 },
                  "edge_follow": { "count": 315 }
                }
              }
            }
        """.trimIndent()

        val parsedJson = InstagramPublicLookup.parseWebProfileJson("kyhlaer", sampleJson)
        assertNotNull(parsedJson)
        assertEquals("kyhlaer", parsedJson?.username)
        assertEquals("Kyhlaer Dev", parsedJson?.displayName)
        assertEquals("123456789", parsedJson?.userId)
        assertEquals(false, parsedJson?.isPrivate)
        assertEquals(4820, parsedJson?.followersCount)
        assertEquals(315, parsedJson?.followingCount)

        val privateSsrHtml = """
            <html><head>
            <meta property="og:title" content="Kerem (&#064;kyhlaer) &#x2022; Instagram photos and videos" />
            <meta property="og:description" content="397 Followers, 416 Following, 3 Posts - See Instagram photos and videos from Kerem (&#064;kyhlaer)" />
            <script>{"profile_id":"8728003276","is_private":true,"follower_count":410,"following_count":373}</script>
            </head></html>
        """.trimIndent()

        val parsedHtml = InstagramPublicLookup.parsePublicHtmlProfile("kyhlaer", privateSsrHtml)
        assertNotNull(parsedHtml)
        assertEquals("Kerem", parsedHtml?.displayName)
        assertEquals("8728003276", parsedHtml?.userId)
        assertEquals(true, parsedHtml?.isPrivate)
        assertEquals(410, parsedHtml?.followersCount)
        assertEquals(373, parsedHtml?.followingCount)
        assertEquals(ListAccessStatus.PRIVATE_ACCOUNT, parsedHtml?.listAccessStatus)

        assertEquals(1_450_000, InstagramPublicLookup.parseCompactCount("1.45M"))
        assertEquals(2_300_000, InstagramPublicLookup.parseCompactCount("2,3 Mn"))
    }

    @Test
    fun localRepository_roundTripsMultiAccountWorkspaces_andMigratesLegacySingleAccount() {
        val ws1 = AccountWorkspace(
            id = "hesap_bir",
            ownerUsername = "hesap_bir",
            displayName = "Birinci Hesap",
            dataSourceType = DataSourceType.OFFICIAL_EXPORT,
            snapshots = listOf(
                Snapshot(
                    id = "snap-1",
                    createdAtMillis = 1700000000000L,
                    sourceLabel = "ZIP",
                    followers = mapOf("a" to 1700000000L),
                    following = mapOf("b" to 1700000100L),
                    reportedFollowersCount = 120,
                    reportedFollowingCount = 85,
                    isPrivateAccount = false
                )
            ),
            annotations = mapOf("b" to AccountAnnotation(isWhitelisted = true, customTag = "Aile", customNote = "Not")),
            reportedFollowersCount = 120,
            reportedFollowingCount = 85,
            isPrivateAccount = false,
            listAccessStatus = ListAccessStatus.FULL_DATA
        )
        val ws2 = AccountWorkspace(
            id = "hesap_iki",
            ownerUsername = "hesap_iki",
            displayName = "İkinci Hesap",
            dataSourceType = DataSourceType.PUBLIC_LOOKUP,
            reportedFollowersCount = 950,
            reportedFollowingCount = 210,
            isPrivateAccount = true,
            listAccessStatus = ListAccessStatus.PRIVATE_ACCOUNT
        )

        val state = PersistedAppState(
            workspaces = listOf(ws1, ws2),
            activeWorkspaceId = "hesap_iki",
            themePreference = ThemePreference.LIGHT,
            languagePreference = LanguagePreference.ENGLISH,
            hideWhitelistedInNonFollowers = false
        )

        val serialized = LocalRepository.serializeStateJson(state).toString()
        val restored = LocalRepository.parseStateJson(serialized)
        assertNotNull(restored)
        assertEquals(2, restored?.workspaces?.size)
        assertEquals("hesap_iki", restored?.activeWorkspaceId)
        assertEquals(ThemePreference.LIGHT, restored?.themePreference)
        assertEquals(LanguagePreference.ENGLISH, restored?.languagePreference)
        assertEquals(false, restored?.hideWhitelistedInNonFollowers)
        assertEquals(true, restored?.workspaces?.last()?.isPrivateAccount)
        assertEquals(ListAccessStatus.PRIVATE_ACCOUNT, restored?.workspaces?.last()?.listAccessStatus)
        assertEquals("Not", restored?.workspaces?.first()?.annotations?.get("b")?.customNote)

        val legacyJson = """
            {
              "activeOwnerUsername": "eski_kullanici",
              "dataSourceType": "WEB_SYNC",
              "themePreference": "DARK",
              "languagePreference": "TURKISH",
              "hideWhitelistedInNonFollowers": true,
              "snapshots": [
                {
                  "id": "legacy-1",
                  "createdAtMillis": 1700000000000,
                  "sourceLabel": "Web",
                  "followers": {"x": 100},
                  "following": {"y": 200}
                }
              ],
              "annotations": {}
            }
        """.trimIndent()
        val migrated = LocalRepository.parseStateJson(legacyJson)
        assertNotNull(migrated)
        assertEquals(1, migrated?.workspaces?.size)
        assertEquals("eski_kullanici", migrated?.workspaces?.first()?.ownerUsername)
        assertEquals(DataSourceType.WEB_SYNC, migrated?.workspaces?.first()?.dataSourceType)
    }
}
