package com.installow.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.installow.app.model.ConnectionQuickFilter
import com.installow.app.model.FilterTab
import com.installow.app.model.InstaAccount
import com.installow.app.model.RelationCategory
import com.installow.app.model.SortOption
import com.installow.app.ui.components.CategoryBadge
import com.installow.app.ui.components.GlassCard
import com.installow.app.ui.components.GradientAvatar
import com.installow.app.ui.components.PillBadge
import com.installow.app.ui.components.formatEpochSeconds
import com.installow.app.ui.components.formatFollowerCount
import com.installow.app.ui.components.openInstagramProfile
import com.installow.app.ui.components.shareTextReport
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.theme.AccentAmber
import com.installow.app.ui.theme.AccentBlue
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.AccentIndigo
import com.installow.app.ui.theme.BrandRose
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.ui.viewmodel.InstallowUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(
    uiState: InstallowUiState,
    onSelectTab: (FilterTab) -> Unit,
    onSelectQuickFilter: (ConnectionQuickFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onToggleHideWhitelisted: () -> Unit,
    onOpenAccountSheet: (InstaAccount?) -> Unit,
    onToggleWhitelist: (String) -> Unit,
    onUpdateTagAndNote: (String, String, String) -> Unit,
    onMarkUnfollowed: (String) -> Unit,
    onStartQueue: () -> Unit,
    onNextQueue: () -> Unit,
    onPrevQueue: () -> Unit,
    onStopQueue: () -> Unit,
    onBuildExportText: () -> String,
    onShowMessage: (String) -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        // Başlık + Sırala / Paylaş
        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.tabLabel(uiState.selectedTab),
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.textPrimary
                    )
                    Text(
                        text = String.format(
                            Locale.ROOT,
                            strings.connectionsCountFormat,
                            uiState.filteredAccounts.size,
                            strings.sortLabel(uiState.sortOption)
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box {
                        Surface(
                            onClick = { sortMenuExpanded = true },
                            shape = CircleShape,
                            color = palette.surface,
                            border = BorderStroke(0.75.dp, palette.border),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Sort,
                                    contentDescription = strings.sortContentDesc,
                                    tint = palette.textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = strings.sortLabel(option),
                                            fontWeight = if (option == uiState.sortOption) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (option == uiState.sortOption) BrandRose else palette.textPrimary
                                        )
                                    },
                                    onClick = {
                                        onSortChange(option)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = {
                            shareTextReport(
                                context = context,
                                title = "${strings.shareReportSubjectPrefix}${strings.tabLabel(uiState.selectedTab)}",
                                content = onBuildExportText(),
                                chooserTitle = strings.shareChooserTitle
                            )
                        },
                        shape = CircleShape,
                        color = palette.surface,
                        border = BorderStroke(0.75.dp, palette.border),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = strings.shareContentDesc,
                                tint = palette.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sade Çerçevesiz Arama Çubuğu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = palette.textMuted,
                    modifier = Modifier.size(18.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.searchQuery.isEmpty()) {
                        Text(
                            text = strings.searchPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textMuted
                        )
                    }
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
                        cursorBrush = SolidColor(BrandRose),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (uiState.searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = strings.clearContentDesc,
                        tint = palette.textSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onSearchChange("") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Yüksek Kontrastlı Minimal Kategori Sekmeleri
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterTab.entries.forEach { tab ->
                val selected = tab == uiState.selectedTab
                val count = uiState.tabCounts[tab] ?: 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selected) palette.accentContrastBg else palette.surface
                        )
                        .border(
                            width = 0.75.dp,
                            color = if (selected) palette.accentContrastBg else palette.border,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "${strings.tabShortLabel(tab)} $count",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) palette.accentContrastFg else palette.textSecondary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hızlı Not & Yakın Arkadaş Filtreleri
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionQuickFilter.entries.forEach { filter ->
                val selected = filter == uiState.selectedQuickFilter
                val label = when (filter) {
                    ConnectionQuickFilter.ALL -> strings.allTagsFilterChip
                    ConnectionQuickFilter.WITH_NOTES -> strings.onlyNotesFilterChip
                    ConnectionQuickFilter.CLOSE_FRIENDS -> strings.onlyCloseFriendsChip
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) BrandRose.copy(alpha = 0.14f) else palette.surface)
                        .border(
                            width = 0.75.dp,
                            color = if (selected) BrandRose.copy(alpha = 0.5f) else palette.border,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectQuickFilter(filter) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) BrandRose else palette.textSecondary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // Beni Takip Etmeyenler Sekmesi Kontrolleri
        if (uiState.selectedTab == FilterTab.NON_FOLLOWERS) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surface)
                        .border(0.75.dp, palette.border, RoundedCornerShape(10.dp))
                        .clickable(onClick = onToggleHideWhitelisted)
                        .padding(horizontal = 11.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VisibilityOff,
                        contentDescription = null,
                        tint = if (uiState.hideWhitelistedInNonFollowers) palette.textPrimary else palette.textMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (uiState.hideWhitelistedInNonFollowers) strings.whitelistHidden else strings.whitelistIncluded,
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.accentContrastBg)
                        .clickable(onClick = onStartQueue)
                        .padding(horizontal = 13.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay,
                        contentDescription = null,
                        tint = palette.accentContrastFg,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.smartQueueChip,
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.accentContrastFg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Aktif Sıralı İnceleme Kartı
        val queueAccount = uiState.currentQueueAccount
        if (uiState.isQueueActive && queueAccount != null) {
            Spacer(modifier = Modifier.height(10.dp))
            SmartQueueCard(
                account = queueAccount,
                index = uiState.queueIndex,
                total = uiState.queueUsernames.size,
                onOpenProfile = { openInstagramProfile(context, queueAccount.username) },
                onMarkUnfollowed = { onMarkUnfollowed(queueAccount.username) },
                onWhitelist = {
                    onToggleWhitelist(queueAccount.username)
                    onNextQueue()
                },
                onPrev = onPrevQueue,
                onNext = onNextQueue,
                onClose = onStopQueue,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
        }

        val isFollowerCountSort = uiState.sortOption == SortOption.FOLLOWERS_DESC ||
            uiState.sortOption == SortOption.FOLLOWERS_ASC
        if (isFollowerCountSort &&
            uiState.filteredAccounts.isNotEmpty() &&
            uiState.filteredAccounts.none { it.followerCount != null }
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = AccentAmber,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = strings.followerCountZipNotice,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sade Düz Liste
        if (uiState.filteredAccounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = strings.emptyMatchesTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.emptyMatchesSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(
                    items = uiState.filteredAccounts,
                    key = { _, acc -> acc.username },
                    contentType = { _, _ -> "account_row" }
                ) { index, account ->
                    AccountListRow(
                        account = account,
                        onRowClick = { onOpenAccountSheet(account) },
                        onOpenInstagram = { openInstagramProfile(context, account.username) }
                    )
                    if (index < uiState.filteredAccounts.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 76.dp, end = 18.dp),
                            thickness = 0.5.dp,
                            color = palette.border
                        )
                    }
                }
            }
        }
    }

    // Kişi Detay Alt Penceresi
    val sheetAccount = uiState.selectedAccountForSheet
    if (sheetAccount != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onOpenAccountSheet(null) },
            sheetState = sheetState,
            containerColor = palette.surfaceElevated,
            contentColor = palette.textPrimary
        ) {
            AccountDetailSheetContent(
                account = sheetAccount,
                onToggleWhitelist = { onToggleWhitelist(sheetAccount.username) },
                onSaveTagAndNote = { tag, note ->
                    onUpdateTagAndNote(sheetAccount.username, tag, note)
                },
                onOpenInstagram = { openInstagramProfile(context, sheetAccount.username) },
                onMarkUnfollowed = { onMarkUnfollowed(sheetAccount.username) },
                onShowMessage = onShowMessage
            )
        }
    }
}

