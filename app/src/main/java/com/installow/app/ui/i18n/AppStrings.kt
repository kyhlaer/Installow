package com.installow.app.ui.i18n

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.installow.app.model.AppLanguage
import com.installow.app.model.AppScreen
import com.installow.app.model.DataSourceType
import com.installow.app.model.FilterTab
import com.installow.app.model.LanguagePreference
import com.installow.app.model.RelationCategory
import com.installow.app.model.SortOption
import java.util.Locale

fun resolveAppLanguage(
    preference: LanguagePreference,
    systemLocale: Locale = Locale.getDefault()
): AppLanguage {
    return when (preference) {
        LanguagePreference.TURKISH -> AppLanguage.TR
        LanguagePreference.ENGLISH -> AppLanguage.EN
        LanguagePreference.GERMAN -> AppLanguage.DE
        LanguagePreference.FRENCH -> AppLanguage.FR
        LanguagePreference.SPANISH -> AppLanguage.ES
        LanguagePreference.SYSTEM -> {
            when (systemLocale.language.lowercase(Locale.ROOT)) {
                "tr" -> AppLanguage.TR
                "de" -> AppLanguage.DE
                "fr" -> AppLanguage.FR
                "es" -> AppLanguage.ES
                else -> AppLanguage.EN
            }
        }
    }
}

@Immutable
sealed interface AppStrings {
    val language: AppLanguage
    // Bottom nav & Exit Dialog
    val navDashboard: String
    val navConnections: String
    val navAnalytics: String
    val navData: String
    val navSettings: String
    val exitDialogTitle: String
    val exitDialogMessage: String
    val exitConfirm: String
    val exitCancel: String
    // Categories & Filters & Sorting
    val categoryMutual: String
    val categoryNonFollower: String
    val categoryFan: String
    val categoryPending: String
    val tabNonFollowers: String
    val tabNonFollowersShort: String
    val tabFans: String
    val tabFansShort: String
    val tabMutuals: String
    val tabMutualsShort: String
    val tabAllFollowers: String
    val tabAllFollowersShort: String
    val tabAllFollowing: String
    val tabAllFollowingShort: String
    val tabUnfollowersDiff: String
    val tabUnfollowersDiffShort: String
    val tabPending: String
    val tabPendingShort: String
    val tabWhitelist: String
    val tabWhitelistShort: String
    val sortNewestFirst: String
    val sortOldestFirst: String
    val sortFollowersDesc: String
    val sortFollowersAsc: String
    val sortUsernameAsc: String
    val sortUsernameDesc: String
    val sourceDemo: String
    val sourcePublicLookup: String
    val sourceOfficialExport: String
    val sourceWebSync: String
    val presetTags: List<String>
    // Dashboard Screen
    val searchContentDesc: String
    val uploadDataContentDesc: String
    val metricNonFollowersTitle: String
    val metricNonFollowersHintWhitelist: String
    val metricNonFollowersHintDefault: String
    val metricFansTitle: String
    val metricFansHint: String
    val metricFollowersTitle: String
    val metricFollowingTitle: String
    val metricAllListHint: String
    val followBalanceTitle: String
    val ratioFormat: String
    val donutMutual: String
    val donutNonFollower: String
    val donutFan: String
    val donutMatchLabel: String
    val recentChangesTitle: String
    val lastRecordPrefix: String
    val compareOnNextSync: String
    val simulateAction: String
    val lostFollowersTitle: String
    val newFollowersTitle: String
    val noChangesText: String
    val mutualsRowTitle: String
    val mutualsRowSubtitle: String
    val pendingRowTitle: String
    val pendingRowSubtitle: String
    val whitelistRowTitle: String
    val whitelistRowSubtitle: String
    val smartQueueTitle: String
    val smartQueueDescFormat: String
    val startReviewButton: String
    val unfollowedByMeRowTitle: String
    val accountListNoticePrivateTitle: String
    val accountListNoticePrivateDesc: String
    val accountListNoticeLoginDesc: String
    val accountListNoticeNotFoundDesc: String
    val accountListNoticeErrorDesc: String
    val loadArchiveActionShort: String
    val webSignInActionShort: String
    val retryActionShort: String
    // Account Switcher & Public Lookup
    val accountsSheetTitle: String
    val accountsSheetSubtitle: String
    val addOrLookupAccountTitle: String
    val usernameInputPlaceholder: String
    val lookupAndAddButton: String
    val lookingUpAccountButton: String
    val savedAccountsTitle: String
    val activeBadge: String
    val privateBadge: String
    val publicBadge: String
    val removeAccountDesc: String
    val accountCountsShortFormat: String
    val publicLookupBannerTitle: String
    val publicLookupBannerDesc: String
    val copyUsernameDesc: String
    val usernameCopiedMsgFormat: String
    val allTagsFilterChip: String
    val onlyNotesFilterChip: String
    val onlyCloseFriendsChip: String
    // Data & Settings Headers
    val dataTabTitle: String
    val dataTabSubtitle: String
    val settingsTabTitle: String
    val settingsTabSubtitle: String
    val accountsCardTitle: String
    val accountsCardDescFormat: String
    val manageAccountsButton: String
    // Connections Screen
    val connectionsCountFormat: String
    val sortContentDesc: String
    val shareContentDesc: String
    val shareReportSubjectPrefix: String
    val shareChooserTitle: String
    val searchPlaceholder: String
    val clearContentDesc: String
    val whitelistHidden: String
    val whitelistIncluded: String
    val smartQueueChip: String
    val followerCountZipNotice: String
    val emptyMatchesTitle: String
    val emptyMatchesSubtitle: String
    val openInInstagramDesc: String
    val followerCountSuffix: String
    val smartQueueHeaderFormat: String
    val closeDesc: String
    val openInIgShort: String
    val markedUnfollowedShort: String
    val prevButton: String
    val nextButton: String
    val finishButton: String
    val closeFriendBadge: String
    val whitelistBadgeDesc: String
    val followerCountDetailPrefix: String
    val followedMeDatePrefix: String
    val iFollowedDatePrefix: String
    val pendingDatePrefix: String
    val whitelistSwitchTitle: String
    val whitelistSwitchSubtitle: String
    val tagSectionTitle: String
    val personalNoteLabel: String
    val personalNotePlaceholder: String
    val saveButton: String
    val openInstagramButton: String
    val markUnfollowedButton: String
    // Analytics Screen
    val analyticsTitle: String
    val analyticsSubtitle: String
    val accountStatusLabel: String
    val healthStrong: String
    val healthBalanced: String
    val healthCleanupSuggested: String
    val matchBadgeFormat: String
    val metricReturnRate: String
    val metricNonFollowerShort: String
    val metricFansShort: String
    val last6MonthsTitle: String
    val last6MonthsSubtitle: String
    val legendNewFollowers: String
    val legendNewFollowing: String
    val highlightsTitle: String
    val firstFollowerLabel: String
    val oldestNonFollowerLabel: String
    val longestPendingLabel: String
    val snapshotHistoryFormat: String
    val followersCountUnit: String
    val followingCountUnit: String
    // Data & Settings Screen
    val dataSettingsTitle: String
    val dataSettingsSubtitle: String
    val officialArchiveTitle: String
    val zeroRiskRecommendedBadge: String
    val officialArchiveDesc: String
    val processingFilesText: String
    val selectArchiveButton: String
    val howToExportTitle: String
    val guideStep1: String
    val guideStep2: String
    val guideStep3: String
    val guideStep4: String
    val liveWebSyncTitle: String
    val slowModeBadge: String
    val liveWebSyncDesc: String
    val connectInstagramWebButton: String
    val appearanceTitle: String
    val themeDark: String
    val themeLight: String
    val themeSystem: String
    val languageTitle: String
    val langTurkish: String
    val langEnglish: String
    val langGerman: String
    val langFrench: String
    val langSpanish: String
    val langSystem: String
    val cyberShieldTitle: String
    val cyberShieldActiveBadge: String
    val cyberShieldVerifiedFormat: String
    val shieldLineAes: String
    val shieldLineHttps: String
    val shieldLineBridge: String
    val shieldLineZipCsv: String
    val shieldLineBackup: String
    val runSecurityTestButton: String
    val clearWebCookiesButton: String
    val exportCsvRowTitle: String
    val exportCsvRowSubtitleFormat: String
    val simulateRowTitle: String
    val simulateRowSubtitle: String
    val resetDemoRowTitle: String
    val resetDemoRowSubtitle: String
    val releaseNotesRowTitle: String
    val releaseNotesRowSubtitle: String
    val releaseNotesDialogTitle: String
    val releaseNotesDialogBody: String
    val releaseNotesClose: String
    val footerSignature: String
    // Web Sync Dialog
    val webSyncHeaderTitle: String
    val webSyncHeaderSubtitle: String
    val clearCookiesDesc: String
    val refreshDesc: String
    val webSyncProtectionBanner: String
    val webSyncCooldownStatusFormat: String
    val webSyncInitialStatus: String
    val webSyncSessionDetected: String
    val webSyncCookiesResetStatus: String
    val webSyncSslErrorStatus: String
    val webSyncVerifyingSession: String
    val webSyncFetchingButton: String
    val webSyncCooldownButtonFormat: String
    val webSyncFetchButton: String
    val webSyncProcessingErrorPrefix: String
    val webSyncJsNoSession: String
    val webSyncJsFetchingFormat: String
    val webSyncJsFollowersLabel: String
    val webSyncJsFollowingLabel: String
    val webSyncJsRateLimited: String
    val webSyncJsHttpErrorPrefix: String
    val webSyncJsHttpErrorSuffix: String
    val webSyncJsTakingBreakSuffix: String
    val webSyncJsFollowersDoneBreak: String
    val webSyncJsSaving: String
    val webSyncJsGenericError: String
    // Date & Relative Time & ViewModel Messages
    val dateUnknown: String
    val relJustNow: String
    val relMinutesAgo: String
    val relHoursAgo: String
    val relDaysAgo: String
    val relMonthsAgo: String
    val relYearsAgo: String
    val savedDataFallbackLabel: String
    val securityAuditMsgFormat: String
    val cookiesClearedMsg: String
    val whitelistAddedFormat: String
    val whitelistRemovedFormat: String
    val noteSavedFormat: String
    val markedUnfollowedMsgFormat: String
    val queueNoCandidatesMsg: String
    val queueCompletedMsg: String
    val queueAllReviewedMsg: String
    val demoResetMsg: String
    val simDiffMsgFormat: String
    val simCreatedMsg: String
    val importNotFoundMsg: String
    val archiveOwnerFallback: String
    val archiveSourceLabelFormat: String
    val importSuccessFormat: String
    val importFailedPrefix: String
    val unknownErrorText: String
    val invalidUsernameMsg: String
    val publicLookupFullSuccessFormat: String
    val publicLookupPrivateMsgFormat: String
    val publicLookupCountsOnlyMsgFormat: String
    val publicLookupNotFoundMsgFormat: String
    val publicLookupErrorMsg: String
    val accountSwitchedMsgFormat: String
    val accountRemovedMsgFormat: String
    val cannotRemoveLastAccountMsg: String
    val webSyncSourceLabelFormat: String
    val webSyncSuccessFormat: String
    val csvReportHeaderFormat: String
    val csvAccountSummaryFormat: String
    val csvColumnsHeader: String
    // Security Self Checks
    val checkUrlTitle: String
    val checkUrlPass: String
    val checkUrlFail: String
    val checkIntentTitle: String
    val checkIntentPass: String
    val checkIntentFail: String
    val checkZipTitle: String
    val checkZipPass: String
    val checkZipFail: String
    val checkCsvTitle: String
    val checkCsvPass: String
    val checkCsvFail: String
    val checkNonceTitle: String
    val checkNoncePass: String
    val checkNonceFail: String
    val checkAesTitle: String
    val checkAesPass: String
    val checkAesFailPrefix: String

    fun screenLabel(screen: AppScreen): String = when (screen) {
        AppScreen.DASHBOARD -> navDashboard
        AppScreen.CONNECTIONS -> navConnections
        AppScreen.ANALYTICS -> navAnalytics
        AppScreen.DATA -> navData
        AppScreen.SETTINGS -> navSettings
    }

    fun categoryLabel(category: RelationCategory): String = when (category) {
        RelationCategory.MUTUAL -> categoryMutual
        RelationCategory.NON_FOLLOWER -> categoryNonFollower
        RelationCategory.FAN -> categoryFan
        RelationCategory.PENDING_REQUEST -> categoryPending
    }

    fun tabLabel(tab: FilterTab): String = when (tab) {
        FilterTab.NON_FOLLOWERS -> tabNonFollowers
        FilterTab.FANS -> tabFans
        FilterTab.MUTUALS -> tabMutuals
        FilterTab.ALL_FOLLOWERS -> tabAllFollowers
        FilterTab.ALL_FOLLOWING -> tabAllFollowing
        FilterTab.UNFOLLOWERS_DIFF -> tabUnfollowersDiff
        FilterTab.PENDING -> tabPending
        FilterTab.WHITELIST -> tabWhitelist
    }

    fun tabShortLabel(tab: FilterTab): String = when (tab) {
        FilterTab.NON_FOLLOWERS -> tabNonFollowersShort
        FilterTab.FANS -> tabFansShort
        FilterTab.MUTUALS -> tabMutualsShort
        FilterTab.ALL_FOLLOWERS -> tabAllFollowersShort
        FilterTab.ALL_FOLLOWING -> tabAllFollowingShort
        FilterTab.UNFOLLOWERS_DIFF -> tabUnfollowersDiffShort
        FilterTab.PENDING -> tabPendingShort
        FilterTab.WHITELIST -> tabWhitelistShort
    }

    fun sortLabel(option: SortOption): String = when (option) {
        SortOption.NEWEST_FIRST -> sortNewestFirst
        SortOption.OLDEST_FIRST -> sortOldestFirst
        SortOption.FOLLOWERS_DESC -> sortFollowersDesc
        SortOption.FOLLOWERS_ASC -> sortFollowersAsc
        SortOption.USERNAME_ASC -> sortUsernameAsc
        SortOption.USERNAME_DESC -> sortUsernameDesc
    }

    fun dataSourceLabel(type: DataSourceType): String = when (type) {
        DataSourceType.DEMO -> sourceDemo
        DataSourceType.PUBLIC_LOOKUP -> sourcePublicLookup
        DataSourceType.OFFICIAL_EXPORT -> sourceOfficialExport
        DataSourceType.WEB_SYNC -> sourceWebSync
    }
}

