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
        LanguagePreference.SYSTEM -> {
            if (systemLocale.language.equals("tr", ignoreCase = true)) {
                AppLanguage.TR
            } else {
                AppLanguage.EN
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
    val navDataSync: String
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
        AppScreen.DATA_SYNC -> navDataSync
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
        DataSourceType.OFFICIAL_EXPORT -> sourceOfficialExport
        DataSourceType.WEB_SYNC -> sourceWebSync
    }
}

object TrStrings : AppStrings {
    override val language = AppLanguage.TR
    override val navDashboard = "Panel"
    override val navConnections = "Kişiler"
    override val navAnalytics = "Analiz"
    override val navDataSync = "Veri"
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
    override val releaseNotesDialogBody = "• İngilizce dil desteği eklendi (Türkçe / English / Sistem).\n• Takipçi sayısı formatları (B/K), tarihler, grafik ayları, CSV raporları ve güvenlik denetim sonuçları seçilen dile uyarlandı.\n• Veri & Ayarlar ekranına anlık Dil / Language seçicisi eklendi.\n• R8 kod küçültme ve AES-256-GCM güvenlik kalkanı korundu."
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
    override val navDataSync = "Data"
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
    override val releaseNotesDialogBody = "• Added full English language support (Türkçe / English / System).\n• Localized follower count units (K/B), dates, chart months, CSV exports, and security self-check outputs.\n• Added instant Language / Dil switcher in Data & Settings.\n• Kept R8 minification and AES-256-GCM security hardening."
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

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.TR -> TrStrings
    AppLanguage.EN -> EnStrings
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { TrStrings }
