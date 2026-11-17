package com.installow.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.installow.app.data.DemoDataGenerator
import com.installow.app.data.InstagramExportParser
import com.installow.app.data.LocalRepository
import com.installow.app.data.PersistedAppState
import com.installow.app.model.AccountAnnotation
import com.installow.app.model.AppScreen
import com.installow.app.model.DataSourceType
import com.installow.app.model.FilterTab
import com.installow.app.model.InstaAccount
import com.installow.app.model.MonthlyStat
import com.installow.app.model.ParsedExportData
import com.installow.app.model.RelationCategory
import com.installow.app.model.Snapshot
import com.installow.app.model.SnapshotDiff
import com.installow.app.model.SortOption
import com.installow.app.model.ThemePreference
import com.installow.app.util.SecurityCheckItem
import com.installow.app.util.SecurityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Immutable
data class InstallowUiState(
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val selectedTab: FilterTab = FilterTab.NON_FOLLOWERS,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NEWEST_FIRST,
    val hideWhitelistedInNonFollowers: Boolean = true,
    val themePreference: ThemePreference = ThemePreference.DARK,
    val dataSourceType: DataSourceType = DataSourceType.DEMO,
    val activeOwnerUsername: String = "benim.hesabim",
    val allAccounts: List<InstaAccount> = emptyList(),
    val filteredAccounts: List<InstaAccount> = emptyList(),
    val tabCounts: Map<FilterTab, Int> = emptyMap(),
    val snapshots: List<Snapshot> = emptyList(),
    val latestDiff: SnapshotDiff? = null,
    val lostFollowerAccounts: List<InstaAccount> = emptyList(),
    val newFollowerAccounts: List<InstaAccount> = emptyList(),
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val mutualCount: Int = 0,
    val nonFollowersTotalCount: Int = 0,
    val nonFollowersActionableCount: Int = 0,
    val fansCount: Int = 0,
    val pendingCount: Int = 0,
    val whitelistCount: Int = 0,
    val followBackRatio: Float = 0f,
    val reciprocityPercent: Int = 0,
    val fanReturnPercent: Int = 0,
    val monthlyStats: List<MonthlyStat> = emptyList(),
    val earliestFollower: InstaAccount? = null,
    val earliestNonFollower: InstaAccount? = null,
    val longestPending: InstaAccount? = null,
    val isQueueActive: Boolean = false,
    val queueUsernames: List<String> = emptyList(),
    val queueIndex: Int = 0,
    val currentQueueAccount: InstaAccount? = null,
    val isImporting: Boolean = false,
    val statusMessage: String? = null,
    val selectedAccountForSheet: InstaAccount? = null,
    val showWebSyncDialog: Boolean = false,
    val securityChecks: List<SecurityCheckItem> = emptyList()
)

class InstallowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocalRepository(application.applicationContext)

    private var snapshots: List<Snapshot> = emptyList()
    private var annotations: MutableMap<String, AccountAnnotation> = linkedMapOf()

    private val _uiState = MutableStateFlow(InstallowUiState())
    val uiState: StateFlow<InstallowUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                repository.loadState()
            }
            if (loaded != null) {
                snapshots = loaded.snapshots
                annotations = loaded.annotations.toMutableMap()
                rebuildState(
                    sourceType = loaded.dataSourceType,
                    themePreference = loaded.themePreference,
                    hideWhitelisted = loaded.hideWhitelistedInNonFollowers,
                    ownerUsername = loaded.activeOwnerUsername
                )
            } else {
                resetToDemoData(silent = true)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun openConnectionsWithTab(tab: FilterTab) {
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.CONNECTIONS,
            selectedTab = tab
        )
        recomputeFilteredList()
    }

    fun selectTab(tab: FilterTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        recomputeFilteredList()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        recomputeFilteredList()
    }

    fun updateSortOption(option: SortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
        recomputeFilteredList()
    }

    fun toggleHideWhitelistedInNonFollowers() {
        val next = !_uiState.value.hideWhitelistedInNonFollowers
        _uiState.value = _uiState.value.copy(hideWhitelistedInNonFollowers = next)
        recomputeFilteredList()
        persistCurrentState()
    }

    fun setThemePreference(preference: ThemePreference) {
        _uiState.value = _uiState.value.copy(themePreference = preference)
        persistCurrentState()
    }

    fun setShowWebSyncDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWebSyncDialog = show)
    }

    fun openAccountSheet(account: InstaAccount?) {
        _uiState.value = _uiState.value.copy(selectedAccountForSheet = account)
    }

    fun dismissStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun runSecurityAudit() {
        viewModelScope.launch {
            val results = withContext(Dispatchers.Default) {
                SecurityManager.runSecuritySelfChecks()
            }
            val passedCount = results.count { it.passed }
            _uiState.value = _uiState.value.copy(
                securityChecks = results,
                statusMessage = "Güvenlik denetimi tamamlandı: $passedCount / ${results.size} kalkan doğrulandı."
            )
        }
    }

    fun clearWebSessionCookies(context: Context) {
        SecurityManager.clearWebViewSessionData(context)
        _uiState.value = _uiState.value.copy(
            statusMessage = "Web oturum çerezleri ve tarayıcı önbelleği temizlendi."
        )
    }

    fun toggleWhitelist(username: String) {
        val current = annotations[username] ?: AccountAnnotation()
        val updated = current.copy(isWhitelisted = !current.isWhitelisted)
        annotations[username] = updated
        rebuildState()
        refreshSheetAccountIfOpen(username)
        persistCurrentState()
        val msg = if (updated.isWhitelisted) {
            "@$username beyaz listeye eklendi."
        } else {
            "@$username beyaz listeden çıkarıldı."
        }
        _uiState.value = _uiState.value.copy(statusMessage = msg)
    }

    fun updateTagAndNote(username: String, tag: String, note: String) {
        val current = annotations[username] ?: AccountAnnotation()
        annotations[username] = current.copy(
            customTag = tag.trim().take(40),
            customNote = note.trim().take(300)
        )
        rebuildState()
        refreshSheetAccountIfOpen(username)
        persistCurrentState()
        _uiState.value = _uiState.value.copy(statusMessage = "@$username için etiket ve not kaydedildi.")
    }

    fun markUnfollowedLocally(username: String) {
        val latest = snapshots.lastOrNull() ?: return
        if (username !in latest.following) return

        val updatedFollowing = LinkedHashMap(latest.following).apply { remove(username) }
        val updatedLatest = latest.copy(following = updatedFollowing)
        snapshots = snapshots.dropLast(1) + updatedLatest

        val wasSheetOpen = _uiState.value.selectedAccountForSheet?.username == username
        rebuildState()
        if (wasSheetOpen) {
            _uiState.value = _uiState.value.copy(selectedAccountForSheet = null)
        }
        if (_uiState.value.isQueueActive) {
            removeFromQueueAndAdvance(username)
        }
        persistCurrentState()
        _uiState.value = _uiState.value.copy(
            statusMessage = "@$username takip edilenlerden çıkarıldı olarak işaretlendi."
        )
    }

    fun startReviewQueue() {
        val state = _uiState.value
        val candidates = state.allAccounts
            .filter { it.category == RelationCategory.NON_FOLLOWER && !it.isWhitelisted }
            .sortedBy { it.relevantTimestamp }
            .map { it.username }

        if (candidates.isEmpty()) {
            _uiState.value = state.copy(
                statusMessage = "İncelenecek (beyaz liste dışı) takip etmeyen hesap bulunamadı!"
            )
            return
        }

        _uiState.value = state.copy(
            currentScreen = AppScreen.CONNECTIONS,
            selectedTab = FilterTab.NON_FOLLOWERS,
            isQueueActive = true,
            queueUsernames = candidates,
            queueIndex = 0,
            currentQueueAccount = resolveQueueAccount(state.allAccounts, true, candidates, 0)
        )
        recomputeFilteredList()
    }

    fun nextInQueue() {
        val state = _uiState.value
        if (!state.isQueueActive || state.queueUsernames.isEmpty()) return
        if (state.queueIndex + 1 >= state.queueUsernames.size) {
            _uiState.value = state.copy(
                isQueueActive = false,
                queueUsernames = emptyList(),
                queueIndex = 0,
                currentQueueAccount = null,
                statusMessage = "Sıralı inceleme kuyruğu tamamlandı!"
            )
        } else {
            val nextIdx = state.queueIndex + 1
            _uiState.value = state.copy(
                queueIndex = nextIdx,
                currentQueueAccount = resolveQueueAccount(state.allAccounts, true, state.queueUsernames, nextIdx)
            )
        }
    }

    fun previousInQueue() {
        val state = _uiState.value
        if (!state.isQueueActive || state.queueIndex <= 0) return
        val prevIdx = state.queueIndex - 1
        _uiState.value = state.copy(
            queueIndex = prevIdx,
            currentQueueAccount = resolveQueueAccount(state.allAccounts, true, state.queueUsernames, prevIdx)
        )
    }

    fun stopQueue() {
        _uiState.value = _uiState.value.copy(
            isQueueActive = false,
            queueUsernames = emptyList(),
            queueIndex = 0,
            currentQueueAccount = null
        )
    }

    private fun removeFromQueueAndAdvance(username: String) {
        val state = _uiState.value
        val remaining = state.queueUsernames.filterNot { it == username }
        if (remaining.isEmpty()) {
            _uiState.value = state.copy(
                isQueueActive = false,
                queueUsernames = emptyList(),
                queueIndex = 0,
                currentQueueAccount = null,
                statusMessage = "Kuyruktaki tüm hesaplar incelendi!"
            )
        } else {
            val nextIdx = state.queueIndex.coerceAtMost(remaining.lastIndex)
            _uiState.value = state.copy(
                queueUsernames = remaining,
                queueIndex = nextIdx,
                currentQueueAccount = resolveQueueAccount(state.allAccounts, true, remaining, nextIdx)
            )
        }
    }

    private fun resolveQueueAccount(
        accounts: List<InstaAccount>,
        active: Boolean,
        queueUsernames: List<String>,
        queueIndex: Int
    ): InstaAccount? {
        if (!active || queueUsernames.isEmpty()) return null
        val target = queueUsernames.getOrNull(queueIndex) ?: return null
        return accounts.firstOrNull { it.username == target }
    }

    fun resetToDemoData(silent: Boolean = false) {
        val bundle = DemoDataGenerator.createInitialBundle()
        snapshots = bundle.snapshots
        annotations = bundle.annotations.toMutableMap()
        rebuildState(
            sourceType = DataSourceType.DEMO,
            ownerUsername = "benim.hesabim"
        )
        persistCurrentState()
        if (!silent) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "Demo veri seti ve örnek değişim geçmişi yüklendi."
            )
        }
    }

    fun simulateSnapshotChange() {
        val latest = snapshots.lastOrNull() ?: return
        val simulated = DemoDataGenerator.simulateNextSnapshot(latest)
        snapshots = (snapshots + simulated).takeLast(15)
        rebuildState()
        persistCurrentState()
        val diff = _uiState.value.latestDiff
        val msg = if (diff != null) {
            "Yeni snapshot: ${diff.lostFollowers.size} takipten çıkan, ${diff.newFollowers.size} yeni takipçi."
        } else {
            "Yeni değişim kaydı oluşturuldu."
        }
        _uiState.value = _uiState.value.copy(statusMessage = msg)
    }

    fun importFromUris(context: Context, uris: List<Uri>) {
        if (uris.isEmpty()) return
        _uiState.value = _uiState.value.copy(isImporting = true)

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    var combined = ParsedExportData()
                    for (uri in uris) {
                        val fileName = resolveFileName(context, uri)
                        val parsed = context.contentResolver.openInputStream(uri)?.use { stream ->
                            InstagramExportParser.parseStreamAuto(fileName, stream)
                        } ?: continue
                        combined = InstagramExportParser.merge(combined, parsed)
                    }
                    combined
                }
            }

            result.fold(
                onSuccess = { parsed ->
                    if (parsed.followers.isEmpty() && parsed.following.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isImporting = false,
                            statusMessage = "Seçilen dosyalarda followers_1.json / following.json verisi bulunamadı. Lütfen Instagram ZIP arşivini veya JSON/HTML dosyalarını seçin."
                        )
                        return@fold
                    }

                    val existingLatest = snapshots.lastOrNull()
                    val newFollowersMap = if (parsed.followers.isNotEmpty()) {
                        parsed.followers.mapValues { it.value.timestampSec }
                    } else if (_uiState.value.dataSourceType == DataSourceType.OFFICIAL_EXPORT && existingLatest != null) {
                        existingLatest.followers
                    } else {
                        emptyMap()
                    }

                    val newFollowingMap = if (parsed.following.isNotEmpty()) {
                        parsed.following.mapValues { it.value.timestampSec }
                    } else if (_uiState.value.dataSourceType == DataSourceType.OFFICIAL_EXPORT && existingLatest != null) {
                        existingLatest.following
                    } else {
                        emptyMap()
                    }

                    val namesMap = linkedMapOf<String, String>()
                    parsed.followers.values.forEach { if (it.displayName.isNotBlank()) namesMap[it.username] = it.displayName }
                    parsed.following.values.forEach { if (it.displayName.isNotBlank()) namesMap[it.username] = it.displayName }
                    parsed.pendingRequests.values.forEach { if (it.displayName.isNotBlank()) namesMap[it.username] = it.displayName }

                    val snapshot = Snapshot(
                        id = UUID.randomUUID().toString(),
                        createdAtMillis = System.currentTimeMillis(),
                        sourceLabel = "Instagram Arşivi (${parsed.sourceFilesFound.size} dosya)",
                        followers = newFollowersMap,
                        following = newFollowingMap,
                        pendingRequests = parsed.pendingRequests.mapValues { it.value.timestampSec },
                        closeFriends = parsed.closeFriends,
                        displayNames = namesMap
                    )

                    val previousReal = snapshots.filter { it.id != "demo-prev" && it.id != "demo-current" }
                    snapshots = if (_uiState.value.dataSourceType == DataSourceType.DEMO) {
                        listOf(snapshot)
                    } else {
                        (previousReal + snapshot).takeLast(15)
                    }

                    rebuildState(
                        sourceType = DataSourceType.OFFICIAL_EXPORT,
                        ownerUsername = "Instagram Arşivim"
                    )
                    persistCurrentState()
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        currentScreen = AppScreen.DASHBOARD,
                        statusMessage = "İçe aktarıldı: ${newFollowersMap.size} takipçi, ${newFollowingMap.size} takip edilen."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        statusMessage = "Dosya okunamadı: ${error.localizedMessage ?: "Bilinmeyen hata"}"
                    )
                }
            )
        }
    }

    fun applyWebSyncData(
        ownerUsername: String,
        followers: Map<String, Pair<String, Long>>,
        following: Map<String, Pair<String, Long>>
    ) {
        val now = System.currentTimeMillis()
        val nowSec = now / 1000L
        val displayNames = linkedMapOf<String, String>()
        val followersMap = linkedMapOf<String, Long>()
        val followingMap = linkedMapOf<String, Long>()

        followers.forEach { (rawHandle, pair) ->
            val handle = InstagramExportParser.normalizeUsername(rawHandle) ?: return@forEach
            if (pair.first.isNotBlank()) displayNames[handle] = pair.first
            followersMap[handle] = if (pair.second > 0L) pair.second else nowSec
        }
        following.forEach { (rawHandle, pair) ->
            val handle = InstagramExportParser.normalizeUsername(rawHandle) ?: return@forEach
            if (pair.first.isNotBlank()) displayNames[handle] = pair.first
            followingMap[handle] = if (pair.second > 0L) pair.second else nowSec
        }

        val cleanOwner = ownerUsername.trim().removePrefix("@").ifBlank { "instagram_hesabim" }
        val snapshot = Snapshot(
            id = UUID.randomUUID().toString(),
            createdAtMillis = now,
            sourceLabel = "Canlı Web Senkronizasyonu (@$cleanOwner)",
            followers = followersMap,
            following = followingMap,
            displayNames = displayNames
        )

        val previousReal = snapshots.filter { it.id != "demo-prev" && it.id != "demo-current" }
        snapshots = if (_uiState.value.dataSourceType == DataSourceType.DEMO) {
            listOf(snapshot)
        } else {
            (previousReal + snapshot).takeLast(15)
        }

        rebuildState(
            sourceType = DataSourceType.WEB_SYNC,
            ownerUsername = cleanOwner
        )
        persistCurrentState()
        _uiState.value = _uiState.value.copy(
            showWebSyncDialog = false,
            currentScreen = AppScreen.DASHBOARD,
            statusMessage = "@$cleanOwner senkronize edildi: ${followersMap.size} takipçi, ${followingMap.size} takip edilen."
        )
    }

    fun exportCurrentListText(): String {
        val state = _uiState.value
        val builder = StringBuilder()
        builder.appendLine("Installow Raporu — ${state.selectedTab.labelTr}")
        builder.appendLine("Hesap: @${SecurityManager.sanitizeCsvCell(state.activeOwnerUsername)} | Toplam: ${state.filteredAccounts.size}")
        builder.appendLine("kullanici_adi,gorunen_ad,durum,etiket,beyaz_liste,profil_url")
        state.filteredAccounts.forEach { acc ->
            val safeUser = SecurityManager.sanitizeCsvCell(acc.username)
            val safeName = SecurityManager.sanitizeCsvCell(acc.displayName)
            val safeCategory = SecurityManager.sanitizeCsvCell(acc.category.labelTr)
            val safeTag = SecurityManager.sanitizeCsvCell(acc.customTag)
            val safeUrl = SecurityManager.sanitizeCsvCell(acc.profileUrl)
            builder.appendLine(
                "$safeUser,$safeName,$safeCategory,$safeTag,${acc.isWhitelisted},$safeUrl"
            )
        }
        return builder.toString()
    }

    private fun rebuildState(
        sourceType: DataSourceType = _uiState.value.dataSourceType,
        themePreference: ThemePreference = _uiState.value.themePreference,
        hideWhitelisted: Boolean = _uiState.value.hideWhitelistedInNonFollowers,
        ownerUsername: String = _uiState.value.activeOwnerUsername
    ) {
        val latest = snapshots.lastOrNull() ?: return
        val previous = if (snapshots.size >= 2) snapshots[snapshots.size - 2] else null

        val allKeys = linkedSetOf<String>().apply {
            addAll(latest.followers.keys)
            addAll(latest.following.keys)
            addAll(latest.pendingRequests.keys)
        }

        val accounts = allKeys.map { username ->
            val ann = annotations[username] ?: AccountAnnotation()
            InstaAccount(
                username = username,
                displayName = latest.displayNames[username].orEmpty(),
                profileUrl = "https://www.instagram.com/$username/",
                followedMeAt = latest.followers[username],
                iFollowedAt = latest.following[username],
                pendingRequestAt = latest.pendingRequests[username],
                isCloseFriend = username in latest.closeFriends,
                isWhitelisted = ann.isWhitelisted,
                customTag = ann.customTag,
                customNote = ann.customNote,
                followerCount = latest.followerCounts[username]
            )
        }

        val diff = if (previous != null) {
            val prevFollowers = previous.followers.keys
            val currFollowers = latest.followers.keys
            val prevFollowing = previous.following.keys
            val currFollowing = latest.following.keys
            SnapshotDiff(
                previousSnapshotId = previous.id,
                currentSnapshotId = latest.id,
                previousTimeMillis = previous.createdAtMillis,
                currentTimeMillis = latest.createdAtMillis,
                newFollowers = (currFollowers - prevFollowers).sorted(),
                lostFollowers = (prevFollowers - currFollowers).sorted(),
                newFollowing = (currFollowing - prevFollowing).sorted(),
                unfollowedByMe = (prevFollowing - currFollowing).sorted()
            )
        } else {
            null
        }

        val accountMap = accounts.associateBy { it.username }
        val lostFollowerAccounts = diff?.lostFollowers?.map { username ->
            accountMap[username] ?: run {
                val ann = annotations[username] ?: AccountAnnotation()
                InstaAccount(
                    username = username,
                    displayName = previous?.displayNames?.get(username).orEmpty(),
                    followedMeAt = previous?.followers?.get(username),
                    iFollowedAt = latest.following[username],
                    isWhitelisted = ann.isWhitelisted,
                    customTag = ann.customTag,
                    customNote = ann.customNote,
                    followerCount = previous?.followerCounts?.get(username)
                )
            }
        }.orEmpty()

        val newFollowerAccounts = diff?.newFollowers?.mapNotNull { accountMap[it] }.orEmpty()

        var followersCount = 0
        var followingCount = 0
        var mutualCount = 0
        var nonFollowersTotal = 0
        var nonFollowersActionable = 0
        var fansCount = 0
        var pendingCount = 0
        var whitelistCount = 0
        var earliestFollower: InstaAccount? = null
        var earliestNonFollower: InstaAccount? = null
        var longestPending: InstaAccount? = null

        for (acc in accounts) {
            if (acc.isFollower) {
                followersCount++
                val ts = acc.followedMeAt ?: 0L
                if (ts > 0L && (earliestFollower == null || ts < (earliestFollower.followedMeAt ?: Long.MAX_VALUE))) {
                    earliestFollower = acc
                }
            }
            if (acc.isFollowing) followingCount++
            if (acc.isWhitelisted) whitelistCount++

            when (acc.category) {
                RelationCategory.MUTUAL -> mutualCount++
                RelationCategory.NON_FOLLOWER -> {
                    nonFollowersTotal++
                    if (!acc.isWhitelisted) {
                        nonFollowersActionable++
                        val ts = acc.iFollowedAt ?: 0L
                        if (ts > 0L && (earliestNonFollower == null || ts < (earliestNonFollower.iFollowedAt ?: Long.MAX_VALUE))) {
                            earliestNonFollower = acc
                        }
                    }
                }
                RelationCategory.FAN -> fansCount++
                RelationCategory.PENDING_REQUEST -> {
                    pendingCount++
                    val ts = acc.pendingRequestAt ?: 0L
                    if (ts > 0L && (longestPending == null || ts < (longestPending.pendingRequestAt ?: Long.MAX_VALUE))) {
                        longestPending = acc
                    }
                }
            }
        }

        val followBackRatio = if (followingCount > 0) followersCount.toFloat() / followingCount.toFloat() else 0f
        val reciprocityPercent = if (followingCount > 0) (mutualCount * 100) / followingCount else 0
        val fanReturnPercent = if (followersCount > 0) (mutualCount * 100) / followersCount else 0

        val tabCounts = mapOf(
            FilterTab.NON_FOLLOWERS to if (hideWhitelisted) nonFollowersActionable else nonFollowersTotal,
            FilterTab.FANS to fansCount,
            FilterTab.MUTUALS to mutualCount,
            FilterTab.ALL_FOLLOWERS to followersCount,
            FilterTab.ALL_FOLLOWING to followingCount,
            FilterTab.UNFOLLOWERS_DIFF to lostFollowerAccounts.size,
            FilterTab.PENDING to pendingCount,
            FilterTab.WHITELIST to whitelistCount
        )

        val monthlyStats = computeMonthlyStats(latest)
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            dataSourceType = sourceType,
            themePreference = themePreference,
            hideWhitelistedInNonFollowers = hideWhitelisted,
            activeOwnerUsername = ownerUsername,
            allAccounts = accounts,
            tabCounts = tabCounts,
            snapshots = snapshots,
            latestDiff = diff,
            lostFollowerAccounts = lostFollowerAccounts,
            newFollowerAccounts = newFollowerAccounts,
            followersCount = followersCount,
            followingCount = followingCount,
            mutualCount = mutualCount,
            nonFollowersTotalCount = nonFollowersTotal,
            nonFollowersActionableCount = nonFollowersActionable,
            fansCount = fansCount,
            pendingCount = pendingCount,
            whitelistCount = whitelistCount,
            followBackRatio = followBackRatio,
            reciprocityPercent = reciprocityPercent,
            fanReturnPercent = fanReturnPercent,
            monthlyStats = monthlyStats,
            earliestFollower = earliestFollower,
            earliestNonFollower = earliestNonFollower,
            longestPending = longestPending,
            currentQueueAccount = resolveQueueAccount(
                accounts = accounts,
                active = currentState.isQueueActive,
                queueUsernames = currentState.queueUsernames,
                queueIndex = currentState.queueIndex
            )
        )
        recomputeFilteredList()
    }

    private fun recomputeFilteredList() {
        val state = _uiState.value
        val baseList = when (state.selectedTab) {
            FilterTab.NON_FOLLOWERS -> state.allAccounts.filter {
                it.category == RelationCategory.NON_FOLLOWER &&
                    (!state.hideWhitelistedInNonFollowers || !it.isWhitelisted)
            }
            FilterTab.FANS -> state.allAccounts.filter { it.category == RelationCategory.FAN }
            FilterTab.MUTUALS -> state.allAccounts.filter { it.category == RelationCategory.MUTUAL }
            FilterTab.ALL_FOLLOWERS -> state.allAccounts.filter { it.isFollower }
            FilterTab.ALL_FOLLOWING -> state.allAccounts.filter { it.isFollowing }
            FilterTab.UNFOLLOWERS_DIFF -> state.lostFollowerAccounts
            FilterTab.PENDING -> state.allAccounts.filter { it.category == RelationCategory.PENDING_REQUEST }
            FilterTab.WHITELIST -> state.allAccounts.filter { it.isWhitelisted }
        }

        val q = state.searchQuery.trim().lowercase(Locale.ROOT)
        val searched = if (q.isEmpty()) {
            baseList
        } else {
            baseList.filter { acc -> acc.searchBlob.contains(q) }
        }

        val sorted = when (state.sortOption) {
            SortOption.NEWEST_FIRST -> searched.sortedByDescending { it.relevantTimestamp }
            SortOption.OLDEST_FIRST -> searched.sortedBy {
                if (it.relevantTimestamp == 0L) Long.MAX_VALUE else it.relevantTimestamp
            }
            SortOption.FOLLOWERS_DESC -> searched.sortedWith(
                compareByDescending<InstaAccount> { it.followerCount ?: -1L }.thenBy { it.username }
            )
            SortOption.FOLLOWERS_ASC -> searched.sortedWith(
                compareBy<InstaAccount> { it.followerCount ?: Long.MAX_VALUE }.thenBy { it.username }
            )
            SortOption.USERNAME_ASC -> searched.sortedBy { it.username }
            SortOption.USERNAME_DESC -> searched.sortedByDescending { it.username }
        }

        val updatedCounts = state.tabCounts.toMutableMap().apply {
            this[FilterTab.NON_FOLLOWERS] = if (state.hideWhitelistedInNonFollowers) {
                state.nonFollowersActionableCount
            } else {
                state.nonFollowersTotalCount
            }
        }

        _uiState.value = state.copy(
            filteredAccounts = sorted,
            tabCounts = updatedCounts
        )
    }

    private fun refreshSheetAccountIfOpen(username: String) {
        val currentOpen = _uiState.value.selectedAccountForSheet ?: return
        if (currentOpen.username == username) {
            val updated = _uiState.value.allAccounts.firstOrNull { it.username == username }
            _uiState.value = _uiState.value.copy(selectedAccountForSheet = updated)
        }
    }

    private fun computeMonthlyStats(snapshot: Snapshot): List<MonthlyStat> {
        val trLocale = Locale("tr", "TR")
        val keyFmt = SimpleDateFormat("yyyy-MM", Locale.US)
        val labelFmt = SimpleDateFormat("MMM", trLocale)

        val latestSec = (snapshot.followers.values + snapshot.following.values)
            .filter { it > 0L }
            .maxOrNull() ?: (snapshot.createdAtMillis / 1000L)

        val cal = Calendar.getInstance().apply {
            timeInMillis = latestSec * 1000L
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val months = mutableListOf<Pair<String, String>>()
        for (i in 5 downTo 0) {
            val c = (cal.clone() as Calendar).apply { add(Calendar.MONTH, -i) }
            val d = Date(c.timeInMillis)
            months.add(keyFmt.format(d) to labelFmt.format(d).replaceFirstChar { it.uppercase(trLocale) })
        }

        val followerBuckets = mutableMapOf<String, Int>()
        snapshot.followers.values.forEach { ts ->
            if (ts > 0L) {
                val key = keyFmt.format(Date(ts * 1000L))
                followerBuckets[key] = (followerBuckets[key] ?: 0) + 1
            }
        }

        val followingBuckets = mutableMapOf<String, Int>()
        snapshot.following.values.forEach { ts ->
            if (ts > 0L) {
                val key = keyFmt.format(Date(ts * 1000L))
                followingBuckets[key] = (followingBuckets[key] ?: 0) + 1
            }
        }

        return months.map { (key, label) ->
            MonthlyStat(
                monthKey = key,
                label = label,
                gainedFollowers = followerBuckets[key] ?: 0,
                newFollowing = followingBuckets[key] ?: 0
            )
        }
    }

    private fun resolveFileName(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) {
                        val name = cursor.getString(idx)
                        if (!name.isNullOrBlank()) return name
                    }
                }
            }
        return uri.lastPathSegment ?: "export.json"
    }

    private fun persistCurrentState() {
        val state = _uiState.value
        val persisted = PersistedAppState(
            snapshots = snapshots.toList(),
            annotations = LinkedHashMap(annotations),
            dataSourceType = state.dataSourceType,
            themePreference = state.themePreference,
            hideWhitelistedInNonFollowers = state.hideWhitelistedInNonFollowers,
            activeOwnerUsername = state.activeOwnerUsername
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveState(persisted)
        }
    }
}