object TrStrings : AppStrings {
    override val language = AppLanguage.TR
    override val navDashboard = "Panel"
    override val navConnections = "Kişiler"
    override val navAnalytics = "Analiz"
    override val navData = "Veri"
    override val navSettings = "Ayarlar"
    override val exitDialogTitle = "Uygulamadan çıkılsın mı?"
    override val exitDialogMessage = "Installow'dan çıkmak istediğine emin misin?"
    override val exitConfirm = "Çık"
    override val exitCancel = "Vazgeç"
    override val categoryMutual = "Karşılıklı"
    override val categoryNonFollower = "Takip Etmiyor"
    override val categoryFan = "Sen Takip Etmiyorsun"
    override val categoryPending = "Bekleyen İstek"
    override val tabNonFollowers = "Beni Takip Etmeyenler"
    override val tabNonFollowersShort = "Takip Etmeyen"
    override val tabFans = "Takip Etmediklerim"
    override val tabFansShort = "Hayranlar"
    override val tabMutuals = "Karşılıklı Takip"
    override val tabMutualsShort = "Karşılıklı"
    override val tabAllFollowers = "Takipçilerim"
    override val tabAllFollowersShort = "Takipçiler"
    override val tabAllFollowing = "Takip Ettiklerim"
    override val tabAllFollowingShort = "Takip Edilen"
    override val tabUnfollowersDiff = "Takipten Çıkanlar"
    override val tabUnfollowersDiffShort = "Çıkanlar"
    override val tabPending = "Bekleyen İstekler"
    override val tabPendingShort = "İstekler"
    override val tabWhitelist = "Beyaz Liste"
    override val tabWhitelistShort = "Beyaz Liste"
    override val sortNewestFirst = "En Yeni Tarih"
    override val sortOldestFirst = "En Eski Tarih"
    override val sortFollowersDesc = "Takipçi Sayısı (Çok → Az)"
    override val sortFollowersAsc = "Takipçi Sayısı (Az → Çok)"
    override val sortUsernameAsc = "Kullanıcı Adı (A → Z)"
    override val sortUsernameDesc = "Kullanıcı Adı (Z → A)"
    override val sourceDemo = "Demo Veri Seti"
    override val sourcePublicLookup = "Herkese Açık Profil"
    override val sourceOfficialExport = "Instagram Resmi Arşiv"
    override val sourceWebSync = "Canlı Web Senkronizasyonu"
    override val presetTags = listOf(
        "Ünlü / Marka",
        "Yakın Arkadaş",
        "Aile",
        "İş / Network",
        "İçerik Üretici",
        "Takip Kontrol"
    )
    override val searchContentDesc = "Ara"
    override val uploadDataContentDesc = "Veri Yükle"
    override val metricNonFollowersTitle = "Beni Takip Etmeyen"
    override val metricNonFollowersHintWhitelist = "Beyaz liste hariç"
    override val metricNonFollowersHintDefault = "Geri takip yok"
    override val metricFansTitle = "Takip Etmediklerim"
    override val metricFansHint = "Seni takip ediyor"
    override val metricFollowersTitle = "Takipçilerim"
    override val metricFollowingTitle = "Takip Ettiklerim"
    override val metricAllListHint = "Tüm liste"
    override val followBalanceTitle = "Takip Dengesi"
    override val ratioFormat = "Oran %sx"
    override val donutMutual = "Karşılıklı"
    override val donutNonFollower = "Takip Etmeyen"
    override val donutFan = "Takip Etmediğin"
    override val donutMatchLabel = "Eşleşme"
    override val recentChangesTitle = "Son Değişimler"
    override val lastRecordPrefix = "Son kayıt: "
    override val compareOnNextSync = "Sonraki veri yüklemede karşılaştırılır"
    override val simulateAction = "Simüle Et"
    override val lostFollowersTitle = "Takipten Çıkanlar"
    override val newFollowersTitle = "Yeni Takipçiler"
    override val noChangesText = "Değişim yok"
    override val mutualsRowTitle = "Karşılıklı Takipleştiklerin"
    override val mutualsRowSubtitle = "İki taraflı takipler"
    override val pendingRowTitle = "Bekleyen Takip İstekleri"
    override val pendingRowSubtitle = "Henüz kabul edilmemiş istekler"
    override val whitelistRowTitle = "Beyaz Liste"
    override val whitelistRowSubtitle = "Takipten çıkma uyarısından hariç tutulanlar"
    override val smartQueueTitle = "Sıralı İnceleme"
    override val smartQueueDescFormat = "Seni takip etmeyen %d hesabı sırayla inceleyip Instagram'da açabilir veya beyaz listeye alabilirsin."
    override val startReviewButton = "İncelemeyi Başlat"
    override val unfollowedByMeRowTitle = "Takipten Çıktıkların"
    override val accountListNoticePrivateTitle = "Bu hesap gizli görünüyor"
    override val accountListNoticePrivateDesc = "Takipçi ve takip sayıları alındı. Hesap gizli olduğu için kişi listesini görmek adına Resmi ZIP yükleyin veya Web ile giriş yapın."
    override val accountListNoticeLoginDesc = "Profilin takipçi ve takip sayıları çekildi. Instagram tam kişi listelerini yalnızca oturum açıkken veya Resmi ZIP ile açar; devam etmek için ZIP yükleyin veya Web ile giriş yapın."
    override val accountListNoticeNotFoundDesc = "Kullanıcı adı Instagram'da bulunamadı. Yazımı kontrol edip tekrar deneyin."
    override val accountListNoticeErrorDesc = "Profil bilgisine şu an ulaşılamadı. Bağlantınızı kontrol edip tekrar deneyin veya ZIP yükleyin."
    override val loadArchiveActionShort = "ZIP Yükle"
    override val webSignInActionShort = "Web ile Bağlan"
    override val retryActionShort = "Tekrar Dene"
    override val accountsSheetTitle = "Instagram Hesapları"
    override val accountsSheetSubtitle = "Kullanıcı adı sorgula, yeni hesap ekle veya hesaplar arası geç"
    override val addOrLookupAccountTitle = "Kullanıcı Adı ile Hesap Ekle / Aç"
    override val usernameInputPlaceholder = "Örn: kyhlaer"
    override val lookupAndAddButton = "Hesabı Getir & Ekle"
    override val lookingUpAccountButton = "Profil Sorgulanıyor..."
    override val savedAccountsTitle = "Kayıtlı Hesaplar (%d)"
    override val activeBadge = "Aktif"
    override val privateBadge = "Gizli"
    override val publicBadge = "Açık"
    override val removeAccountDesc = "Hesabı Kaldır"
    override val accountCountsShortFormat = "%d takipçi · %d takip"
    override val publicLookupBannerTitle = "Kullanıcı Adıyla Hızlı Veri Çek"
    override val publicLookupBannerDesc = "ZIP veya giriş olmadan sadece kullanıcı adını yazarak profil sayılarını ve uygunsa açık listeleri getirir."
    override val copyUsernameDesc = "Kullanıcı adını kopyala"
    override val usernameCopiedMsgFormat = "@%s panoya kopyalandı."
    override val allTagsFilterChip = "Tümü"
    override val onlyNotesFilterChip = "Notlular"
    override val onlyCloseFriendsChip = "Yakın Arkadaşlar"
    override val dataTabTitle = "Veri Merkezi"
    override val dataTabSubtitle = "Hesap ekle, kullanıcı adından çek veya arşiv/web ile senkronize et"
    override val settingsTabTitle = "Ayarlar"
    override val settingsTabSubtitle = "Tema, dil, güvenlik kalkanı ve sürüm bilgileri"
    override val accountsCardTitle = "Hesap Yönetimi"
    override val accountsCardDescFormat = "Aktif: @%s · Toplam %d kayıtlı hesap"
    override val manageAccountsButton = "Hesapları Yönet / Değiştir"
    override val connectionsCountFormat = "%d kişi · %s"
    override val sortContentDesc = "Sırala"
    override val shareContentDesc = "Paylaş"
    override val shareReportSubjectPrefix = "Installow - "
    override val shareChooserTitle = "Raporu Paylaş / Dışa Aktar"
    override val searchPlaceholder = "Kullanıcı adı, isim veya not ara"
    override val clearContentDesc = "Temizle"
    override val whitelistHidden = "Beyaz Liste Gizli"
    override val whitelistIncluded = "Beyaz Liste Dahil"
    override val smartQueueChip = "Sıralı İncele"
    override val followerCountZipNotice = "Resmi Instagram ZIP dosyaları diğer kişilerin takipçi sayısını içermez."
    override val emptyMatchesTitle = "Eşleşen hesap bulunamadı"
    override val emptyMatchesSubtitle = "Aramayı temizleyip tekrar deneyebilirsin."
    override val openInInstagramDesc = "Instagram'da Aç"
    override val followerCountSuffix = " takipçi"
    override val smartQueueHeaderFormat = "Sıralı İnceleme · %d / %d"
    override val closeDesc = "Kapat"
    override val openInIgShort = "IG'de Aç"
    override val markedUnfollowedShort = "Çıktım"
    override val prevButton = "Önceki"
    override val nextButton = "Sonraki"
    override val finishButton = "Tamamla"
    override val closeFriendBadge = "Yakın Arkadaş"
    override val whitelistBadgeDesc = "Beyaz Liste"
    override val followerCountDetailPrefix = "Takipçi Sayısı: "
    override val followedMeDatePrefix = "Seni Takip Tarihi: "
    override val iFollowedDatePrefix = "Senin Takip Tarihin: "
    override val pendingDatePrefix = "İstek Gönderim Tarihi: "
    override val whitelistSwitchTitle = "Beyaz Liste"
    override val whitelistSwitchSubtitle = "Takip etmeyenler uyarısından hariç tutar."
    override val tagSectionTitle = "Etiket"
    override val personalNoteLabel = "Kişisel Not"
    override val personalNotePlaceholder = "Not ekle..."
    override val saveButton = "Kaydet"
    override val openInstagramButton = "Instagram'da Aç"
    override val markUnfollowedButton = "Takipten Çıktım Olarak İşaretle"
    override val analyticsTitle = "Analiz"
    override val analyticsSubtitle = "Hesap dengesi, aylık aktivite ve kayıt geçmişi"
    override val accountStatusLabel = "Hesap Durumu"
    override val healthStrong = "Güçlü Karşılıklılık"
    override val healthBalanced = "Dengeli Profil"
    override val healthCleanupSuggested = "Takip Temizliği Önerilir"
    override val matchBadgeFormat = "%%%d Eşleşme"
    override val metricReturnRate = "Geri Dönüş"
    override val metricNonFollowerShort = "Takip Etmeyen"
    override val metricFansShort = "Hayranlar"
    override val last6MonthsTitle = "Son 6 Ay"
    override val last6MonthsSubtitle = "Tarih bilgisi bulunan hesapların aylık dağılımı"
    override val legendNewFollowers = "Yeni Takipçi"
    override val legendNewFollowing = "Yeni Takip Edilen"
    override val highlightsTitle = "Öne Çıkanlar"
    override val firstFollowerLabel = "İlk Takipçin"
    override val oldestNonFollowerLabel = "En Eski Karşılıksız Takip"
    override val longestPendingLabel = "En Uzun Bekleyen İstek"
    override val snapshotHistoryFormat = "Kayıt Geçmişi (%d)"
    override val followersCountUnit = "%d takipçi"
    override val followingCountUnit = "%d takip"
    override val dataSettingsTitle = "Veri & Ayarlar"
    override val dataSettingsSubtitle = "Hesap verilerini yükle, dışa aktar ve görünümü düzenle"
    override val officialArchiveTitle = "Resmi Instagram Arşivi"
    override val zeroRiskRecommendedBadge = "Sıfır Risk · Önerilen"
    override val officialArchiveDesc = "Instagram sunucularına tek bir istek bile göndermez. Meta'nın indirme merkezinden aldığın ZIP veya JSON dosyalarını cihazında çevrimdışı analiz eder."
    override val processingFilesText = "Dosyalar İşleniyor..."
    override val selectArchiveButton = "ZIP / JSON / HTML Seç"
    override val howToExportTitle = "Dosya Instagram'dan nasıl alınır?"
    override val guideStep1 = "Instagram > Profil > Menü (≡) > Hesaplar Merkezi'ne gir."
    override val guideStep2 = "Bilgilerin ve izinlerin > Bilgilerini dışa aktar > Dışa aktarım oluştur."
    override val guideStep3 = "Sadece 'Takipçiler ve takip edilenler'i seç, Format olarak 'JSON' seç."
    override val guideStep4 = "İnen .zip dosyasını veya içindeki JSON dosyalarını yukarıdan seç."
    override val liveWebSyncTitle = "Canlı Web Senkronizasyonu"
    override val slowModeBadge = "Yavaş Mod"
    override val liveWebSyncDesc = "Uygulama içi tarayıcıdan instagram.com oturumunla salt-okunur çekim yapar. Her sayfa arasında 2-3 sn bekler ve 15 dk soğuma koruması uygular."
    override val connectInstagramWebButton = "Instagram Web ile Bağlan"
    override val appearanceTitle = "Görünüm"
    override val themeDark = "Koyu"
    override val themeLight = "Açık"
    override val themeSystem = "Sistem"
    override val languageTitle = "Dil / Language"
    override val langTurkish = "Türkçe"
    override val langEnglish = "English"
    override val langGerman = "Deutsch"
    override val langFrench = "Français"
    override val langSpanish = "Español"
    override val langSystem = "Sistem"
    override val cyberShieldTitle = "Siber Güvenlik Kalkanı"
    override val cyberShieldActiveBadge = "Aktif"
    override val cyberShieldVerifiedFormat = "%d/%d Doğrulandı"
    override val shieldLineAes = "AES-256-GCM Keystore ile yerel veri şifrelemesi"
    override val shieldLineHttps = "Yalnızca HTTPS & Meta/Instagram alan adı whitelist"
    override val shieldLineBridge = "256-bit Nonce JS köprüsü ve FLAG_SECURE ekran kilidi"
    override val shieldLineZipCsv = "Zip Slip, ZIP bombası ve CSV enjeksiyon koruması"
    override val shieldLineBackup = "Bulut yedekleme ve şifresiz trafik (cleartext) kapalı"
    override val runSecurityTestButton = "Güvenlik Testini Çalıştır"
    override val clearWebCookiesButton = "Web Oturum Çerezlerini Temizle"
    override val exportCsvRowTitle = "Aktif Listeyi CSV Olarak Paylaş"
    override val exportCsvRowSubtitleFormat = "%s (%d kişi)"
    override val simulateRowTitle = "Değişim Simülasyonu Çalıştır"
    override val simulateRowSubtitle = "Takipten çıkan ve yeni takipçi örneği ekler"
    override val resetDemoRowTitle = "Demo Verisine Sıfırla"
    override val resetDemoRowSubtitle = "Örnek takipçi ve takip listesini geri yükler"
    override val releaseNotesRowTitle = "Sürüm Notları (v1.33)"
    override val releaseNotesRowSubtitle = "Bu güncellemedeki yenilikleri ve iyileştirmeleri gör"
    override val releaseNotesDialogTitle = "Installow v1.33 — Sürüm Notları"
    override val releaseNotesDialogBody = "• Türkçe, İngilizce, Almanca, Fransızca, İspanyolca ve Sistem dil desteği eklendi.\n• Veri ve Ayarlar ayrı sekmelere bölündü; başlık butonları ferahlaştırıldı ve by kyhlaer imzası eklendi.\n• Çoklu Instagram hesabı ekleme, hesaplar arası tek dokunuşla geçiş ve kullanıcı adından doğrudan profil/takip çekimi eklendi.\n• Kişiler ekranına etiket/not filtresi ve tek dokunuşla kullanıcı adı kopyalama eklendi.\n• AES-256-GCM şifreleme ve R8 optimizasyonu korundu."
    override val releaseNotesClose = "Kapat"
    override val footerSignature = "Installow v1.33 · by kyhlaer"
    override val webSyncHeaderTitle = "Instagram Web Bağlantısı"
    override val webSyncHeaderSubtitle = "Güvenli yavaş mod · Şifre kaydedilmez"
    override val clearCookiesDesc = "Çerezleri Temizle"
    override val refreshDesc = "Yenile"
    override val webSyncProtectionBanner = "Koruma aktif: Sayfalar arası 2-3 sn bekleme, 15 dk soğuma sınırı ve uyarıda otomatik durdurma."
    override val webSyncCooldownStatusFormat = "Hesap koruma soğuma süresi aktif: %d dk bekleyin veya sıfır riskli Resmi ZIP yöntemini kullanın."
    override val webSyncInitialStatus = "Instagram hesabınıza giriş yapın, ardından aşağıdaki butona dokunun."
    override val webSyncSessionDetected = "Oturum algılandı. Takipçi ve takip edilenleri çekebilirsiniz."
    override val webSyncCookiesResetStatus = "Oturum ve çerezler sıfırlandı. Yeniden giriş yapabilirsiniz."
    override val webSyncSslErrorStatus = "SSL/TLS sertifikası doğrulanamadı. Bağlantı güvenlik gerekçesiyle durduruldu."
    override val webSyncVerifyingSession = "Oturum doğrulanıyor..."
    override val webSyncFetchingButton = "Güvenli Modda Çekiliyor..."
    override val webSyncCooldownButtonFormat = "Koruma Beklemesi (%d dk)"
    override val webSyncFetchButton = "Takipçi & Takip Edilenleri Çek"
    override val webSyncProcessingErrorPrefix = "Veri işlenirken hata oluştu: "
    override val webSyncJsNoSession = "Aktif oturum bulunamadı. Lütfen önce yukarıdan Instagram hesabınıza giriş yapın."
    override val webSyncJsFetchingFormat = " güvenli modda çekiliyor ("
    override val webSyncJsFollowersLabel = "Takipçiler"
    override val webSyncJsFollowingLabel = "Takip edilenler"
    override val webSyncJsRateLimited = "Instagram hız uyarısı algılandı. Hesabınızı korumak için işlem durduruldu ve 60 dk kilitlendi. Sıfır risk için Resmi ZIP yöntemini kullanın."
    override val webSyncJsHttpErrorPrefix = "Instagram HTTP "
    override val webSyncJsHttpErrorSuffix = " döndürdü. İşlem güvenli şekilde durduruldu."
    override val webSyncJsTakingBreakSuffix = " kişi) · Kısa mola veriliyor..."
    override val webSyncJsFollowersDoneBreak = "Takipçiler tamam. Kısa mola..."
    override val webSyncJsSaving = "Tamamlandı, kaydediliyor..."
    override val webSyncJsGenericError = "Senkronizasyon güvenli şekilde durduruldu."
    override val dateUnknown = "Tarih bilinmiyor"
    override val relJustNow = "Az önce"
    override val relMinutesAgo = "%d dk önce"
    override val relHoursAgo = "%d sa önce"
    override val relDaysAgo = "%d gün önce"
    override val relMonthsAgo = "%d ay önce"
    override val relYearsAgo = "%d yıl önce"
    override val savedDataFallbackLabel = "Kayıtlı Veri"
    override val securityAuditMsgFormat = "Güvenlik denetimi tamamlandı: %d / %d kalkan doğrulandı."
    override val cookiesClearedMsg = "Web oturum çerezleri ve tarayıcı önbelleği temizlendi."
    override val whitelistAddedFormat = "@%s beyaz listeye eklendi."
    override val whitelistRemovedFormat = "@%s beyaz listeden çıkarıldı."
    override val noteSavedFormat = "@%s için etiket ve not kaydedildi."
    override val markedUnfollowedMsgFormat = "@%s takip edilenlerden çıkarıldı olarak işaretlendi."
    override val queueNoCandidatesMsg = "İncelenecek (beyaz liste dışı) takip etmeyen hesap bulunamadı!"
    override val queueCompletedMsg = "Sıralı inceleme kuyruğu tamamlandı!"
    override val queueAllReviewedMsg = "Kuyruktaki tüm hesaplar incelendi!"
    override val demoResetMsg = "Demo veri seti ve örnek değişim geçmişi yüklendi."
    override val simDiffMsgFormat = "Yeni snapshot: %d takipten çıkan, %d yeni takipçi."
    override val simCreatedMsg = "Yeni değişim kaydı oluşturuldu."
    override val importNotFoundMsg = "Seçilen dosyalarda followers_1.json / following.json verisi bulunamadı. Lütfen Instagram ZIP arşivini veya JSON/HTML dosyalarını seçin."
    override val archiveOwnerFallback = "Instagram Arşivim"
    override val archiveSourceLabelFormat = "Instagram Arşivi (%d dosya)"
    override val importSuccessFormat = "İçe aktarıldı: %d takipçi, %d takip edilen."
    override val importFailedPrefix = "Dosya okunamadı: "
    override val unknownErrorText = "Bilinmeyen hata"
    override val invalidUsernameMsg = "Geçerli bir Instagram kullanıcı adı girin."
    override val publicLookupFullSuccessFormat = "@%s verileri çekildi: %d takipçi, %d takip edilen."
    override val publicLookupPrivateMsgFormat = "@%s gizli hesap (%d takipçi, %d takip). Kişi listesi için ZIP yükleyin veya Web ile giriş yapın."
    override val publicLookupCountsOnlyMsgFormat = "@%s sayıları alındı (%d takipçi, %d takip). Liste detayı için Web girişi veya Resmi ZIP gerekir."
    override val publicLookupNotFoundMsgFormat = "@%s bulunamadı. Kullanıcı adını kontrol edin."
    override val publicLookupErrorMsg = "Instagram profili sorgulanırken ağ hatası oluştu."
    override val accountSwitchedMsgFormat = "Aktif hesap @%s olarak değiştirildi."
    override val accountRemovedMsgFormat = "@%s kayıtlı hesaplardan kaldırıldı."
    override val cannotRemoveLastAccountMsg = "En az bir hesap kalmalıdır."
    override val webSyncSourceLabelFormat = "Canlı Web Senkronizasyonu (@%s)"
    override val webSyncSuccessFormat = "@%s senkronize edildi: %d takipçi, %d takip edilen."
    override val csvReportHeaderFormat = "Installow Raporu — %s"
    override val csvAccountSummaryFormat = "Hesap: @%s | Toplam: %d"
    override val csvColumnsHeader = "kullanici_adi,gorunen_ad,durum,etiket,beyaz_liste,profil_url"
    override val checkUrlTitle = "HTTPS & Alan Adı Whitelist"
    override val checkUrlPass = "HTTP, javascript:, file: ve sahte domainler engellendi."
    override val checkUrlFail = "Alan adı doğrulaması başarısız!"
    override val checkIntentTitle = "Intent Yönlendirme Kalkanı"
    override val checkIntentPass = "Sadece doğrulanmış Meta/Instagram fallback linklerine izin veriliyor."
    override val checkIntentFail = "Intent fallback filtresi başarısız!"
    override val checkZipTitle = "Zip Slip & Arşiv Koruması"
    override val checkZipPass = "Yol geçişi (..) ve mutlak yol saldırıları engellendi."
    override val checkZipFail = "Zip yolu doğrulaması başarısız!"
    override val checkCsvTitle = "CSV Enjeksiyon Koruması"
    override val checkCsvPass = "Formül başlangıçları (=, +, -, @) ve satır atlamaları nötralize edildi."
    override val checkCsvFail = "CSV temizleme testi başarısız!"
    override val checkNonceTitle = "JS Köprüsü 256-bit Nonce Kilidi"
    override val checkNoncePass = "Sabit zamanlı token doğrulaması aktif (256-bit)."
    override val checkNonceFail = "Token testi başarısız!"
    override val checkAesTitle = "AES-256-GCM Keystore Şifreleme"
    override val checkAesPass = "Donanım destekli Android Keystore AES-256-GCM şifreleme/çözme doğrulandı."
    override val checkAesFailPrefix = "Keystore test edilemedi: "
}

