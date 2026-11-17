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
import com.installow.app.data.InstagramPublicLookup
import com.installow.app.data.LocalRepository
import com.installow.app.data.PersistedAppState
import com.installow.app.model.AccountAnnotation
import com.installow.app.model.AccountWorkspace
import com.installow.app.model.AppLanguage
import com.installow.app.model.AppScreen
import com.installow.app.model.ConnectionQuickFilter
import com.installow.app.model.DataSourceType
import com.installow.app.model.FilterTab
import com.installow.app.model.InstaAccount
import com.installow.app.model.LanguagePreference
import com.installow.app.model.ListAccessStatus
import com.installow.app.model.MonthlyStat
import com.installow.app.model.ParsedExportData
import com.installow.app.model.RelationCategory
import com.installow.app.model.Snapshot
import com.installow.app.model.SnapshotDiff
import com.installow.app.model.SortOption
import com.installow.app.model.ThemePreference
import com.installow.app.ui.i18n.AppStrings
import com.installow.app.ui.i18n.resolveAppLanguage
import com.installow.app.ui.i18n.stringsFor
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
    val selectedQuickFilter: ConnectionQuickFilter = ConnectionQuickFilter.ALL,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NEWEST_FIRST,
    val hideWhitelistedInNonFollowers: Boolean = true,
    val themePreference: ThemePreference = ThemePreference.DARK,
    val languagePreference: LanguagePreference = LanguagePreference.SYSTEM,
    val dataSourceType: DataSourceType = DataSourceType.DEMO,
    val activeOwnerUsername: String = "benim.hesabim",
    val activeOwnerDisplayName: String = "",
    val workspaces: List<AccountWorkspace> = emptyList(),
    val activeWorkspaceId: String = "",
    val isPrivateAccount: Boolean? = null,
    val listAccessStatus: ListAccessStatus = ListAccessStatus.FULL_DATA,
    val isLookingUpAccount: Boolean = false,
    val showAccountSwitcherSheet: Boolean = false,
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

    private var workspaces: MutableList<AccountWorkspace> = mutableListOf()
    private var activeWorkspaceId: String = ""
    private var snapshots: List<Snapshot> = emptyList()
    private var annotations: MutableMap<String, AccountAnnotation> = linkedMapOf()

    private val _uiState = MutableStateFlow(InstallowUiState())
    val uiState: StateFlow<InstallowUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun currentStrings(pref: LanguagePreference = _uiState.value.languagePreference): AppStrings =
        stringsFor(resolveAppLanguage(pref))

    private fun activeWorkspace(): AccountWorkspace? =
        workspaces.firstOrNull { it.id == activeWorkspaceId } ?: workspaces.firstOrNull()

    private fun syncActiveWorkspaceInList(
        ownerUsername: String = activeWorkspace()?.ownerUsername ?: _uiState.value.activeOwnerUsername,
        displayName: String = activeWorkspace()?.displayName ?: _uiState.value.activeOwnerDisplayName,
        sourceType: DataSourceType = activeWorkspace()?.dataSourceType ?: _uiState.value.dataSourceType,
        reportedFollowers: Int? = activeWorkspace()?.reportedFollowersCount,
        reportedFollowing: Int? = activeWorkspace()?.reportedFollowingCount,
        isPrivate: Boolean? = activeWorkspace()?.isPrivateAccount,
        listStatus: ListAccessStatus = activeWorkspace()?.listAccessStatus ?: _uiState.value.listAccessStatus
    ) {
        val updated = AccountWorkspace(
            id = activeWorkspaceId,
            ownerUsername = ownerUsername,
            displayName = displayName,
            dataSourceType = sourceType,
            snapshots = snapshots.toList(),
            annotations = LinkedHashMap(annotations),
            reportedFollowersCount = reportedFollowers,
            reportedFollowingCount = reportedFollowing,
            isPrivateAccount = isPrivate,
            listAccessStatus = listStatus
        )
        val idx = workspaces.indexOfFirst { it.id == activeWorkspaceId }
        if (idx >= 0) {
            workspaces[idx] = updated
        } else {
            workspaces.add(0, updated)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                repository.loadState()
            }
            if (loaded != null && loaded.workspaces.isNotEmpty()) {
                workspaces = loaded.workspaces.toMutableList()
                activeWorkspaceId = loaded.activeWorkspaceId
                val active = activeWorkspace()!!
                snapshots = active.snapshots
                annotations = active.annotations.toMutableMap()
                rebuildState(
                    sourceType = active.dataSourceType,
                    themePreference = loaded.themePreference,
                    languagePreference = loaded.languagePreference,
                    hideWhitelisted = loaded.hideWhitelistedInNonFollowers,
                    ownerUsername = active.ownerUsername,
                    ownerDisplayName = active.displayName,
                    isPrivateAccount = active.isPrivateAccount,
                    listAccessStatus = active.listAccessStatus,
                    reportedFollowers = active.reportedFollowersCount,
                    reportedFollowing = active.reportedFollowingCount
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

    fun selectQuickFilter(filter: ConnectionQuickFilter) {
        _uiState.value = _uiState.value.copy(selectedQuickFilter = filter)
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

    fun setLanguagePreference(preference: LanguagePreference) {
        val hadChecks = _uiState.value.securityChecks.isNotEmpty()
        rebuildState(languagePreference = preference)
        if (hadChecks) {
            val lang = resolveAppLanguage(preference)
            _uiState.value = _uiState.value.copy(
                securityChecks = SecurityManager.runSecuritySelfChecks(lang)
            )
        }
        persistCurrentState()
    }

    fun setShowWebSyncDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWebSyncDialog = show)
    }

    fun setShowAccountSwitcherSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAccountSwitcherSheet = show)
    }

    fun openAccountSheet(account: InstaAccount?) {
        _uiState.value = _uiState.value.copy(selectedAccountForSheet = account)
    }

    fun dismissStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun showTransientMessage(message: String) {
        _uiState.value = _uiState.value.copy(statusMessage = message)
    }

    fun switchWorkspace(workspaceId: String) {
        if (workspaceId == activeWorkspaceId) {
            _uiState.value = _uiState.value.copy(showAccountSwitcherSheet = false)
            return
        }
        syncActiveWorkspaceInList()
        val target = workspaces.firstOrNull { it.id == workspaceId } ?: return
        activeWorkspaceId = target.id
        snapshots = target.snapshots
        annotations = target.annotations.toMutableMap()
        val strings = currentStrings()
        _uiState.value = _uiState.value.copy(
            showAccountSwitcherSheet = false,
            isQueueActive = false,
            queueUsernames = emptyList(),
            queueIndex = 0,
            currentQueueAccount = null,
            selectedAccountForSheet = null,
            statusMessage = String.format(Locale.ROOT, strings.accountSwitchedMsgFormat, target.ownerUsername)
        )
        rebuildState(
            sourceType = target.dataSourceType,
            ownerUsername = target.ownerUsername,
            ownerDisplayName = target.displayName,
            isPrivateAccount = target.isPrivateAccount,
            listAccessStatus = target.listAccessStatus,
            reportedFollowers = target.reportedFollowersCount,
            reportedFollowing = target.reportedFollowingCount
        )
        persistCurrentState()
    }

    fun removeWorkspace(workspaceId: String) {
        val strings = currentStrings()
        if (workspaces.size <= 1) {
            _uiState.value = _uiState.value.copy(statusMessage = strings.cannotRemoveLastAccountMsg)
            return
        }
        val target = workspaces.firstOrNull { it.id == workspaceId } ?: return
        workspaces.removeAll { it.id == workspaceId }
        if (activeWorkspaceId == workspaceId) {
            val fallback = workspaces.first()
            activeWorkspaceId = fallback.id
            snapshots = fallback.snapshots
            annotations = fallback.annotations.toMutableMap()
            rebuildState(
                sourceType = fallback.dataSourceType,
                ownerUsername = fallback.ownerUsername,
                ownerDisplayName = fallback.displayName,
                isPrivateAccount = fallback.isPrivateAccount,
                listAccessStatus = fallback.listAccessStatus,
                reportedFollowers = fallback.reportedFollowersCount,
                reportedFollowing = fallback.reportedFollowingCount
            )
        } else {
            _uiState.value = _uiState.value.copy(workspaces = workspaces.toList())
        }
        _uiState.value = _uiState.value.copy(
            statusMessage = String.format(Locale.ROOT, strings.accountRemovedMsgFormat, target.ownerUsername)
        )
        persistCurrentState()
    }

    fun lookupAndAddAccount(rawUsername: String) {
        val strings = currentStrings()
        val clean = InstagramExportParser.normalizeUsername(rawUsername)
        if (clean == null) {
            _uiState.value = _uiState.value.copy(statusMessage = strings.invalidUsernameMsg)
            return
        }

        _uiState.value = _uiState.value.copy(isLookingUpAccount = true)
        viewModelScope.launch {
            val lookup = withContext(Dispatchers.IO) {
                InstagramPublicLookup.lookupProfile(clean)
            }

            val latestStrings = currentStrings()
            when (lookup.listAccessStatus) {
                ListAccessStatus.PROFILE_NOT_FOUND -> {
                    _uiState.value = _uiState.value.copy(
                        isLookingUpAccount = false,
                        statusMessage = String.format(Locale.ROOT, latestStrings.publicLookupNotFoundMsgFormat, clean)
                    )
                    return@launch
                }

                ListAccessStatus.NETWORK_ERROR -> {
                    val existing = workspaces.firstOrNull { it.id == clean }
                    if (existing != null) {
                        _uiState.value = _uiState.value.copy(isLookingUpAccount = false)
                        switchWorkspace(existing.id)
                        _uiState.value = _uiState.value.copy(statusMessage = latestStrings.publicLookupErrorMsg)
                    } else {
                        val fallbackSnapshot = Snapshot(
                            id = UUID.randomUUID().toString(),
                            createdAtMillis = System.currentTimeMillis(),
                            sourceLabel = "${latestStrings.sourcePublicLookup} (@$clean)",
                            followers = emptyMap(),
                            following = emptyMap()
                        )
                        activateOrCreateWorkspaceFromLookup(
                            username = clean,
                            displayName = "",
                            sourceType = DataSourceType.PUBLIC_LOOKUP,
                            snapshot = fallbackSnapshot,
                            reportedFollowers = null,
                            reportedFollowing = null,
                            isPrivate = null,
                            listStatus = ListAccessStatus.NETWORK_ERROR
                        )
                        _uiState.value = _uiState.value.copy(
                            isLookingUpAccount = false,
                            showAccountSwitcherSheet = false,
                            statusMessage = latestStrings.publicLookupErrorMsg
                        )
                    }
                    return@launch
                }

                else -> {
                    val now = System.currentTimeMillis()
                    val nowSec = now / 1000L
                    val followersMap = linkedMapOf<String, Long>()
                    val followingMap = linkedMapOf<String, Long>()
                    val displayNames = linkedMapOf<String, String>()

                    lookup.followers.forEach { (handle, pair) ->
                        if (pair.first.isNotBlank()) displayNames[handle] = pair.first
                        followersMap[handle] = if (pair.second > 0L) pair.second else nowSec
                    }
                    lookup.following.forEach { (handle, pair) ->
                        if (pair.first.isNotBlank()) displayNames[handle] = pair.first
                        followingMap[handle] = if (pair.second > 0L) pair.second else nowSec
                    }

                    val existingWs = workspaces.firstOrNull { it.id == clean }
                    val existingLatest = existingWs?.snapshots?.lastOrNull()
                    val effectiveFollowers = if (followersMap.isNotEmpty()) followersMap else existingLatest?.followers.orEmpty()
                    val effectiveFollowing = if (followingMap.isNotEmpty()) followingMap else existingLatest?.following.orEmpty()
                    val effectiveNames = if (displayNames.isNotEmpty()) displayNames else existingLatest?.displayNames.orEmpty()

                    val sourceType = when {
                        followersMap.isNotEmpty() || followingMap.isNotEmpty() -> DataSourceType.PUBLIC_LOOKUP
                        existingWs != null && existingWs.dataSourceType != DataSourceType.DEMO -> existingWs.dataSourceType
                        else -> DataSourceType.PUBLIC_LOOKUP
                    }

                    val newSnapshot = Snapshot(
                        id = UUID.randomUUID().toString(),
                        createdAtMillis = now,
                        sourceLabel = "${latestStrings.sourcePublicLookup} (@$clean)",
                        followers = effectiveFollowers,
                        following = effectiveFollowing,
                        pendingRequests = existingLatest?.pendingRequests.orEmpty(),
                        closeFriends = existingLatest?.closeFriends.orEmpty(),
                        displayNames = effectiveNames,
                        reportedFollowersCount = lookup.followersCount,
                        reportedFollowingCount = lookup.followingCount,
                        isPrivateAccount = lookup.isPrivate
                    )

                    val resolvedListStatus = when {
                        effectiveFollowers.isNotEmpty() || effectiveFollowing.isNotEmpty() -> ListAccessStatus.FULL_DATA
                        else -> lookup.listAccessStatus
                    }

                    activateOrCreateWorkspaceFromLookup(
                        username = clean,
                        displayName = lookup.displayName,
                        sourceType = sourceType,
                        snapshot = newSnapshot,
                        reportedFollowers = lookup.followersCount,
                        reportedFollowing = lookup.followingCount,
                        isPrivate = lookup.isPrivate,
                        listStatus = resolvedListStatus
                    )

                    val fCount = lookup.followersCount ?: effectiveFollowers.size
                    val gCount = lookup.followingCount ?: effectiveFollowing.size
                    val msg = when (resolvedListStatus) {
                        ListAccessStatus.FULL_DATA ->
                            String.format(Locale.ROOT, latestStrings.publicLookupFullSuccessFormat, clean, fCount, gCount)
                        ListAccessStatus.PRIVATE_ACCOUNT ->
                            String.format(Locale.ROOT, latestStrings.publicLookupPrivateMsgFormat, clean, fCount, gCount)
                        else ->
                            String.format(Locale.ROOT, latestStrings.publicLookupCountsOnlyMsgFormat, clean, fCount, gCount)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLookingUpAccount = false,
                        showAccountSwitcherSheet = false,
                        statusMessage = msg
                    )
                }
            }
        }
    }

    private fun activateOrCreateWorkspaceFromLookup(
        username: String,
        displayName: String,
        sourceType: DataSourceType,
        snapshot: Snapshot,
        reportedFollowers: Int?,
        reportedFollowing: Int?,
        isPrivate: Boolean?,
        listStatus: ListAccessStatus
    ) {
        syncActiveWorkspaceInList()
        val existingIdx = workspaces.indexOfFirst { it.id == username }
        val prevSnapshots = if (existingIdx >= 0) {
            workspaces[existingIdx].snapshots.filter { it.id != "demo-prev" && it.id != "demo-current" }
        } else {
            emptyList()
        }
        val prevAnnotations = if (existingIdx >= 0) workspaces[existingIdx].annotations else emptyMap()
        val updatedSnapshots = (prevSnapshots + snapshot).takeLast(15)

        val ws = AccountWorkspace(
            id = username,
            ownerUsername = username,
            displayName = displayName,
            dataSourceType = sourceType,
            snapshots = updatedSnapshots,
            annotations = prevAnnotations,
            reportedFollowersCount = reportedFollowers,
            reportedFollowingCount = reportedFollowing,
            isPrivateAccount = isPrivate,
            listAccessStatus = listStatus
        )
        if (existingIdx >= 0) {
            workspaces[existingIdx] = ws
        } else {
            workspaces.add(0, ws)
        }

        activeWorkspaceId = ws.id
        snapshots = ws.snapshots
        annotations = ws.annotations.toMutableMap()

        rebuildState(
            sourceType = ws.dataSourceType,
            ownerUsername = ws.ownerUsername,
            ownerDisplayName = ws.displayName,
            isPrivateAccount = ws.isPrivateAccount,
            listAccessStatus = ws.listAccessStatus,
            reportedFollowers = ws.reportedFollowersCount,
            reportedFollowing = ws.reportedFollowingCount
        )
        persistCurrentState()
    }

    fun runSecurityAudit() {
        viewModelScope.launch {
            val pref = _uiState.value.languagePreference
            val lang = resolveAppLanguage(pref)
            val strings = stringsFor(lang)
            val results = withContext(Dispatchers.Default) {
                SecurityManager.runSecuritySelfChecks(lang)
            }
            val passedCount = results.count { it.passed }
            _uiState.value = _uiState.value.copy(
                securityChecks = results,
                statusMessage = String.format(Locale.ROOT, strings.securityAuditMsgFormat, passedCount, results.size)
            )
        }
    }

    fun clearWebSessionCookies(context: Context) {
        SecurityManager.clearWebViewSessionData(context)
        val strings = currentStrings()
        _uiState.value = _uiState.value.copy(
            statusMessage = strings.cookiesClearedMsg
        )
    }

    fun toggleWhitelist(username: String) {
        val current = annotations[username] ?: AccountAnnotation()
        val updated = current.copy(isWhitelisted = !current.isWhitelisted)
        annotations[username] = updated
        syncActiveWorkspaceInList()
        rebuildState()
        refreshSheetAccountIfOpen(username)
        persistCurrentState()
        val strings = currentStrings()
        val msg = String.format(
            Locale.ROOT,
            if (updated.isWhitelisted) strings.whitelistAddedFormat else strings.whitelistRemovedFormat,
            username
        )
        _uiState.value = _uiState.value.copy(statusMessage = msg)
    }

    fun updateTagAndNote(username: String, tag: String, note: String) {
        val current = annotations[username] ?: AccountAnnotation()
        annotations[username] = current.copy(
            customTag = tag.trim().take(40),
            customNote = note.trim().take(300)
        )
        syncActiveWorkspaceInList()
        rebuildState()
        refreshSheetAccountIfOpen(username)
        persistCurrentState()
        val strings = currentStrings()
        _uiState.value = _uiState.value.copy(
            statusMessage = String.format(Locale.ROOT, strings.noteSavedFormat, username)
        )
    }

    fun markUnfollowedLocally(username: String) {
        val latest = snapshots.lastOrNull() ?: return
        if (username !in latest.following) return

        val updatedFollowing = LinkedHashMap(latest.following).apply { remove(username) }
        val updatedLatest = latest.copy(following = updatedFollowing)
        snapshots = snapshots.dropLast(1) + updatedLatest
        syncActiveWorkspaceInList()

        val wasSheetOpen = _uiState.value.selectedAccountForSheet?.username == username
        rebuildState()
        if (wasSheetOpen) {
            _uiState.value = _uiState.value.copy(selectedAccountForSheet = null)
        }
        if (_uiState.value.isQueueActive) {
            removeFromQueueAndAdvance(username)
        }
        persistCurrentState()
        val strings = currentStrings()
        _uiState.value = _uiState.value.copy(
            statusMessage = String.format(Locale.ROOT, strings.markedUnfollowedMsgFormat, username)
        )
    }

    fun startReviewQueue() {
        val state = _uiState.value
        val strings = currentStrings(state.languagePreference)
        val candidates = state.allAccounts
            .filter { it.category == RelationCategory.NON_FOLLOWER && !it.isWhitelisted }
            .sortedBy { it.relevantTimestamp }
            .map { it.username }

        if (candidates.isEmpty()) {
            _uiState.value = state.copy(
                statusMessage = strings.queueNoCandidatesMsg
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
        val strings = currentStrings(state.languagePreference)
        if (!state.isQueueActive || state.queueUsernames.isEmpty()) return
        if (state.queueIndex + 1 >= state.queueUsernames.size) {
            _uiState.value = state.copy(
                isQueueActive = false,
                queueUsernames = emptyList(),
                queueIndex = 0,
                currentQueueAccount = null,
                statusMessage = strings.queueCompletedMsg
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
        val strings = currentStrings(state.languagePreference)
        val remaining = state.queueUsernames.filterNot { it == username }
        if (remaining.isEmpty()) {
            _uiState.value = state.copy(
                isQueueActive = false,
                queueUsernames = emptyList(),
                queueIndex = 0,
                currentQueueAccount = null,
                statusMessage = strings.queueAllReviewedMsg
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
        val demoWs = AccountWorkspace(
            id = "benim.hesabim",
            ownerUsername = "benim.hesabim",
            displayName = "Demo Hesap",
            dataSourceType = DataSourceType.DEMO,
            snapshots = snapshots,
            annotations = annotations,
            listAccessStatus = ListAccessStatus.FULL_DATA
        )
        activeWorkspaceId = demoWs.id
        val idx = workspaces.indexOfFirst { it.id == demoWs.id }
        if (idx >= 0) workspaces[idx] = demoWs else workspaces.add(demoWs)

        rebuildState(
            sourceType = DataSourceType.DEMO,
            ownerUsername = "benim.hesabim",
            ownerDisplayName = demoWs.displayName,
            isPrivateAccount = null,
            listAccessStatus = ListAccessStatus.FULL_DATA,
            reportedFollowers = null,
            reportedFollowing = null
        )
        persistCurrentState()
        if (!silent) {
            val strings = currentStrings()
            _uiState.value = _uiState.value.copy(
                statusMessage = strings.demoResetMsg
            )
        }
    }

    fun simulateSnapshotChange() {
        val latest = snapshots.lastOrNull() ?: return
        val simulated = DemoDataGenerator.simulateNextSnapshot(latest)
        snapshots = (snapshots + simulated).takeLast(15)
        syncActiveWorkspaceInList()
        rebuildState()
        persistCurrentState()
        val strings = currentStrings()
        val diff = _uiState.value.latestDiff
        val msg = if (diff != null) {
            String.format(Locale.ROOT, strings.simDiffMsgFormat, diff.lostFollowers.size, diff.newFollowers.size)
        } else {
            strings.simCreatedMsg
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

            val strings = currentStrings()
            result.fold(
                onSuccess = { parsed ->
                    if (parsed.followers.isEmpty() && parsed.following.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isImporting = false,
                            statusMessage = strings.importNotFoundMsg
                        )
                        return@fold
                    }

                    val existingLatest = snapshots.lastOrNull()
                    val activeWs = activeWorkspace()
                    val isCurrentlyDemo = activeWs?.dataSourceType == DataSourceType.DEMO

                    val newFollowersMap = if (parsed.followers.isNotEmpty()) {
                        parsed.followers.mapValues { it.value.timestampSec }
                    } else if (!isCurrentlyDemo && existingLatest != null) {
                        existingLatest.followers
                    } else {
                        emptyMap()
                    }

                    val newFollowingMap = if (parsed.following.isNotEmpty()) {
                        parsed.following.mapValues { it.value.timestampSec }
                    } else if (!isCurrentlyDemo && existingLatest != null) {
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
                        sourceLabel = String.format(Locale.ROOT, strings.archiveSourceLabelFormat, parsed.sourceFilesFound.size),
                        followers = newFollowersMap,
                        following = newFollowingMap,
                        pendingRequests = parsed.pendingRequests.mapValues { it.value.timestampSec },
                        closeFriends = parsed.closeFriends,
                        displayNames = namesMap
                    )

                    val previousReal = snapshots.filter { it.id != "demo-prev" && it.id != "demo-current" }
                    snapshots = if (isCurrentlyDemo) {
                        listOf(snapshot)
                    } else {
                        (previousReal + snapshot).takeLast(15)
                    }

                    val targetOwner = if (activeWs != null && activeWs.ownerUsername != "benim.hesabim") {
                        activeWs.ownerUsername
                    } else {
                        strings.archiveOwnerFallback
                    }
                    syncActiveWorkspaceInList(
                        ownerUsername = targetOwner,
                        sourceType = DataSourceType.OFFICIAL_EXPORT,
                        reportedFollowers = null,
                        reportedFollowing = null,
                        listStatus = ListAccessStatus.FULL_DATA
                    )

                    rebuildState(
                        sourceType = DataSourceType.OFFICIAL_EXPORT,
                        ownerUsername = targetOwner,
                        listAccessStatus = ListAccessStatus.FULL_DATA,
                        reportedFollowers = null,
                        reportedFollowing = null
                    )
                    persistCurrentState()
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        currentScreen = AppScreen.DASHBOARD,
                        statusMessage = String.format(Locale.ROOT, strings.importSuccessFormat, newFollowersMap.size, newFollowingMap.size)
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        statusMessage = "${strings.importFailedPrefix}${error.localizedMessage ?: strings.unknownErrorText}"
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

        val strings = currentStrings()
        val cleanOwner = InstagramExportParser.normalizeUsername(ownerUsername)
            ?: ownerUsername.trim().removePrefix("@").ifBlank { "instagram_account" }

        val snapshot = Snapshot(
            id = UUID.randomUUID().toString(),
            createdAtMillis = now,
            sourceLabel = String.format(Locale.ROOT, strings.webSyncSourceLabelFormat, cleanOwner),
            followers = followersMap,
            following = followingMap,
            displayNames = displayNames
        )

        activateOrCreateWorkspaceFromLookup(
            username = cleanOwner,
            displayName = activeWorkspace()?.takeIf { it.id == cleanOwner }?.displayName.orEmpty(),
            sourceType = DataSourceType.WEB_SYNC,
            snapshot = snapshot,
            reportedFollowers = null,
            reportedFollowing = null,
            isPrivate = null,
            listStatus = ListAccessStatus.FULL_DATA
        )
        _uiState.value = _uiState.value.copy(
            showWebSyncDialog = false,
            currentScreen = AppScreen.DASHBOARD,
            statusMessage = String.format(Locale.ROOT, strings.webSyncSuccessFormat, cleanOwner, followersMap.size, followingMap.size)
        )
    }

    fun exportCurrentListText(): String {
        val state = _uiState.value
        val strings = currentStrings(state.languagePreference)
        val builder = StringBuilder()
        builder.appendLine(String.format(Locale.ROOT, strings.csvReportHeaderFormat, strings.tabLabel(state.selectedTab)))
        builder.appendLine(
            String.format(
                Locale.ROOT,
                strings.csvAccountSummaryFormat,
                SecurityManager.sanitizeCsvCell(state.activeOwnerUsername),
                state.filteredAccounts.size
            )
        )
        builder.appendLine(strings.csvColumnsHeader)
        state.filteredAccounts.forEach { acc ->
            val safeUser = SecurityManager.sanitizeCsvCell(acc.username)
            val safeName = SecurityManager.sanitizeCsvCell(acc.displayName)
            val safeCategory = SecurityManager.sanitizeCsvCell(strings.categoryLabel(acc.category))
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
        languagePreference: LanguagePreference = _uiState.value.languagePreference,
        hideWhitelisted: Boolean = _uiState.value.hideWhitelistedInNonFollowers,
        ownerUsername: String = _uiState.value.activeOwnerUsername,
        ownerDisplayName: String = _uiState.value.activeOwnerDisplayName,
        isPrivateAccount: Boolean? = _uiState.value.isPrivateAccount,
        listAccessStatus: ListAccessStatus = _uiState.value.listAccessStatus,
        reportedFollowers: Int? = activeWorkspace()?.reportedFollowersCount,
        reportedFollowing: Int? = activeWorkspace()?.reportedFollowingCount
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

        var listFollowersCount = 0
        var listFollowingCount = 0
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
                listFollowersCount++
                val ts = acc.followedMeAt ?: 0L
                if (ts > 0L && (earliestFollower == null || ts < (earliestFollower.followedMeAt ?: Long.MAX_VALUE))) {
                    earliestFollower = acc
                }
            }
            if (acc.isFollowing) listFollowingCount++
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

        val effectiveFollowersCount = if (listFollowersCount == 0 && (reportedFollowers ?: latest.reportedFollowersCount) != null) {
            reportedFollowers ?: latest.reportedFollowersCount ?: 0
        } else {
            listFollowersCount
        }
        val effectiveFollowingCount = if (listFollowingCount == 0 && (reportedFollowing ?: latest.reportedFollowingCount) != null) {
            reportedFollowing ?: latest.reportedFollowingCount ?: 0
        } else {
            listFollowingCount
        }

        val followBackRatio = if (effectiveFollowingCount > 0) {
            effectiveFollowersCount.toFloat() / effectiveFollowingCount.toFloat()
        } else {
            0f
        }
        val reciprocityPercent = if (listFollowingCount > 0) (mutualCount * 100) / listFollowingCount else 0
        val fanReturnPercent = if (listFollowersCount > 0) (mutualCount * 100) / listFollowersCount else 0

        val tabCounts = mapOf(
            FilterTab.NON_FOLLOWERS to if (hideWhitelisted) nonFollowersActionable else nonFollowersTotal,
            FilterTab.FANS to fansCount,
            FilterTab.MUTUALS to mutualCount,
            FilterTab.ALL_FOLLOWERS to listFollowersCount,
            FilterTab.ALL_FOLLOWING to listFollowingCount,
            FilterTab.UNFOLLOWERS_DIFF to lostFollowerAccounts.size,
            FilterTab.PENDING to pendingCount,
            FilterTab.WHITELIST to whitelistCount
        )

        val monthlyStats = computeMonthlyStats(latest, resolveAppLanguage(languagePreference))
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            dataSourceType = sourceType,
            themePreference = themePreference,
            languagePreference = languagePreference,
            hideWhitelistedInNonFollowers = hideWhitelisted,
            activeOwnerUsername = ownerUsername,
            activeOwnerDisplayName = ownerDisplayName,
            workspaces = workspaces.toList(),
            activeWorkspaceId = activeWorkspaceId,
            isPrivateAccount = isPrivateAccount,
            listAccessStatus = listAccessStatus,
            allAccounts = accounts,
            tabCounts = tabCounts,
            snapshots = snapshots,
            latestDiff = diff,
            lostFollowerAccounts = lostFollowerAccounts,
            newFollowerAccounts = newFollowerAccounts,
            followersCount = effectiveFollowersCount,
            followingCount = effectiveFollowingCount,
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
        val tabFiltered = when (state.selectedTab) {
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

        val quickFiltered = when (state.selectedQuickFilter) {
            ConnectionQuickFilter.ALL -> tabFiltered
            ConnectionQuickFilter.WITH_NOTES -> tabFiltered.filter {
                it.customTag.isNotBlank() || it.customNote.isNotBlank()
            }
            ConnectionQuickFilter.CLOSE_FRIENDS -> tabFiltered.filter { it.isCloseFriend }
        }

        val q = state.searchQuery.trim().lowercase(Locale.ROOT)
        val searched = if (q.isEmpty()) {
            quickFiltered
        } else {
            quickFiltered.filter { acc -> acc.searchBlob.contains(q) }
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

    private fun computeMonthlyStats(snapshot: Snapshot, language: AppLanguage): List<MonthlyStat> {
        val monthLocale = if (language == AppLanguage.TR) Locale("tr", "TR") else Locale.US
        val keyFmt = SimpleDateFormat("yyyy-MM", Locale.US)
        val labelFmt = SimpleDateFormat("MMM", monthLocale)

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
            months.add(keyFmt.format(d) to labelFmt.format(d).replaceFirstChar { it.uppercase(monthLocale) })
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
        syncActiveWorkspaceInList()
        val state = _uiState.value
        val persisted = PersistedAppState(
            workspaces = workspaces.toList(),
            activeWorkspaceId = activeWorkspaceId,
            themePreference = state.themePreference,
            languagePreference = state.languagePreference,
            hideWhitelistedInNonFollowers = state.hideWhitelistedInNonFollowers
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveState(persisted)
        }
    }
}