@Composable
private fun AccountListRow(
    account: InstaAccount,
    onRowClick: () -> Unit,
    onOpenInstagram: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    val statusColor = when (account.category) {
        RelationCategory.NON_FOLLOWER -> AccentCoral
        RelationCategory.FAN -> AccentBlue
        RelationCategory.MUTUAL -> AccentEmerald
        RelationCategory.PENDING_REQUEST -> AccentAmber
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRowClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GradientAvatar(account = account, size = 46.dp)

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = account.displayName.ifBlank { account.username },
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (account.customTag.isNotBlank()) {
                    PillBadge(text = account.customTag, color = palette.textSecondary)
                }
            }

            val metaParts = buildList {
                add("@${account.username}")
                if (account.followerCount != null) {
                    add("${formatFollowerCount(account.followerCount, strings.language)}${strings.followerCountSuffix}")
                }
            }.joinToString(" · ")

            Text(
                text = metaParts,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = strings.categoryLabel(account.category),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
                if (account.relevantTimestamp > 0L) {
                    Text(
                        text = "· ${formatEpochSeconds(account.relevantTimestamp, strings)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        IconButton(
            onClick = onOpenInstagram,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                contentDescription = strings.openInInstagramDesc,
                tint = palette.textMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SmartQueueCard(
    account: InstaAccount,
    index: Int,
    total: Int,
    onOpenProfile: () -> Unit,
    onMarkUnfollowed: () -> Unit,
    onWhitelist: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    GlassCard(
        modifier = modifier,
        containerColor = palette.surfaceElevated,
        padding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(Locale.ROOT, strings.smartQueueHeaderFormat, index + 1, total),
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary
            )
            IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = strings.closeDesc,
                    tint = palette.textSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientAvatar(account = account, size = 42.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.displayName.ifBlank { "@${account.username}" },
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = "@${account.username} · ${formatEpochSeconds(account.iFollowedAt, strings)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenProfile,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accentContrastBg,
                    contentColor = palette.accentContrastFg
                ),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text(strings.openInIgShort, style = MaterialTheme.typography.labelMedium)
            }
            Button(
                onClick = onMarkUnfollowed,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCoral.copy(alpha = 0.14f),
                    contentColor = AccentCoral
                ),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Icon(Icons.Rounded.PersonRemove, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text(strings.markedUnfollowedShort, style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(
                onClick = onWhitelist,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Icon(Icons.Rounded.Shield, null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onPrev, enabled = index > 0) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text(strings.prevButton)
            }
            TextButton(onClick = onNext) {
                Text(if (index + 1 >= total) strings.finishButton else strings.nextButton)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccountDetailSheetContent(
    account: InstaAccount,
    onToggleWhitelist: () -> Unit,
    onSaveTagAndNote: (String, String) -> Unit,
    onOpenInstagram: () -> Unit,
    onMarkUnfollowed: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    var selectedTag by remember(account.username) { mutableStateOf(account.customTag) }
    var noteText by remember(account.username) { mutableStateOf(account.customNote) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GradientAvatar(account = account, size = 56.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.displayName.ifBlank { "@${account.username}" },
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString("@${account.username}"))
                        onShowMessage(
                            String.format(Locale.ROOT, strings.usernameCopiedMsgFormat, account.username)
                        )
                    }
                ) {
                    Text(
                        text = "@${account.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = strings.copyUsernameDesc,
                        tint = palette.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryBadge(account.category)
                    if (account.isCloseFriend) {
                        PillBadge(strings.closeFriendBadge, AccentEmerald)
                    }
                }
            }
        }

        GlassCard(containerColor = palette.surface, padding = 14.dp) {
            if (account.followerCount != null) {
                Text(
                    text = "${strings.followerCountDetailPrefix}${formatFollowerCount(account.followerCount, strings.language)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (account.followedMeAt != null) {
                Text(
                    text = "${strings.followedMeDatePrefix}${formatEpochSeconds(account.followedMeAt, strings)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
            if (account.iFollowedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${strings.iFollowedDatePrefix}${formatEpochSeconds(account.iFollowedAt, strings)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
            if (account.pendingRequestAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${strings.pendingDatePrefix}${formatEpochSeconds(account.pendingRequestAt, strings)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(palette.surface)
                .border(0.75.dp, palette.border, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.whitelistSwitchTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.whitelistSwitchSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
            }
            Switch(
                checked = account.isWhitelisted,
                onCheckedChange = { onToggleWhitelist() },
                colors = SwitchDefaults.colors(checkedTrackColor = BrandRose)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = strings.tagSectionTitle,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                strings.presetTags.forEach { tag ->
                    val isSelected = selectedTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTag = if (isSelected) "" else tag },
                        label = { Text(tag, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.accentContrastBg,
                            selectedLabelColor = palette.accentContrastFg
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it.take(300) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(strings.personalNoteLabel) },
            placeholder = { Text(strings.personalNotePlaceholder) },
            shape = RoundedCornerShape(12.dp),
            maxLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { onSaveTagAndNote(selectedTag, noteText) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(strings.saveButton)
            }

            Button(
                onClick = onOpenInstagram,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accentContrastBg,
                    contentColor = palette.accentContrastFg
                )
            ) {
                Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                Text(strings.openInstagramButton)
            }
        }

        if (account.isFollowing) {
            Button(
                onClick = onMarkUnfollowed,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCoral.copy(alpha = 0.14f),
                    contentColor = AccentCoral
                )
            ) {
                Icon(Icons.Rounded.PersonRemove, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                Text(strings.markUnfollowedButton)
            }
        }
    }
}