object EnStrings : AppStrings {
    override val language = AppLanguage.EN
    override val navDashboard = "Home"
    override val navConnections = "People"
    override val navAnalytics = "Insights"
    override val navData = "Data"
    override val navSettings = "Settings"
    override val exitDialogTitle = "Exit Installow?"
    override val exitDialogMessage = "Are you sure you want to close Installow?"
    override val exitConfirm = "Exit"
    override val exitCancel = "Cancel"
    override val categoryMutual = "Mutual"
    override val categoryNonFollower = "Not Following Back"
    override val categoryFan = "Follows You"
    override val categoryPending = "Pending Request"
    override val tabNonFollowers = "Not Following Back"
    override val tabNonFollowersShort = "Non-Followers"
    override val tabFans = "Fans (You Don't Follow)"
    override val tabFansShort = "Fans"
    override val tabMutuals = "Mutuals"
    override val tabMutualsShort = "Mutuals"
    override val tabAllFollowers = "Followers"
    override val tabAllFollowersShort = "Followers"
    override val tabAllFollowing = "Following"
    override val tabAllFollowingShort = "Following"
    override val tabUnfollowersDiff = "Unfollowers"
    override val tabUnfollowersDiffShort = "Unfollowed"
    override val tabPending = "Pending Requests"
    override val tabPendingShort = "Pending"
    override val tabWhitelist = "Whitelist"
    override val tabWhitelistShort = "Whitelist"
    override val sortNewestFirst = "Newest First"
    override val sortOldestFirst = "Oldest First"
    override val sortFollowersDesc = "Followers (High → Low)"
    override val sortFollowersAsc = "Followers (Low → High)"
    override val sortUsernameAsc = "Username (A → Z)"
    override val sortUsernameDesc = "Username (Z → A)"
    override val sourceDemo = "Demo Dataset"
    override val sourcePublicLookup = "Public Profile"
    override val sourceOfficialExport = "Official Instagram Archive"
    override val sourceWebSync = "Live Web Sync"
    override val presetTags = listOf(
        "Celebrity / Brand",
        "Close Friend",
        "Family",
        "Work / Network",
        "Content Creator",
        "Follow Check"
    )
    override val searchContentDesc = "Search"
    override val uploadDataContentDesc = "Import Data"
    override val metricNonFollowersTitle = "Not Following Back"
    override val metricNonFollowersHintWhitelist = "Excl. whitelist"
    override val metricNonFollowersHintDefault = "No follow back"
    override val metricFansTitle = "You Don't Follow"
    override val metricFansHint = "Follows you"
    override val metricFollowersTitle = "Followers"
    override val metricFollowingTitle = "Following"
    override val metricAllListHint = "Full list"
    override val followBalanceTitle = "Follow Balance"
    override val ratioFormat = "Ratio %sx"
    override val donutMutual = "Mutual"
    override val donutNonFollower = "Not Following"
    override val donutFan = "Fans"
    override val donutMatchLabel = "Mutual"
    override val recentChangesTitle = "Recent Changes"
    override val lastRecordPrefix = "Last snapshot: "
    override val compareOnNextSync = "Compared on next data import"
    override val simulateAction = "Simulate"
    override val lostFollowersTitle = "Unfollowers"
    override val newFollowersTitle = "New Followers"
    override val noChangesText = "No changes"
    override val mutualsRowTitle = "Mutual Connections"
    override val mutualsRowSubtitle = "Following each other"
    override val pendingRowTitle = "Pending Follow Requests"
    override val pendingRowSubtitle = "Sent requests not yet accepted"
    override val whitelistRowTitle = "Whitelist"
    override val whitelistRowSubtitle = "Excluded from non-follower alerts"
    override val smartQueueTitle = "Step-by-Step Review"
    override val smartQueueDescFormat = "Review %d accounts not following you back one by one, open them on Instagram, or add to whitelist."
    override val startReviewButton = "Start Review"
    override val unfollowedByMeRowTitle = "Unfollowed by You"
    override val accountListNoticePrivateTitle = "This account is private"
    override val accountListNoticePrivateDesc = "Follower and following counts were fetched. Since this profile is private, import the Official ZIP or sign in via Instagram Web to view full lists."
    override val accountListNoticeLoginDesc = "Public follower and following counts were fetched. Instagram requires a signed-in session or Official ZIP to display full follower lists — upload a ZIP or sign in via Web."
    override val accountListNoticeNotFoundDesc = "Username was not found on Instagram. Check the spelling and try again."
    override val accountListNoticeErrorDesc = "Could not reach Instagram profile info right now. Check your connection or import an Official ZIP."
    override val loadArchiveActionShort = "Import ZIP"
    override val webSignInActionShort = "Web Sign-In"
    override val retryActionShort = "Retry"
    override val accountsSheetTitle = "Instagram Accounts"
    override val accountsSheetSubtitle = "Look up a username, add accounts, or switch anytime"
    override val addOrLookupAccountTitle = "Add / Open by Username"
    override val usernameInputPlaceholder = "e.g. kyhlaer"
    override val lookupAndAddButton = "Fetch & Add Account"
    override val lookingUpAccountButton = "Checking Profile..."
    override val savedAccountsTitle = "Saved Accounts (%d)"
    override val activeBadge = "Active"
    override val privateBadge = "Private"
    override val publicBadge = "Public"
    override val removeAccountDesc = "Remove Account"
    override val accountCountsShortFormat = "%d followers · %d following"
    override val publicLookupBannerTitle = "Quick Lookup by Username"
    override val publicLookupBannerDesc = "Fetch live counts (and public lists when available) just by typing a username — no ZIP required."
    override val copyUsernameDesc = "Copy username"
    override val usernameCopiedMsgFormat = "@%s copied to clipboard."
    override val allTagsFilterChip = "All"
    override val onlyNotesFilterChip = "With Notes"
    override val onlyCloseFriendsChip = "Close Friends"
    override val dataTabTitle = "Data Hub"
    override val dataTabSubtitle = "Manage accounts, fetch by username, or import/sync data"
    override val settingsTabTitle = "Settings"
    override val settingsTabSubtitle = "Theme, language, security shield, and version info"
    override val accountsCardTitle = "Account Management"
    override val accountsCardDescFormat = "Active: @%s · %d saved accounts"
    override val manageAccountsButton = "Manage / Switch Accounts"
    override val connectionsCountFormat = "%d accounts · %s"
    override val sortContentDesc = "Sort"
    override val shareContentDesc = "Share"
    override val shareReportSubjectPrefix = "Installow - "
    override val shareChooserTitle = "Share / Export Report"
    override val searchPlaceholder = "Search username, name or note"
    override val clearContentDesc = "Clear"
    override val whitelistHidden = "Whitelist Hidden"
    override val whitelistIncluded = "Whitelist Included"
    override val smartQueueChip = "Review Queue"
    override val followerCountZipNotice = "Official Instagram ZIP exports do not include follower counts of other accounts."
    override val emptyMatchesTitle = "No matching accounts"
    override val emptyMatchesSubtitle = "Try clearing or adjusting your search query."
    override val openInInstagramDesc = "Open on Instagram"
    override val followerCountSuffix = " followers"
    override val smartQueueHeaderFormat = "Review Queue · %d / %d"
    override val closeDesc = "Close"
    override val openInIgShort = "Open IG"
    override val markedUnfollowedShort = "Unfollowed"
    override val prevButton = "Prev"
    override val nextButton = "Next"
    override val finishButton = "Finish"
    override val closeFriendBadge = "Close Friend"
    override val whitelistBadgeDesc = "Whitelisted"
    override val followerCountDetailPrefix = "Followers: "
    override val followedMeDatePrefix = "Followed You: "
    override val iFollowedDatePrefix = "You Followed: "
    override val pendingDatePrefix = "Request Sent: "
    override val whitelistSwitchTitle = "Whitelist"
    override val whitelistSwitchSubtitle = "Exclude from non-follower alerts."
    override val tagSectionTitle = "Tag"
    override val personalNoteLabel = "Personal Note"
    override val personalNotePlaceholder = "Add a note..."
    override val saveButton = "Save"
    override val openInstagramButton = "Open on Instagram"
    override val markUnfollowedButton = "Mark as Unfollowed"
    override val analyticsTitle = "Insights"
    override val analyticsSubtitle = "Follow ratio, monthly activity and snapshot history"
    override val accountStatusLabel = "Account Health"
    override val healthStrong = "Strong Reciprocity"
    override val healthBalanced = "Balanced Profile"
    override val healthCleanupSuggested = "Cleanup Suggested"
    override val matchBadgeFormat = "%d%% Mutual"
    override val metricReturnRate = "Follow Back"
    override val metricNonFollowerShort = "Non-Followers"
    override val metricFansShort = "Fans"
    override val last6MonthsTitle = "Last 6 Months"
    override val last6MonthsSubtitle = "Monthly breakdown of timestamped connections"
    override val legendNewFollowers = "New Followers"
    override val legendNewFollowing = "New Following"
    override val highlightsTitle = "Highlights"
    override val firstFollowerLabel = "Earliest Follower"
    override val oldestNonFollowerLabel = "Oldest One-Way Follow"
    override val longestPendingLabel = "Longest Pending Request"
    override val snapshotHistoryFormat = "Snapshot History (%d)"
    override val followersCountUnit = "%d followers"
    override val followingCountUnit = "%d following"
    override val dataSettingsTitle = "Data & Settings"
    override val dataSettingsSubtitle = "Import account data, export reports and customize preferences"
    override val officialArchiveTitle = "Official Instagram Archive"
    override val zeroRiskRecommendedBadge = "Zero Risk · Recommended"
    override val officialArchiveDesc = "Sends zero requests to Instagram servers. Analyzes ZIP or JSON exports from Meta Accounts Center completely offline on your device."
    override val processingFilesText = "Processing Files..."
    override val selectArchiveButton = "Select ZIP / JSON / HTML"
    override val howToExportTitle = "How to download data from Instagram?"
    override val guideStep1 = "Open Instagram > Profile > Menu (≡) > Accounts Center."
    override val guideStep2 = "Your information and permissions > Export your information > Create export."
    override val guideStep3 = "Select only 'Followers and following', and choose 'JSON' format."
    override val guideStep4 = "Select the downloaded .zip archive or JSON files above."
    override val liveWebSyncTitle = "Live Web Sync"
    override val slowModeBadge = "Slow Mode"
    override val liveWebSyncDesc = "Read-only sync using your instagram.com session in the built-in browser. Waits 2-3s between pages with a 15-min cooldown shield."
    override val connectInstagramWebButton = "Connect via Instagram Web"
    override val appearanceTitle = "Appearance"
    override val themeDark = "Dark"
    override val themeLight = "Light"
    override val themeSystem = "System"
    override val languageTitle = "Language / Dil"
    override val langTurkish = "Türkçe"
    override val langEnglish = "English"
    override val langGerman = "Deutsch"
    override val langFrench = "Français"
    override val langSpanish = "Español"
    override val langSystem = "System"
    override val cyberShieldTitle = "Cyber Security Shield"
    override val cyberShieldActiveBadge = "Active"
    override val cyberShieldVerifiedFormat = "%d/%d Verified"
    override val shieldLineAes = "Local AES-256-GCM Keystore encryption"
    override val shieldLineHttps = "Strict HTTPS & Meta/Instagram domain whitelist"
    override val shieldLineBridge = "256-bit Nonce JS bridge & FLAG_SECURE screen lock"
    override val shieldLineZipCsv = "Zip Slip, ZIP bomb & CSV injection protection"
    override val shieldLineBackup = "Cloud backup & cleartext HTTP traffic disabled"
    override val runSecurityTestButton = "Run Security Self-Test"
    override val clearWebCookiesButton = "Clear Web Session Cookies"
    override val exportCsvRowTitle = "Share Active List as CSV"
    override val exportCsvRowSubtitleFormat = "%s (%d accounts)"
    override val simulateRowTitle = "Run Change Simulation"
    override val simulateRowSubtitle = "Adds sample unfollower and new follower entries"
    override val resetDemoRowTitle = "Reset to Demo Data"
    override val resetDemoRowSubtitle = "Restores sample followers and following dataset"
    override val releaseNotesRowTitle = "Release Notes (v1.33)"
    override val releaseNotesRowSubtitle = "View what's new and improved in this update"
    override val releaseNotesDialogTitle = "Installow v1.33 — Release Notes"
    override val releaseNotesDialogBody = "• Added Turkish, English, German, French, Spanish, and System language support.\n• Separated Data and Settings into dedicated tabs; refined header button spacing and added by kyhlaer branding.\n• Added multi-account switching, quick account creation, and direct username public lookup.\n• Added quick tag/note filters and 1-tap username copy in People.\n• Retained AES-256-GCM encryption and R8 minification."
    override val releaseNotesClose = "Close"
    override val footerSignature = "Installow v1.33 · by kyhlaer"
    override val webSyncHeaderTitle = "Instagram Web Connection"
    override val webSyncHeaderSubtitle = "Safe slow mode · Password never stored"
    override val clearCookiesDesc = "Clear Cookies"
    override val refreshDesc = "Refresh"
    override val webSyncProtectionBanner = "Protection active: 2-3s delay between pages, 15-min cooldown, auto-stop on rate warning."
    override val webSyncCooldownStatusFormat = "Account protection cooldown active: wait %d min or use the zero-risk Official ZIP method."
    override val webSyncInitialStatus = "Sign in to your Instagram account above, then tap the button below."
    override val webSyncSessionDetected = "Session detected. You can now fetch followers and following."
    override val webSyncCookiesResetStatus = "Session and cookies cleared. You can sign in again."
    override val webSyncSslErrorStatus = "SSL/TLS certificate could not be verified. Connection blocked for safety."
    override val webSyncVerifyingSession = "Verifying session..."
    override val webSyncFetchingButton = "Fetching in Safe Mode..."
    override val webSyncCooldownButtonFormat = "Protection Cooldown (%d min)"
    override val webSyncFetchButton = "Fetch Followers & Following"
    override val webSyncProcessingErrorPrefix = "Error processing data: "
    override val webSyncJsNoSession = "No active session found. Please sign in to Instagram above first."
    override val webSyncJsFetchingFormat = " fetching in safe mode ("
    override val webSyncJsFollowersLabel = "Followers"
    override val webSyncJsFollowingLabel = "Following"
    override val webSyncJsRateLimited = "Instagram rate limit detected. Stopped to protect your account and locked for 60 min. Use Official ZIP for zero risk."
    override val webSyncJsHttpErrorPrefix = "Instagram returned HTTP "
    override val webSyncJsHttpErrorSuffix = ". Safely stopped."
    override val webSyncJsTakingBreakSuffix = " accounts) · Taking short break..."
    override val webSyncJsFollowersDoneBreak = "Followers complete. Taking short break..."
    override val webSyncJsSaving = "Complete, saving..."
    override val webSyncJsGenericError = "Sync safely stopped."
    override val dateUnknown = "Date unknown"
    override val relJustNow = "Just now"
    override val relMinutesAgo = "%dm ago"
    override val relHoursAgo = "%dh ago"
    override val relDaysAgo = "%dd ago"
    override val relMonthsAgo = "%dmo ago"
    override val relYearsAgo = "%dy ago"
    override val savedDataFallbackLabel = "Saved Data"
    override val securityAuditMsgFormat = "Security audit finished: %d / %d shields verified."
    override val cookiesClearedMsg = "Web session cookies and browser cache cleared."
    override val whitelistAddedFormat = "@%s added to whitelist."
    override val whitelistRemovedFormat = "@%s removed from whitelist."
    override val noteSavedFormat = "Tag and note saved for @%s."
    override val markedUnfollowedMsgFormat = "@%s marked as unfollowed."
    override val queueNoCandidatesMsg = "No non-whitelisted non-followers left to review!"
    override val queueCompletedMsg = "Review queue completed!"
    override val queueAllReviewedMsg = "All accounts in queue reviewed!"
    override val demoResetMsg = "Demo dataset and sample change history loaded."
    override val simDiffMsgFormat = "New snapshot: %d unfollowers, %d new followers."
    override val simCreatedMsg = "New change snapshot created."
    override val importNotFoundMsg = "Could not find followers_1.json / following.json in selected files. Please select your Instagram ZIP archive or JSON/HTML files."
    override val archiveOwnerFallback = "My Instagram Archive"
    override val archiveSourceLabelFormat = "Instagram Archive (%d files)"
    override val importSuccessFormat = "Imported: %d followers, %d following."
    override val importFailedPrefix = "Failed to read file: "
    override val unknownErrorText = "Unknown error"
    override val invalidUsernameMsg = "Enter a valid Instagram username."
    override val publicLookupFullSuccessFormat = "@%s synced: %d followers, %d following."
    override val publicLookupPrivateMsgFormat = "@%s is private (%d followers, %d following). Import ZIP or sign in via Web for full lists."
    override val publicLookupCountsOnlyMsgFormat = "@%s counts fetched (%d followers, %d following). Web sign-in or ZIP needed for full lists."
    override val publicLookupNotFoundMsgFormat = "@%s was not found. Please check the username."
    override val publicLookupErrorMsg = "Network error while checking the Instagram profile."
    override val accountSwitchedMsgFormat = "Switched active account to @%s."
    override val accountRemovedMsgFormat = "Removed @%s from saved accounts."
    override val cannotRemoveLastAccountMsg = "At least one account must remain."
    override val webSyncSourceLabelFormat = "Live Web Sync (@%s)"
    override val webSyncSuccessFormat = "@%s synced: %d followers, %d following."
    override val csvReportHeaderFormat = "Installow Report — %s"
    override val csvAccountSummaryFormat = "Account: @%s | Total: %d"
    override val csvColumnsHeader = "username,display_name,status,tag,whitelisted,profile_url"
    override val checkUrlTitle = "HTTPS & Domain Whitelist"
    override val checkUrlPass = "HTTP, javascript:, file: and phishing domains blocked."
    override val checkUrlFail = "Domain validation failed!"
    override val checkIntentTitle = "Intent Redirect Guard"
    override val checkIntentPass = "Only verified Meta/Instagram fallback URLs allowed."
    override val checkIntentFail = "Intent fallback filter failed!"
    override val checkZipTitle = "Zip Slip & Archive Guard"
    override val checkZipPass = "Path traversal (..) and absolute paths blocked."
    override val checkZipFail = "Zip path validation failed!"
    override val checkCsvTitle = "CSV Formula Injection Guard"
    override val checkCsvPass = "Formula prefixes (=, +, -, @) and line breaks neutralized."
    override val checkCsvFail = "CSV sanitization test failed!"
    override val checkNonceTitle = "256-bit Nonce JS Bridge Lock"
    override val checkNoncePass = "Constant-time token verification active (256-bit)."
    override val checkNonceFail = "Token verification test failed!"
    override val checkAesTitle = "AES-256-GCM Keystore Encryption"
    override val checkAesPass = "Hardware-backed Android Keystore AES-256-GCM encrypt/decrypt verified."
    override val checkAesFailPrefix = "Keystore test failed: "
}

object DeStrings : AppStrings {
    override val language = AppLanguage.DE
    override val navDashboard = "Start"
    override val navConnections = "Kontakte"
    override val navAnalytics = "Analyse"
    override val navData = "Daten"
    override val navSettings = "Einst."
    override val exitDialogTitle = "Installow beenden?"
    override val exitDialogMessage = "Möchtest du Installow wirklich schließen?"
    override val exitConfirm = "Beenden"
    override val exitCancel = "Abbrechen"
    override val categoryMutual = "Gegenseitig"
    override val categoryNonFollower = "Folgt nicht zurück"
    override val categoryFan = "Folgt dir"
    override val categoryPending = "Ausstehend"
    override val tabNonFollowers = "Folgen nicht zurück"
    override val tabNonFollowersShort = "Entfolgt"
    override val tabFans = "Fans (Folgst du nicht)"
    override val tabFansShort = "Fans"
    override val tabMutuals = "Gegenseitig"
    override val tabMutualsShort = "Gegenseitig"
    override val tabAllFollowers = "Follower"
    override val tabAllFollowersShort = "Follower"
    override val tabAllFollowing = "Gefolgt"
    override val tabAllFollowingShort = "Gefolgt"
    override val tabUnfollowersDiff = "Entfolger"
    override val tabUnfollowersDiffShort = "Verloren"
    override val tabPending = "Ausstehende Anfragen"
    override val tabPendingShort = "Anfragen"
    override val tabWhitelist = "Whitelist"
    override val tabWhitelistShort = "Whitelist"
    override val sortNewestFirst = "Neueste zuerst"
    override val sortOldestFirst = "Älteste zuerst"
    override val sortFollowersDesc = "Follower (Hoch → Niedrig)"
    override val sortFollowersAsc = "Follower (Niedrig → Hoch)"
    override val sortUsernameAsc = "Benutzername (A → Z)"
    override val sortUsernameDesc = "Benutzername (Z → A)"
    override val sourceDemo = "Demo-Datensatz"
    override val sourcePublicLookup = "Öffentliches Profil"
    override val sourceOfficialExport = "Offizielles Archiv"
    override val sourceWebSync = "Live Web-Sync"
    override val presetTags = listOf(
        "Promi / Marke",
        "Enger Freund",
        "Familie",
        "Arbeit / Netzwerk",
        "Creator",
        "Follow-Check"
    )
    override val searchContentDesc = "Suchen"
    override val uploadDataContentDesc = "Daten abrufen"
    override val metricNonFollowersTitle = "Folgen nicht zurück"
    override val metricNonFollowersHintWhitelist = "Ohne Whitelist"
    override val metricNonFollowersHintDefault = "Kein Rück-Follow"
    override val metricFansTitle = "Folgst du nicht"
    override val metricFansHint = "Folgt dir"
    override val metricFollowersTitle = "Follower"
    override val metricFollowingTitle = "Gefolgt"
    override val metricAllListHint = "Alle anzeigen"
    override val followBalanceTitle = "Follow-Balance"
    override val ratioFormat = "Verhältnis %sx"
    override val donutMutual = "Gegenseitig"
    override val donutNonFollower = "Folgt nicht"
    override val donutFan = "Fans"
    override val donutMatchLabel = "Quote"
    override val recentChangesTitle = "Letzte Änderungen"
    override val lastRecordPrefix = "Letzter Stand: "
    override val compareOnNextSync = "Vergleich beim nächsten Import"
    override val simulateAction = "Simulieren"
    override val lostFollowersTitle = "Entfolger"
    override val newFollowersTitle = "Neue Follower"
    override val noChangesText = "Keine Änderungen"
    override val mutualsRowTitle = "Gegenseitig"
    override val mutualsRowSubtitle = "Ihr folgt euch"
    override val pendingRowTitle = "Ausstehende Anfragen"
    override val pendingRowSubtitle = "Noch nicht angenommene Anfragen"
    override val whitelistRowTitle = "Whitelist"
    override val whitelistRowSubtitle = "Von Hinweisen ausgeschlossen"
    override val smartQueueTitle = "Schritt-für-Schritt-Prüfung"
    override val smartQueueDescFormat = "Prüfe %d Konten ohne Rück-Follow einzeln, öffne sie auf Instagram oder setze sie auf die Whitelist."
    override val startReviewButton = "Prüfung starten"
    override val unfollowedByMeRowTitle = "Von dir entfolgt"
    override val accountListNoticePrivateTitle = "Dieses Konto ist privat"
    override val accountListNoticePrivateDesc = "Follower- und Gefolgt-Zahlen wurden geladen. Da dieses Konto privat ist, lade für vollständige Listen ein offizielles ZIP-Archiv oder melde dich per Instagram Web an."
    override val accountListNoticeLoginDesc = "Öffentliche Follower- und Gefolgt-Zahlen wurden geladen. Für vollständige Listen verlangt Instagram eine Web-Anmeldung oder ein offizielles ZIP-Archiv."
    override val accountListNoticeNotFoundDesc = "Benutzername wurde auf Instagram nicht gefunden. Bitte überprüfe die Schreibweise."
    override val accountListNoticeErrorDesc = "Instagram-Profildaten konnten gerade nicht abgerufen werden. Prüfe deine Verbindung oder importiere ein ZIP-Archiv."
    override val loadArchiveActionShort = "ZIP laden"
    override val webSignInActionShort = "Web-Login"
    override val retryActionShort = "Erneut"
    override val accountsSheetTitle = "Instagram-Konten"
    override val accountsSheetSubtitle = "Benutzernamen prüfen, Konten hinzufügen oder wechseln"
    override val addOrLookupAccountTitle = "Per Benutzername öffnen"
    override val usernameInputPlaceholder = "z. B. kyhlaer"
    override val lookupAndAddButton = "Abrufen & Hinzufügen"
    override val lookingUpAccountButton = "Profil wird geprüft..."
    override val savedAccountsTitle = "Gespeicherte Konten (%d)"
    override val activeBadge = "Aktiv"
    override val privateBadge = "Privat"
    override val publicBadge = "Öffentlich"
    override val removeAccountDesc = "Konto entfernen"
    override val accountCountsShortFormat = "%d Follower · %d Gefolgt"
    override val publicLookupBannerTitle = "Schnellabruf per Benutzername"
    override val publicLookupBannerDesc = "Live-Zahlen (und offene Listen sofern erlaubt) einfach über @benutzername laden — auch ohne ZIP."
    override val copyUsernameDesc = "Benutzernamen kopieren"
    override val usernameCopiedMsgFormat = "@%s in Zwischenablage kopiert."
    override val allTagsFilterChip = "Alle"
    override val onlyNotesFilterChip = "Mit Notiz"
    override val onlyCloseFriendsChip = "Enge Freunde"
    override val dataTabTitle = "Daten & Konten"
    override val dataTabSubtitle = "Konten verwalten, Profildaten abrufen oder ZIP importieren"
    override val settingsTabTitle = "Einstellungen"
    override val settingsTabSubtitle = "Design, Sprache, Sicherheitsschild und Versionsinfos"
    override val accountsCardTitle = "Kontoverwaltung"
    override val accountsCardDescFormat = "Aktiv: @%s · %d gespeicherte Konten"
    override val manageAccountsButton = "Konten verwalten / wechseln"
    override val connectionsCountFormat = "%d Konten · %s"
    override val sortContentDesc = "Sortieren"
    override val shareContentDesc = "Teilen"
    override val shareReportSubjectPrefix = "Installow - "
    override val shareChooserTitle = "Bericht teilen / exportieren"
    override val searchPlaceholder = "Name, @Handle oder Notiz"
    override val clearContentDesc = "Leeren"
    override val whitelistHidden = "Whitelist ausgeblendet"
    override val whitelistIncluded = "Whitelist sichtbar"
    override val smartQueueChip = "Prüfliste"
    override val followerCountZipNotice = "Offizielle Instagram-ZIP-Exporte enthalten keine Follower-Zahlen fremder Konten."
    override val emptyMatchesTitle = "Keine passenden Konten"
    override val emptyMatchesSubtitle = "Suchbegriff oder Filter zurücksetzen."
    override val openInInstagramDesc = "Auf Instagram öffnen"
    override val followerCountSuffix = " Follower"
    override val smartQueueHeaderFormat = "Prüfliste · %d / %d"
    override val closeDesc = "Schließen"
    override val openInIgShort = "IG öffnen"
    override val markedUnfollowedShort = "Entfolgt"
    override val prevButton = "Zurück"
    override val nextButton = "Weiter"
    override val finishButton = "Fertig"
    override val closeFriendBadge = "Enger Freund"
    override val whitelistBadgeDesc = "Whitelist"
    override val followerCountDetailPrefix = "Follower: "
    override val followedMeDatePrefix = "Folgte dir: "
    override val iFollowedDatePrefix = "Du folgtest: "
    override val pendingDatePrefix = "Anfrage gesendet: "
    override val whitelistSwitchTitle = "Whitelist"
    override val whitelistSwitchSubtitle = "Aus Entfolger-Hinweisen ausblenden."
    override val tagSectionTitle = "Tag"
    override val personalNoteLabel = "Persönliche Notiz"
    override val personalNotePlaceholder = "Notiz hinzufügen..."
    override val saveButton = "Speichern"
    override val openInstagramButton = "Auf Instagram öffnen"
    override val markUnfollowedButton = "Als entfolgt markieren"
    override val analyticsTitle = "Analyse"
    override val analyticsSubtitle = "Verhältnis, Monatsaktivität und Verlauf"
    override val accountStatusLabel = "Kontostatus"
    override val healthStrong = "Starke Gegenseitigkeit"
    override val healthBalanced = "Ausgewogenes Profil"
    override val healthCleanupSuggested = "Bereinigung empfohlen"
    override val matchBadgeFormat = "%d%% Gegenseitig"
    override val metricReturnRate = "Rück-Follow"
    override val metricNonFollowerShort = "Entfolgt"
    override val metricFansShort = "Fans"
    override val last6MonthsTitle = "Letzte 6 Monate"
    override val last6MonthsSubtitle = "Verteilung nach Zeitstempel-Aktivität"
    override val legendNewFollowers = "Neue Follower"
    override val legendNewFollowing = "Neu gefolgt"
    override val highlightsTitle = "Highlights"
    override val firstFollowerLabel = "Ältester Follower"
    override val oldestNonFollowerLabel = "Ältestes einseitiges Follow"
    override val longestPendingLabel = "Längste offene Anfrage"
    override val snapshotHistoryFormat = "Verlauf (%d)"
    override val followersCountUnit = "%d Follower"
    override val followingCountUnit = "%d Gefolgt"
    override val dataSettingsTitle = "Daten & Einstellungen"
    override val dataSettingsSubtitle = "Kontodaten laden, Berichte teilen und App anpassen"
    override val officialArchiveTitle = "Offizielles Instagram-Archiv"
    override val zeroRiskRecommendedBadge = "Kein Risiko · Empfohlen"
    override val officialArchiveDesc = "Sendet keine Anfragen an Instagram. Analysiert ZIP- oder JSON-Exporte komplett lokal und offline."
    override val processingFilesText = "Dateien werden verarbeitet..."
    override val selectArchiveButton = "ZIP / JSON / HTML wählen"
    override val howToExportTitle = "Wie lädt man Daten von Instagram?"
    override val guideStep1 = "Instagram > Profil > Menü (≡) > Kontenübersicht öffnen."
    override val guideStep2 = "Deine Informationen und Berechtigungen > Export erstellen."
    override val guideStep3 = "Nur 'Follower und Gefolgte' sowie Format 'JSON' wählen."
    override val guideStep4 = "Heruntergeladene .zip- oder JSON-Dateien oben auswählen."
    override val liveWebSyncTitle = "Live Web-Synchronisierung"
    override val slowModeBadge = "Langsamer Modus"
    override val liveWebSyncDesc = "Schreibgeschützter Abruf über instagram.com im sicheren Browser mit Pausen und 15-Minuten-Abkühlschutz."
    override val connectInstagramWebButton = "Mit Instagram Web verbinden"
    override val appearanceTitle = "Darstellung"
    override val themeDark = "Dunkel"
    override val themeLight = "Hell"
    override val themeSystem = "System"
    override val languageTitle = "Sprache / Language"
    override val langTurkish = "Türkçe"
    override val langEnglish = "English"
    override val langGerman = "Deutsch"
    override val langFrench = "Français"
    override val langSpanish = "Español"
    override val langSystem = "System"
    override val cyberShieldTitle = "Sicherheits-Schild"
    override val cyberShieldActiveBadge = "Aktiv"
    override val cyberShieldVerifiedFormat = "%d/%d Geprüft"
    override val shieldLineAes = "Lokale AES-256-GCM Keystore-Verschlüsselung"
    override val shieldLineHttps = "Strikte HTTPS- & Meta/Instagram-Domain-Whitelist"
    override val shieldLineBridge = "256-Bit Nonce JS-Bridge & FLAG_SECURE Bildschirmschutz"
    override val shieldLineZipCsv = "Schutz vor Zip-Slip, ZIP-Bomben & CSV-Injektion"
    override val shieldLineBackup = "Cloud-Backup & unverschlüsselter HTTP-Verkehr gesperrt"
    override val runSecurityTestButton = "Sicherheitstest starten"
    override val clearWebCookiesButton = "Web-Cookies & Cache löschen"
    override val exportCsvRowTitle = "Aktive Liste als CSV teilen"
    override val exportCsvRowSubtitleFormat = "%s (%d Konten)"
    override val simulateRowTitle = "Änderungssimulation starten"
    override val simulateRowSubtitle = "Fügt Beispiel-Entfolger und neue Follower hinzu"
    override val resetDemoRowTitle = "Auf Demo-Daten zurücksetzen"
    override val resetDemoRowSubtitle = "Lädt wieder den Beispiel-Datensatz"
    override val releaseNotesRowTitle = "Versionshinweise (v1.33)"
    override val releaseNotesRowSubtitle = "Neuerungen und Verbesserungen dieser Version"
    override val releaseNotesDialogTitle = "Installow v1.33 — Hinweise"
    override val releaseNotesDialogBody = "• Unterstützung für Türkisch, Englisch, Deutsch, Französisch, Spanisch und System.\n• Daten und Einstellungen getrennt; optimierte Header-Buttons und by kyhlaer.\n• Multi-Konto-Verwaltung und direkte Profilabfrage per Benutzername.\n• Schnellfilter für Notizen/Enge Freunde und 1-Tap-Kopieren.\n• Hardware-Verschlüsselung und R8-Optimierung beibehalten."
    override val releaseNotesClose = "Schließen"
    override val footerSignature = "Installow v1.33 · by kyhlaer"
    override val webSyncHeaderTitle = "Instagram Web-Verbindung"
    override val webSyncHeaderSubtitle = "Sicherer Modus · Passwort wird nie gespeichert"
    override val clearCookiesDesc = "Cookies löschen"
    override val refreshDesc = "Aktualisieren"
    override val webSyncProtectionBanner = "Schutz aktiv: 2-3s Pause pro Seite, 15-Min-Abkühlung, automatischer Stopp bei Limit."
    override val webSyncCooldownStatusFormat = "Kontoschutz aktiv: %d Min warten oder das sichere ZIP-Archiv nutzen."
    override val webSyncInitialStatus = "Oben bei Instagram anmelden und danach unten auf Abrufen tippen."
    override val webSyncSessionDetected = "Sitzung erkannt. Follower & Gefolgte können jetzt geladen werden."
    override val webSyncCookiesResetStatus = "Sitzung und Cookies gelöscht. Du kannst dich erneut anmelden."
    override val webSyncSslErrorStatus = "SSL/TLS-Zertifikat nicht vertrauenswürdig. Verbindung blockiert."
    override val webSyncVerifyingSession = "Sitzung wird geprüft..."
    override val webSyncFetchingButton = "Lädt im Schutzmodus..."
    override val webSyncCooldownButtonFormat = "Schutzpause (%d Min)"
    override val webSyncFetchButton = "Follower & Gefolgte abrufen"
    override val webSyncProcessingErrorPrefix = "Fehler beim Verarbeiten: "
    override val webSyncJsNoSession = "Keine aktive Sitzung gefunden. Bitte zuerst oben anmelden."
    override val webSyncJsFetchingFormat = " lädt sicher ("
    override val webSyncJsFollowersLabel = "Follower"
    override val webSyncJsFollowingLabel = "Gefolgt"
    override val webSyncJsRateLimited = "Instagram-Limit erkannt. Zum Kontoschutz für 60 Min gesperrt. Nutze das offizielle ZIP-Archiv."
    override val webSyncJsHttpErrorPrefix = "Instagram HTTP "
    override val webSyncJsHttpErrorSuffix = ". Sicher gestoppt."
    override val webSyncJsTakingBreakSuffix = " Konten) · Kurze Pause..."
    override val webSyncJsFollowersDoneBreak = "Follower fertig. Kurze Pause..."
    override val webSyncJsSaving = "Abgeschlossen, wird gespeichert..."
    override val webSyncJsGenericError = "Abruf sicher beendet."
    override val dateUnknown = "Datum unbekannt"
    override val relJustNow = "Gerade eben"
    override val relMinutesAgo = "vor %d Min."
    override val relHoursAgo = "vor %d Std."
    override val relDaysAgo = "vor %d T."
    override val relMonthsAgo = "vor %d Mon."
    override val relYearsAgo = "vor %d J."
    override val savedDataFallbackLabel = "Gespeicherte Daten"
    override val securityAuditMsgFormat = "Sicherheitstest beendet: %d / %d Prüfungen bestanden."
    override val cookiesClearedMsg = "Web-Cookies und Browser-Cache wurden geleert."
    override val whitelistAddedFormat = "@%s zur Whitelist hinzugefügt."
    override val whitelistRemovedFormat = "@%s von Whitelist entfernt."
    override val noteSavedFormat = "Tag und Notiz für @%s gespeichert."
    override val markedUnfollowedMsgFormat = "@%s als entfolgt markiert."
    override val queueNoCandidatesMsg = "Keine Konten außerhalb der Whitelist übrig!"
    override val queueCompletedMsg = "Prüfliste abgeschlossen!"
    override val queueAllReviewedMsg = "Alle Konten in der Prüfliste angesehen!"
    override val demoResetMsg = "Demo-Datensatz und Beispielverlauf geladen."
    override val simDiffMsgFormat = "Neuer Stand: %d Entfolger, %d neue Follower."
    override val simCreatedMsg = "Neue Änderungssimulation erstellt."
    override val importNotFoundMsg = "followers_1.json / following.json nicht gefunden. Bitte Instagram-ZIP oder JSON/HTML wählen."
    override val archiveOwnerFallback = "Mein Instagram-Archiv"
    override val archiveSourceLabelFormat = "Instagram-Archiv (%d Dateien)"
    override val importSuccessFormat = "Geladen: %d Follower, %d Gefolgt."
    override val importFailedPrefix = "Datei nicht lesbar: "
    override val unknownErrorText = "Unbekannter Fehler"
    override val invalidUsernameMsg = "Bitte gültigen Instagram-Benutzernamen eingeben."
    override val publicLookupFullSuccessFormat = "@%s aktualisiert: %d Follower, %d Gefolgt."
    override val publicLookupPrivateMsgFormat = "@%s ist privat (%d Follower, %d Gefolgt). Für Listen ZIP laden oder Web-Login nutzen."
    override val publicLookupCountsOnlyMsgFormat = "@%s Zahlen geladen (%d Follower, %d Gefolgt). Für vollständige Listen ZIP oder Web-Login nötig."
    override val publicLookupNotFoundMsgFormat = "@%s wurde nicht gefunden. Bitte Schreibweise prüfen."
    override val publicLookupErrorMsg = "Netzwerkfehler beim Prüfen des Instagram-Profils."
    override val accountSwitchedMsgFormat = "Aktives Konto zu @%s gewechselt."
    override val accountRemovedMsgFormat = "@%s aus gespeicherten Konten entfernt."
    override val cannotRemoveLastAccountMsg = "Mindestens ein Konto muss erhalten bleiben."
    override val webSyncSourceLabelFormat = "Live Web-Sync (@%s)"
    override val webSyncSuccessFormat = "@%s synchronisiert: %d Follower, %d Gefolgt."
    override val csvReportHeaderFormat = "Installow Bericht — %s"
    override val csvAccountSummaryFormat = "Konto: @%s | Gesamt: %d"
    override val csvColumnsHeader = "benutzername,anzeigename,status,tag,whitelist,profil_url"
    override val checkUrlTitle = "HTTPS & Domain-Whitelist"
    override val checkUrlPass = "HTTP, javascript:, file: und Phishing-Domains blockiert."
    override val checkUrlFail = "Domain-Prüfung fehlgeschlagen!"
    override val checkIntentTitle = "Intent-Weiterleitungsschutz"
    override val checkIntentPass = "Nur geprüfte Meta/Instagram-URLs zugelassen."
    override val checkIntentFail = "Intent-Filter fehlgeschlagen!"
    override val checkZipTitle = "Zip-Slip- & Archiv-Schutz"
    override val checkZipPass = "Pfad-Traversal (..) und absolute Pfade blockiert."
    override val checkZipFail = "Zip-Pfadprüfung fehlgeschlagen!"
    override val checkCsvTitle = "CSV-Formel-Schutz"
    override val checkCsvPass = "Formelpräfixe (=, +, -, @) und Zeilenumbrüche neutralisiert."
    override val checkCsvFail = "CSV-Test fehlgeschlagen!"
    override val checkNonceTitle = "256-Bit Nonce JS-Bridge"
    override val checkNoncePass = "Zeitkonstante Token-Prüfung aktiv (256-Bit)."
    override val checkNonceFail = "Token-Prüfung fehlgeschlagen!"
    override val checkAesTitle = "AES-256-GCM Keystore"
    override val checkAesPass = "Hardware-Verschlüsselung im Android Keystore verifiziert."
    override val checkAesFailPrefix = "Keystore-Test fehlgeschlagen: "
}

object FrStrings : AppStrings {
    override val language = AppLanguage.FR
    override val navDashboard = "Accueil"
    override val navConnections = "Contacts"
    override val navAnalytics = "Stats"
    override val navData = "Données"
    override val navSettings = "Réglages"
    override val exitDialogTitle = "Quitter Installow ?"
    override val exitDialogMessage = "Voulez-vous vraiment fermer Installow ?"
    override val exitConfirm = "Quitter"
    override val exitCancel = "Annuler"
    override val categoryMutual = "Mutuel"
    override val categoryNonFollower = "Ne te suit pas"
    override val categoryFan = "Te suit"
    override val categoryPending = "En attente"
    override val tabNonFollowers = "Ne te suivent pas"
    override val tabNonFollowersShort = "Non-abonnés"
    override val tabFans = "Fans (Non suivis)"
    override val tabFansShort = "Fans"
    override val tabMutuals = "Mutuels"
    override val tabMutualsShort = "Mutuels"
    override val tabAllFollowers = "Abonnés"
    override val tabAllFollowersShort = "Abonnés"
    override val tabAllFollowing = "Abonnements"
    override val tabAllFollowingShort = "Suivis"
    override val tabUnfollowersDiff = "Désabonnés"
    override val tabUnfollowersDiffShort = "Partis"
    override val tabPending = "Demandes en attente"
    override val tabPendingShort = "Demandes"
    override val tabWhitelist = "Liste blanche"
    override val tabWhitelistShort = "Protégés"
    override val sortNewestFirst = "Plus récents"
    override val sortOldestFirst = "Plus anciens"
    override val sortFollowersDesc = "Abonnés (Décroissant)"
    override val sortFollowersAsc = "Abonnés (Croissant)"
    override val sortUsernameAsc = "Pseudo (A → Z)"
    override val sortUsernameDesc = "Pseudo (Z → A)"
    override val sourceDemo = "Jeu de démo"
    override val sourcePublicLookup = "Profil public"
    override val sourceOfficialExport = "Archive officielle"
    override val sourceWebSync = "Sync Web en direct"
    override val presetTags = listOf(
        "Célébrité / Marque",
        "Ami proche",
        "Famille",
        "Travail / Réseau",
        "Créateur",
        "Suivi"
    )
    override val searchContentDesc = "Rechercher"
    override val uploadDataContentDesc = "Importer"
    override val metricNonFollowersTitle = "Ne suivent pas"
    override val metricNonFollowersHintWhitelist = "Hors liste blanche"
    override val metricNonFollowersHintDefault = "Pas de retour"
    override val metricFansTitle = "Non suivis en retour"
    override val metricFansHint = "Te suivent"
    override val metricFollowersTitle = "Abonnés"
    override val metricFollowingTitle = "Abonnements"
    override val metricAllListHint = "Liste complète"
    override val followBalanceTitle = "Équilibre"
    override val ratioFormat = "Ratio %sx"
    override val donutMutual = "Mutuel"
    override val donutNonFollower = "Ne suit pas"
    override val donutFan = "Fans"
    override val donutMatchLabel = "Mutuels"
    override val recentChangesTitle = "Changements récents"
    override val lastRecordPrefix = "Dernier relevé : "
    override val compareOnNextSync = "Comparé au prochain import"
    override val simulateAction = "Simuler"
    override val lostFollowersTitle = "Désabonnés"
    override val newFollowersTitle = "Nouveaux abonnés"
    override val noChangesText = "Aucun changement"
    override val mutualsRowTitle = "Mutuels"
    override val mutualsRowSubtitle = "Vous vous suivez mutuellement"
    override val pendingRowTitle = "Demandes en attente"
    override val pendingRowSubtitle = "Demandes envoyées sans réponse"
    override val whitelistRowTitle = "Liste blanche"
    override val whitelistRowSubtitle = "Exclus des alertes"
    override val smartQueueTitle = "Revue étape par étape"
    override val smartQueueDescFormat = "Examine %d comptes qui ne te suivent pas, ouvre-les sur Instagram ou ajoute-les en liste blanche."
    override val startReviewButton = "Démarrer la revue"
    override val unfollowedByMeRowTitle = "Retirés par toi"
    override val accountListNoticePrivateTitle = "Ce compte est privé"
    override val accountListNoticePrivateDesc = "Les compteurs d'abonnés et d'abonnements ont été récupérés. Ce profil étant privé, importe l'archive ZIP ou connecte-toi via Web pour voir les listes."
    override val accountListNoticeLoginDesc = "Les compteurs publics ont été récupérés. Instagram exige une session Web ou une archive ZIP officielle pour afficher les listes complètes."
    override val accountListNoticeNotFoundDesc = "Nom d'utilisateur introuvable sur Instagram. Vérifie l'orthographe."
    override val accountListNoticeErrorDesc = "Impossible de joindre Instagram pour le moment. Vérifie ta connexion ou importe un ZIP."
    override val loadArchiveActionShort = "Charger ZIP"
    override val webSignInActionShort = "Connexion Web"
    override val retryActionShort = "Réessayer"
    override val accountsSheetTitle = "Comptes Instagram"
    override val accountsSheetSubtitle = "Rechercher un pseudo, ajouter ou changer de compte"
    override val addOrLookupAccountTitle = "Ajouter / Ouvrir par pseudo"
    override val usernameInputPlaceholder = "ex. kyhlaer"
    override val lookupAndAddButton = "Récupérer & Ajouter"
    override val lookingUpAccountButton = "Vérification..."
    override val savedAccountsTitle = "Comptes enregistrés (%d)"
    override val activeBadge = "Actif"
    override val privateBadge = "Privé"
    override val publicBadge = "Public"
    override val removeAccountDesc = "Supprimer le compte"
    override val accountCountsShortFormat = "%d abonnés · %d suivis"
    override val publicLookupBannerTitle = "Recherche rapide par pseudo"
    override val publicLookupBannerDesc = "Récupère les compteurs en direct (et listes ouvertes si disponibles) sans fichier ZIP."
    override val copyUsernameDesc = "Copier le pseudo"
    override val usernameCopiedMsgFormat = "@%s copié dans le presse-papiers."
    override val allTagsFilterChip = "Tous"
    override val onlyNotesFilterChip = "Avec notes"
    override val onlyCloseFriendsChip = "Amis proches"
    override val dataTabTitle = "Données & Comptes"
    override val dataTabSubtitle = "Gérer les comptes, récupérer un profil ou importer un ZIP"
    override val settingsTabTitle = "Réglages"
    override val settingsTabSubtitle = "Thème, langue, bouclier de sécurité et version"
    override val accountsCardTitle = "Gestion des comptes"
    override val accountsCardDescFormat = "Actif : @%s · %d comptes enregistrés"
    override val manageAccountsButton = "Gérer / Changer de compte"
    override val connectionsCountFormat = "%d comptes · %s"
    override val sortContentDesc = "Trier"
    override val shareContentDesc = "Partager"
    override val shareReportSubjectPrefix = "Installow - "
    override val shareChooserTitle = "Partager / Exporter"
    override val searchPlaceholder = "Pseudo, nom ou note"
    override val clearContentDesc = "Effacer"
    override val whitelistHidden = "Liste blanche masquée"
    override val whitelistIncluded = "Liste blanche incluse"
    override val smartQueueChip = "Revue rapide"
    override val followerCountZipNotice = "Les archives ZIP Instagram officielles n'incluent pas le nombre d'abonnés des autres comptes."
    override val emptyMatchesTitle = "Aucun résultat"
    override val emptyMatchesSubtitle = "Modifie ou efface ta recherche."
    override val openInInstagramDesc = "Ouvrir sur Instagram"
    override val followerCountSuffix = " abonnés"
    override val smartQueueHeaderFormat = "Revue · %d / %d"
    override val closeDesc = "Fermer"
    override val openInIgShort = "Ouvrir IG"
    override val markedUnfollowedShort = "Retiré"
    override val prevButton = "Préc."
    override val nextButton = "Suiv."
    override val finishButton = "Terminer"
    override val closeFriendBadge = "Ami proche"
    override val whitelistBadgeDesc = "Liste blanche"
    override val followerCountDetailPrefix = "Abonnés : "
    override val followedMeDatePrefix = "Te suit depuis : "
    override val iFollowedDatePrefix = "Suivi depuis : "
    override val pendingDatePrefix = "Demande envoyée : "
    override val whitelistSwitchTitle = "Liste blanche"
    override val whitelistSwitchSubtitle = "Exclure des alertes de désabonnement."
    override val tagSectionTitle = "Étiquette"
    override val personalNoteLabel = "Note personnelle"
    override val personalNotePlaceholder = "Ajouter une note..."
    override val saveButton = "Enregistrer"
    override val openInstagramButton = "Ouvrir sur Instagram"
    override val markUnfollowedButton = "Marquer comme retiré"
    override val analyticsTitle = "Statistiques"
    override val analyticsSubtitle = "Ratio d'abonnements, activité mensuelle et historique"
    override val accountStatusLabel = "Santé du compte"
    override val healthStrong = "Forte réciprocité"
    override val healthBalanced = "Profil équilibré"
    override val healthCleanupSuggested = "Tri conseillé"
    override val matchBadgeFormat = "%d%% Mutuels"
    override val metricReturnRate = "Retour"
    override val metricNonFollowerShort = "Non-abonnés"
    override val metricFansShort = "Fans"
    override val last6MonthsTitle = "6 derniers mois"
    override val last6MonthsSubtitle = "Répartition par date d'activité"
    override val legendNewFollowers = "Nouveaux abonnés"
    override val legendNewFollowing = "Nouveaux suivis"
    override val highlightsTitle = "Points clés"
    override val firstFollowerLabel = "Premier abonné"
    override val oldestNonFollowerLabel = "Plus ancien non-réciproque"
    override val longestPendingLabel = "Plus ancienne demande"
    override val snapshotHistoryFormat = "Historique (%d)"
    override val followersCountUnit = "%d abonnés"
    override val followingCountUnit = "%d suivis"
    override val dataSettingsTitle = "Données & Réglages"
    override val dataSettingsSubtitle = "Importer des données, exporter et configurer l'app"
    override val officialArchiveTitle = "Archive Instagram officielle"
    override val zeroRiskRecommendedBadge = "Risque zéro · Recommandé"
    override val officialArchiveDesc = "Aucune requête envoyée à Instagram. Analyse les archives ZIP ou JSON totalement hors ligne."
    override val processingFilesText = "Traitement des fichiers..."
    override val selectArchiveButton = "Choisir ZIP / JSON / HTML"
    override val howToExportTitle = "Comment télécharger vos données ?"
    override val guideStep1 = "Ouvre Instagram > Profil > Menu (≡) > Espace Comptes."
    override val guideStep2 = "Vos informations et autorisations > Exporter vos informations."
    override val guideStep3 = "Choisis uniquement 'Abonnés et suivis' et le format 'JSON'."
    override val guideStep4 = "Sélectionne le fichier .zip ou JSON téléchargé ci-dessus."
    override val liveWebSyncTitle = "Sync Web en direct"
    override val slowModeBadge = "Mode lent"
    override val liveWebSyncDesc = "Lecture seule via instagram.com dans le navigateur intégré avec pauses et délai de 15 min."
    override val connectInstagramWebButton = "Connexion Instagram Web"
    override val appearanceTitle = "Apparence"
    override val themeDark = "Sombre"
    override val themeLight = "Clair"
    override val themeSystem = "Système"
    override val languageTitle = "Langue / Language"
    override val langTurkish = "Türkçe"
    override val langEnglish = "English"
    override val langGerman = "Deutsch"
    override val langFrench = "Français"
    override val langSpanish = "Español"
    override val langSystem = "Système"
    override val cyberShieldTitle = "Bouclier de sécurité"
    override val cyberShieldActiveBadge = "Actif"
    override val cyberShieldVerifiedFormat = "%d/%d Vérifiés"
    override val shieldLineAes = "Chiffrement local AES-256-GCM Keystore"
    override val shieldLineHttps = "HTTPS strict & liste blanche Meta/Instagram"
    override val shieldLineBridge = "Pont JS Nonce 256 bits & FLAG_SECURE"
    override val shieldLineZipCsv = "Protection Zip Slip, ZIP bomb & injection CSV"
    override val shieldLineBackup = "Sauvegarde cloud et trafic HTTP en clair désactivés"
    override val runSecurityTestButton = "Lancer l'audit de sécurité"
    override val clearWebCookiesButton = "Effacer cookies et cache Web"
    override val exportCsvRowTitle = "Partager la liste en CSV"
    override val exportCsvRowSubtitleFormat = "%s (%d comptes)"
    override val simulateRowTitle = "Simuler un changement"
    override val simulateRowSubtitle = "Ajoute un exemple de changement d'abonnés"
    override val resetDemoRowTitle = "Réinitialiser les données démo"
    override val resetDemoRowSubtitle = "Restaure le jeu d'exemple par défaut"
    override val releaseNotesRowTitle = "Notes de version (v1.33)"
    override val releaseNotesRowSubtitle = "Voir les nouveautés de cette mise à jour"
    override val releaseNotesDialogTitle = "Installow v1.33 — Nouveautés"
    override val releaseNotesDialogBody = "• Ajout du turc, anglais, allemand, français, espagnol et langue système.\n• Séparation des onglets Données et Réglages ; boutons espacés et signature by kyhlaer.\n• Gestion multi-comptes et recherche directe par nom d'utilisateur.\n• Filtres rapides par notes/amis proches et copie en 1 clic.\n• Chiffrement AES-256-GCM et optimisation R8 maintenus."
    override val releaseNotesClose = "Fermer"
    override val footerSignature = "Installow v1.33 · by kyhlaer"
    override val webSyncHeaderTitle = "Connexion Instagram Web"
    override val webSyncHeaderSubtitle = "Mode lent sûr · Mot de passe jamais stocké"
    override val clearCookiesDesc = "Effacer cookies"
    override val refreshDesc = "Actualiser"
    override val webSyncProtectionBanner = "Protection active : pause 2-3s par page, délai 15 min, arrêt auto sur avertissement."
    override val webSyncCooldownStatusFormat = "Pause de protection active : attends %d min ou utilise le ZIP officiel sans risque."
    override val webSyncInitialStatus = "Connecte-toi ci-dessus puis touche le bouton ci-dessous."
    override val webSyncSessionDetected = "Session détectée. Tu peux récupérer abonnés et suivis."
    override val webSyncCookiesResetStatus = "Session et cookies réinitialisés. Tu peux te reconnecter."
    override val webSyncSslErrorStatus = "Certificat SSL/TLS invalide. Connexion bloquée."
    override val webSyncVerifyingSession = "Vérification de session..."
    override val webSyncFetchingButton = "Récupération sécurisée..."
    override val webSyncCooldownButtonFormat = "Pause protection (%d min)"
    override val webSyncFetchButton = "Récupérer abonnés & suivis"
    override val webSyncProcessingErrorPrefix = "Erreur de traitement : "
    override val webSyncJsNoSession = "Aucune session active. Connecte-toi d'abord ci-dessus."
    override val webSyncJsFetchingFormat = " récupération sécurisée ("
    override val webSyncJsFollowersLabel = "Abonnés"
    override val webSyncJsFollowingLabel = "Abonnements"
    override val webSyncJsRateLimited = "Limite Instagram détectée. Arrêté par sécurité pour 60 min. Utilise l'archive ZIP."
    override val webSyncJsHttpErrorPrefix = "Instagram HTTP "
    override val webSyncJsHttpErrorSuffix = ". Arrêt sécurisé."
    override val webSyncJsTakingBreakSuffix = " comptes) · Courte pause..."
    override val webSyncJsFollowersDoneBreak = "Abonnés terminés. Courte pause..."
    override val webSyncJsSaving = "Terminé, enregistrement..."
    override val webSyncJsGenericError = "Synchronisation arrêtée avec succès."
    override val dateUnknown = "Date inconnue"
    override val relJustNow = "À l'instant"
    override val relMinutesAgo = "il y a %d min"
    override val relHoursAgo = "il y a %d h"
    override val relDaysAgo = "il y a %d j"
    override val relMonthsAgo = "il y a %d mois"
    override val relYearsAgo = "il y a %d an(s)"
    override val savedDataFallbackLabel = "Données sauvées"
    override val securityAuditMsgFormat = "Audit terminé : %d / %d contrôles validés."
    override val cookiesClearedMsg = "Cookies Web et cache navigateur effacés."
    override val whitelistAddedFormat = "@%s ajouté à la liste blanche."
    override val whitelistRemovedFormat = "@%s retiré de la liste blanche."
    override val noteSavedFormat = "Étiquette et note enregistrées pour @%s."
    override val markedUnfollowedMsgFormat = "@%s marqué comme retiré."
    override val queueNoCandidatesMsg = "Aucun compte hors liste blanche à examiner !"
    override val queueCompletedMsg = "Revue terminée !"
    override val queueAllReviewedMsg = "Tous les comptes de la liste ont été vérifiés !"
    override val demoResetMsg = "Données de démonstration rechargées."
    override val simDiffMsgFormat = "Nouveau relevé : %d désabonnés, %d nouveaux."
    override val simCreatedMsg = "Nouvel instantané de test créé."
    override val importNotFoundMsg = "followers_1.json / following.json introuvable. Sélectionne une archive ZIP Instagram ou un fichier JSON/HTML."
    override val archiveOwnerFallback = "Mon archive Instagram"
    override val archiveSourceLabelFormat = "Archive Instagram (%d fichiers)"
    override val importSuccessFormat = "Importé : %d abonnés, %d suivis."
    override val importFailedPrefix = "Impossible de lire : "
    override val unknownErrorText = "Erreur inconnue"
    override val invalidUsernameMsg = "Saisis un nom d'utilisateur Instagram valide."
    override val publicLookupFullSuccessFormat = "@%s synchronisé : %d abonnés, %d suivis."
    override val publicLookupPrivateMsgFormat = "@%s est privé (%d abonnés, %d suivis). Importe un ZIP ou connecte-toi via Web pour les listes."
    override val publicLookupCountsOnlyMsgFormat = "@%s compteurs chargés (%d abonnés, %d suivis). Session Web ou ZIP requis pour les listes."
    override val publicLookupNotFoundMsgFormat = "@%s est introuvable. Vérifie le pseudo."
    override val publicLookupErrorMsg = "Erreur réseau lors de la vérification du profil."
    override val accountSwitchedMsgFormat = "Compte actif basculé vers @%s."
    override val accountRemovedMsgFormat = "@%s supprimé des comptes enregistrés."
    override val cannotRemoveLastAccountMsg = "Au moins un compte doit rester enregistré."
    override val webSyncSourceLabelFormat = "Sync Web (@%s)"
    override val webSyncSuccessFormat = "@%s synchronisé : %d abonnés, %d suivis."
    override val csvReportHeaderFormat = "Rapport Installow — %s"
    override val csvAccountSummaryFormat = "Compte : @%s | Total : %d"
    override val csvColumnsHeader = "pseudo,nom_affiche,statut,etiquette,liste_blanche,url_profil"
    override val checkUrlTitle = "HTTPS & Liste blanche"
    override val checkUrlPass = "HTTP, javascript:, file: et domaines suspects bloqués."
    override val checkUrlFail = "Échec validation domaine !"
    override val checkIntentTitle = "Protection Intent"
    override val checkIntentPass = "Seules les URL officielles Meta/Instagram sont autorisées."
    override val checkIntentFail = "Échec filtre Intent !"
    override val checkZipTitle = "Protection Zip Slip"
    override val checkZipPass = "Chemins absolus et traversée (..) bloqués."
    override val checkZipFail = "Échec vérification ZIP !"
    override val checkCsvTitle = "Protection injection CSV"
    override val checkCsvPass = "Préfixes (=, +, -, @) et sauts de ligne neutralisés."
    override val checkCsvFail = "Échec test CSV !"
    override val checkNonceTitle = "Pont JS Nonce 256 bits"
    override val checkNoncePass = "Vérification en temps constant active (256 bits)."
    override val checkNonceFail = "Échec vérification jeton !"
    override val checkAesTitle = "Chiffrement Keystore AES-256-GCM"
    override val checkAesPass = "Chiffrement/déchiffrement matériel vérifié."
    override val checkAesFailPrefix = "Échec test Keystore : "
}

object EsStrings : AppStrings {
    override val language = AppLanguage.ES
    override val navDashboard = "Inicio"
    override val navConnections = "Gente"
    override val navAnalytics = "Análisis"
    override val navData = "Datos"
    override val navSettings = "Ajustes"
    override val exitDialogTitle = "¿Salir de Installow?"
    override val exitDialogMessage = "¿Seguro que quieres cerrar Installow?"
    override val exitConfirm = "Salir"
    override val exitCancel = "Cancelar"
    override val categoryMutual = "Mutuo"
    override val categoryNonFollower = "No te sigue"
    override val categoryFan = "Te sigue"
    override val categoryPending = "Pendiente"
    override val tabNonFollowers = "No te siguen"
    override val tabNonFollowersShort = "No siguen"
    override val tabFans = "Fans (No sigues)"
    override val tabFansShort = "Fans"
    override val tabMutuals = "Mutuos"
    override val tabMutualsShort = "Mutuos"
    override val tabAllFollowers = "Seguidores"
    override val tabAllFollowersShort = "Seguidores"
    override val tabAllFollowing = "Seguidos"
    override val tabAllFollowingShort = "Seguidos"
    override val tabUnfollowersDiff = "Dejaron de seguir"
    override val tabUnfollowersDiffShort = "Bajas"
    override val tabPending = "Solicitudes pendientes"
    override val tabPendingShort = "Solicitudes"
    override val tabWhitelist = "Lista blanca"
    override val tabWhitelistShort = "Protegidos"
    override val sortNewestFirst = "Más recientes"
    override val sortOldestFirst = "Más antiguos"
    override val sortFollowersDesc = "Seguidores (Mayor → Menor)"
    override val sortFollowersAsc = "Seguidores (Menor → Mayor)"
    override val sortUsernameAsc = "Usuario (A → Z)"
    override val sortUsernameDesc = "Usuario (Z → A)"
    override val sourceDemo = "Datos de demo"
    override val sourcePublicLookup = "Perfil público"
    override val sourceOfficialExport = "Archivo oficial"
    override val sourceWebSync = "Sync Web en vivo"
    override val presetTags = listOf(
        "Famoso / Marca",
        "Mejor amigo",
        "Familia",
        "Trabajo / Red",
        "Creador",
        "Control"
    )
    override val searchContentDesc = "Buscar"
    override val uploadDataContentDesc = "Importar datos"
    override val metricNonFollowersTitle = "No te siguen"
    override val metricNonFollowersHintWhitelist = "Sin lista blanca"
    override val metricNonFollowersHintDefault = "Sin retorno"
    override val metricFansTitle = "No sigues"
    override val metricFansHint = "Te siguen"
    override val metricFollowersTitle = "Seguidores"
    override val metricFollowingTitle = "Seguidos"
    override val metricAllListHint = "Lista completa"
    override val followBalanceTitle = "Balance"
    override val ratioFormat = "Ratio %sx"
    override val donutMutual = "Mutuo"
    override val donutNonFollower = "No te sigue"
    override val donutFan = "Fans"
    override val donutMatchLabel = "Mutuos"
    override val recentChangesTitle = "Cambios recientes"
    override val lastRecordPrefix = "Último registro: "
    override val compareOnNextSync = "Comparado en el próximo import"
    override val simulateAction = "Simular"
    override val lostFollowersTitle = "Bajas"
    override val newFollowersTitle = "Nuevos seguidores"
    override val noChangesText = "Sin cambios"
    override val mutualsRowTitle = "Mutuos"
    override val mutualsRowSubtitle = "Os seguís mutuamente"
    override val pendingRowTitle = "Solicitudes pendientes"
    override val pendingRowSubtitle = "Enviadas sin respuesta"
    override val whitelistRowTitle = "Lista blanca"
    override val whitelistRowSubtitle = "Excluidos de alertas"
    override val smartQueueTitle = "Revisión paso a paso"
    override val smartQueueDescFormat = "Revisa %d cuentas que no te siguen una por una, ábrelas en Instagram o protégelas en tu lista blanca."
    override val startReviewButton = "Iniciar revisión"
    override val unfollowedByMeRowTitle = "Dejados de seguir por ti"
    override val accountListNoticePrivateTitle = "Esta cuenta es privada"
    override val accountListNoticePrivateDesc = "Se obtuvieron los contadores de seguidores y seguidos. Al ser privada, carga un archivo ZIP oficial o inicia sesión Web para ver las listas."
    override val accountListNoticeLoginDesc = "Se obtuvieron los contadores públicos. Instagram requiere sesión Web o archivo ZIP oficial para mostrar listas completas."
    override val accountListNoticeNotFoundDesc = "No se encontró el usuario en Instagram. Revisa cómo está escrito."
    override val accountListNoticeErrorDesc = "No se pudo consultar Instagram ahora mismo. Comprueba tu conexión o importa un ZIP."
    override val loadArchiveActionShort = "Cargar ZIP"
    override val webSignInActionShort = "Entrar Web"
    override val retryActionShort = "Reintentar"
    override val accountsSheetTitle = "Cuentas de Instagram"
    override val accountsSheetSubtitle = "Consulta un usuario, añade cuentas o cambia al instante"
    override val addOrLookupAccountTitle = "Añadir / Abrir por usuario"
    override val usernameInputPlaceholder = "ej. kyhlaer"
    override val lookupAndAddButton = "Buscar y Añadir"
    override val lookingUpAccountButton = "Consultando..."
    override val savedAccountsTitle = "Cuentas guardadas (%d)"
    override val activeBadge = "Activa"
    override val privateBadge = "Privada"
    override val publicBadge = "Pública"
    override val removeAccountDesc = "Eliminar cuenta"
    override val accountCountsShortFormat = "%d seguidores · %d seguidos"
    override val publicLookupBannerTitle = "Consulta rápida por usuario"
    override val publicLookupBannerDesc = "Obtén contadores reales (y listas públicas si es posible) solo con el usuario, sin ZIP."
    override val copyUsernameDesc = "Copiar usuario"
    override val usernameCopiedMsgFormat = "@%s copiado al portapapeles."
    override val allTagsFilterChip = "Todos"
    override val onlyNotesFilterChip = "Con notas"
    override val onlyCloseFriendsChip = "Mejores amigos"
    override val dataTabTitle = "Datos y Cuentas"
    override val dataTabSubtitle = "Gestionar cuentas, buscar perfiles o importar ZIP"
    override val settingsTabTitle = "Ajustes"
    override val settingsTabSubtitle = "Tema, idioma, escudo de seguridad y versión"
    override val accountsCardTitle = "Gestión de cuentas"
    override val accountsCardDescFormat = "Activa: @%s · %d cuentas guardadas"
    override val manageAccountsButton = "Gestionar / Cambiar cuentas"
    override val connectionsCountFormat = "%d cuentas · %s"
    override val sortContentDesc = "Ordenar"
    override val shareContentDesc = "Compartir"
    override val shareReportSubjectPrefix = "Installow - "
    override val shareChooserTitle = "Compartir / Exportar informe"
    override val searchPlaceholder = "Buscar usuario, nombre o nota"
    override val clearContentDesc = "Borrar"
    override val whitelistHidden = "Lista blanca oculta"
    override val whitelistIncluded = "Lista blanca visible"
    override val smartQueueChip = "Revisión rápida"
    override val followerCountZipNotice = "Los ZIP oficiales de Instagram no incluyen los seguidores de otras cuentas."
    override val emptyMatchesTitle = "Sin coincidencias"
    override val emptyMatchesSubtitle = "Prueba limpiando o ajustando la búsqueda."
    override val openInInstagramDesc = "Abrir en Instagram"
    override val followerCountSuffix = " seguidores"
    override val smartQueueHeaderFormat = "Revisión · %d / %d"
    override val closeDesc = "Cerrar"
    override val openInIgShort = "Abrir IG"
    override val markedUnfollowedShort = "Eliminado"
    override val prevButton = "Ant."
    override val nextButton = "Sig."
    override val finishButton = "Fin"
    override val closeFriendBadge = "Mejor amigo"
    override val whitelistBadgeDesc = "Lista blanca"
    override val followerCountDetailPrefix = "Seguidores: "
    override val followedMeDatePrefix = "Te sigue desde: "
    override val iFollowedDatePrefix = "Sigues desde: "
    override val pendingDatePrefix = "Solicitud enviada: "
    override val whitelistSwitchTitle = "Lista blanca"
    override val whitelistSwitchSubtitle = "Excluir de alertas de no seguimiento."
    override val tagSectionTitle = "Etiqueta"
    override val personalNoteLabel = "Nota personal"
    override val personalNotePlaceholder = "Añadir una nota..."
    override val saveButton = "Guardar"
    override val openInstagramButton = "Abrir en Instagram"
    override val markUnfollowedButton = "Marcar como dejado"
    override val analyticsTitle = "Análisis"
    override val analyticsSubtitle = "Ratio de seguimiento, actividad mensual e historial"
    override val accountStatusLabel = "Estado de cuenta"
    override val healthStrong = "Alta reciprocidad"
    override val healthBalanced = "Perfil equilibrado"
    override val healthCleanupSuggested = "Limpieza sugerida"
    override val matchBadgeFormat = "%d%% Mutuos"
    override val metricReturnRate = "Retorno"
    override val metricNonFollowerShort = "No siguen"
    override val metricFansShort = "Fans"
    override val last6MonthsTitle = "Últimos 6 meses"
    override val last6MonthsSubtitle = "Actividad mensual según fecha"
    override val legendNewFollowers = "Nuevos seguidores"
    override val legendNewFollowing = "Nuevos seguidos"
    override val highlightsTitle = "Destacados"
    override val firstFollowerLabel = "Seguidor más antiguo"
    override val oldestNonFollowerLabel = "Seguido unilateral más antiguo"
    override val longestPendingLabel = "Solicitud más antigua"
    override val snapshotHistoryFormat = "Historial (%d)"
    override val followersCountUnit = "%d seguidores"
    override val followingCountUnit = "%d seguidos"
    override val dataSettingsTitle = "Datos y Ajustes"
    override val dataSettingsSubtitle = "Importar datos, exportar y configurar preferencias"
    override val officialArchiveTitle = "Archivo oficial de Instagram"
    override val zeroRiskRecommendedBadge = "Riesgo cero · Recomendado"
    override val officialArchiveDesc = "No envía peticiones a Instagram. Analiza ZIP o JSON totalmente sin conexión."
    override val processingFilesText = "Procesando archivos..."
    override val selectArchiveButton = "Elegir ZIP / JSON / HTML"
    override val howToExportTitle = "¿Cómo descargar tus datos?"
    override val guideStep1 = "Abre Instagram > Perfil > Menú (≡) > Centro de cuentas."
    override val guideStep2 = "Tu información y permisos > Exportar tu información."
    override val guideStep3 = "Elige solo 'Seguidores y seguidos' en formato 'JSON'."
    override val guideStep4 = "Selecciona el archivo .zip o JSON arriba."
    override val liveWebSyncTitle = "Sincronización Web en vivo"
    override val slowModeBadge = "Modo lento"
    override val liveWebSyncDesc = "Lectura segura con tu sesión de instagram.com en el navegador integrado con pausas y protección de 15 min."
    override val connectInstagramWebButton = "Conectar vía Instagram Web"
    override val appearanceTitle = "Apariencia"
    override val themeDark = "Oscuro"
    override val themeLight = "Claro"
    override val themeSystem = "Sistema"
    override val languageTitle = "Idioma / Language"
    override val langTurkish = "Türkçe"
    override val langEnglish = "English"
    override val langGerman = "Deutsch"
    override val langFrench = "Français"
    override val langSpanish = "Español"
    override val langSystem = "Sistema"
    override val cyberShieldTitle = "Escudo de seguridad"
    override val cyberShieldActiveBadge = "Activo"
    override val cyberShieldVerifiedFormat = "%d/%d Verificados"
    override val shieldLineAes = "Cifrado local AES-256-GCM en Android Keystore"
    override val shieldLineHttps = "HTTPS estricto y lista blanca Meta/Instagram"
    override val shieldLineBridge = "Puente JS Nonce 256 bits y bloqueo FLAG_SECURE"
    override val shieldLineZipCsv = "Protección Zip Slip, bomba ZIP e inyección CSV"
    override val shieldLineBackup = "Copia en la nube y tráfico HTTP bloqueados"
    override val runSecurityTestButton = "Ejecutar test de seguridad"
    override val clearWebCookiesButton = "Limpiar cookies y caché Web"
    override val exportCsvRowTitle = "Compartir lista como CSV"
    override val exportCsvRowSubtitleFormat = "%s (%d cuentas)"
    override val simulateRowTitle = "Simular cambios"
    override val simulateRowSubtitle = "Añade un ejemplo de bajas y altas"
    override val resetDemoRowTitle = "Restablecer datos demo"
    override val resetDemoRowSubtitle = "Restaura el conjunto de prueba inicial"
    override val releaseNotesRowTitle = "Notas de la versión (v1.33)"
    override val releaseNotesRowSubtitle = "Ver novedades y mejoras incluidas"
    override val releaseNotesDialogTitle = "Installow v1.33 — Notas"
    override val releaseNotesDialogBody = "• Soporte para turco, inglés, alemán, francés, español y sistema.\n• Pestañas Datos y Ajustes separadas; botones mejorados y firma by kyhlaer.\n• Gestión multi-cuenta y consulta directa por nombre de usuario.\n• Filtros rápidos de notas/amigos cercanos y copia rápida.\n• Cifrado AES-256-GCM y optimización R8 activos."
    override val releaseNotesClose = "Cerrar"
    override val footerSignature = "Installow v1.33 · by kyhlaer"
    override val webSyncHeaderTitle = "Conexión Instagram Web"
    override val webSyncHeaderSubtitle = "Modo lento seguro · Sin guardar contraseñas"
    override val clearCookiesDesc = "Limpiar cookies"
    override val refreshDesc = "Actualizar"
    override val webSyncProtectionBanner = "Protección activa: pausa de 2-3s por página, espera de 15 min y parada automática ante avisos."
    override val webSyncCooldownStatusFormat = "Espera de protección activa: espera %d min o usa el archivo ZIP sin riesgo."
    override val webSyncInitialStatus = "Inicia sesión arriba y luego pulsa el botón inferior."
    override val webSyncSessionDetected = "Sesión detectada. Ya puedes obtener seguidores y seguidos."
    override val webSyncCookiesResetStatus = "Sesión y cookies borradas. Puedes volver a entrar."
    override val webSyncSslErrorStatus = "Certificado SSL/TLS no válido. Conexión bloqueada."
    override val webSyncVerifyingSession = "Verificando sesión..."
    override val webSyncFetchingButton = "Obteniendo en modo seguro..."
    override val webSyncCooldownButtonFormat = "Pausa de protección (%d min)"
    override val webSyncFetchButton = "Obtener seguidores y seguidos"
    override val webSyncProcessingErrorPrefix = "Error al procesar datos: "
    override val webSyncJsNoSession = "Sin sesión activa. Inicia sesión en Instagram arriba primero."
    override val webSyncJsFetchingFormat = " obteniendo de forma segura ("
    override val webSyncJsFollowersLabel = "Seguidores"
    override val webSyncJsFollowingLabel = "Seguidos"
    override val webSyncJsRateLimited = "Límite de Instagram detectado. Bloqueado 60 min para proteger tu cuenta. Usa el ZIP oficial."
    override val webSyncJsHttpErrorPrefix = "Instagram devolvió HTTP "
    override val webSyncJsHttpErrorSuffix = ". Detenido con seguridad."
    override val webSyncJsTakingBreakSuffix = " cuentas) · Pausa breve..."
    override val webSyncJsFollowersDoneBreak = "Seguidores completados. Pausa breve..."
    override val webSyncJsSaving = "Completado, guardando..."
    override val webSyncJsGenericError = "Sincronización detenida con seguridad."
    override val dateUnknown = "Fecha desconocida"
    override val relJustNow = "Ahora"
    override val relMinutesAgo = "hace %d min"
    override val relHoursAgo = "hace %d h"
    override val relDaysAgo = "hace %d d"
    override val relMonthsAgo = "hace %d m"
    override val relYearsAgo = "hace %d a"
    override val savedDataFallbackLabel = "Datos guardados"
    override val securityAuditMsgFormat = "Auditoría lista: %d / %d escudos verificados."
    override val cookiesClearedMsg = "Cookies Web y caché eliminadas."
    override val whitelistAddedFormat = "@%s añadido a la lista blanca."
    override val whitelistRemovedFormat = "@%s quitado de la lista blanca."
    override val noteSavedFormat = "Etiqueta y nota guardadas para @%s."
    override val markedUnfollowedMsgFormat = "@%s marcado como dejado de seguir."
    override val queueNoCandidatesMsg = "No quedan cuentas fuera de lista blanca."
    override val queueCompletedMsg = "¡Revisión completada!"
    override val queueAllReviewedMsg = "¡Todas las cuentas de la cola revisadas!"
    override val demoResetMsg = "Datos de demostración restaurados."
    override val simDiffMsgFormat = "Nuevo registro: %d bajas, %d nuevos."
    override val simCreatedMsg = "Instantánea de prueba creada."
    override val importNotFoundMsg = "No se encontró followers_1.json / following.json. Elige tu ZIP de Instagram o JSON/HTML."
    override val archiveOwnerFallback = "Mi archivo Instagram"
    override val archiveSourceLabelFormat = "Archivo Instagram (%d archivos)"
    override val importSuccessFormat = "Cargado: %d seguidores, %d seguidos."
    override val importFailedPrefix = "Error leyendo archivo: "
    override val unknownErrorText = "Error desconocido"
    override val invalidUsernameMsg = "Introduce un usuario de Instagram válido."
    override val publicLookupFullSuccessFormat = "@%s sincronizado: %d seguidores, %d seguidos."
    override val publicLookupPrivateMsgFormat = "@%s es privada (%d seguidores, %d seguidos). Carga ZIP o entra vía Web para ver listas."
    override val publicLookupCountsOnlyMsgFormat = "@%s contadores obtenidos (%d seguidores, %d seguidos). Requiere sesión Web o ZIP para listas."
    override val publicLookupNotFoundMsgFormat = "No se encontró @%s. Comprueba el nombre."
    override val publicLookupErrorMsg = "Error de red al consultar el perfil."
    override val accountSwitchedMsgFormat = "Cuenta activa cambiada a @%s."
    override val accountRemovedMsgFormat = "@%s eliminada de cuentas guardadas."
    override val cannotRemoveLastAccountMsg = "Debe permanecer al menos una cuenta."
    override val webSyncSourceLabelFormat = "Sync Web (@%s)"
    override val webSyncSuccessFormat = "@%s sincronizado: %d seguidores, %d seguidos."
    override val csvReportHeaderFormat = "Informe Installow — %s"
    override val csvAccountSummaryFormat = "Cuenta: @%s | Total: %d"
    override val csvColumnsHeader = "usuario,nombre_visible,estado,etiqueta,lista_blanca,url_perfil"
    override val checkUrlTitle = "HTTPS y Lista blanca"
    override val checkUrlPass = "HTTP, javascript:, file: y phishing bloqueados."
    override val checkUrlFail = "¡Falló validación de dominio!"
    override val checkIntentTitle = "Protección de Intent"
    override val checkIntentPass = "Solo enlaces Meta/Instagram verificados."
    override val checkIntentFail = "¡Falló filtro Intent!"
    override val checkZipTitle = "Protección Zip Slip"
    override val checkZipPass = "Rutas absolutas y saltos (..) bloqueados."
    override val checkZipFail = "¡Falló validación ZIP!"
    override val checkCsvTitle = "Protección inyección CSV"
    override val checkCsvPass = "Prefijos (=, +, -, @) y saltos neutralizados."
    override val checkCsvFail = "¡Falló test CSV!"
    override val checkNonceTitle = "Puente JS Nonce 256 bits"
    override val checkNoncePass = "Verificación de tiempo constante activa (256 bits)."
    override val checkNonceFail = "¡Falló validación de token!"
    override val checkAesTitle = "Cifrado Keystore AES-256-GCM"
    override val checkAesPass = "Cifrado y descifrado hardware verificado."
    override val checkAesFailPrefix = "Error en Keystore: "
}

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.TR -> TrStrings
    AppLanguage.EN -> EnStrings
    AppLanguage.DE -> DeStrings
    AppLanguage.FR -> FrStrings
    AppLanguage.ES -> EsStrings
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { TrStrings }
