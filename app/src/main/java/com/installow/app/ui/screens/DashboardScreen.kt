package com.installow.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.installow.app.model.DataSourceType
import com.installow.app.model.FilterTab
import com.installow.app.model.ListAccessStatus
import com.installow.app.ui.components.DonutSegment
import com.installow.app.ui.components.GlassCard
import com.installow.app.ui.components.RelationshipDonutChart
import com.installow.app.ui.components.formatRelativeTime
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

@Composable
fun DashboardScreen(
    uiState: InstallowUiState,
    onOpenTab: (FilterTab) -> Unit,
    onOpenAccountSwitcher: () -> Unit,
    onStartQueue: () -> Unit,
    onOpenFilePicker: () -> Unit,
    onOpenWebSync: () -> Unit,
    onRetryLookup: () -> Unit,
    onSimulateChange: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Başlık + by kyhlaer + Etkileşimli Hesap Seçici + Ferah Aksiyon Butonları
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = "Installow",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = "by kyhlaer",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val dotColor = when (uiState.dataSourceType) {
                        DataSourceType.DEMO -> AccentAmber
                        DataSourceType.PUBLIC_LOOKUP -> AccentBlue
                        DataSourceType.OFFICIAL_EXPORT -> AccentEmerald
                        DataSourceType.WEB_SYNC -> AccentEmerald
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(palette.surface)
                            .border(0.75.dp, palette.border, RoundedCornerShape(9.dp))
                            .clickable(onClick = onOpenAccountSwitcher)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Text(
                            text = "@${uiState.activeOwnerUsername} · ${strings.dataSourceLabel(uiState.dataSourceType)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (uiState.isPrivateAccount == true) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = strings.privateBadge,
                                tint = AccentAmber,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = palette.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Surface(
                        onClick = { onOpenTab(FilterTab.ALL_FOLLOWERS) },
                        shape = CircleShape,
                        color = palette.surface,
                        border = BorderStroke(0.75.dp, palette.border),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = strings.searchContentDesc,
                                tint = palette.textPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = onOpenFilePicker,
                        shape = CircleShape,
                        color = palette.surface,
                        border = BorderStroke(0.75.dp, palette.border),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = strings.uploadDataContentDesc,
                                tint = palette.textPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2x2 Sade İstatistik Izgarası
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        title = strings.metricNonFollowersTitle,
                        count = uiState.nonFollowersActionableCount,
                        hint = if (uiState.hideWhitelistedInNonFollowers && uiState.whitelistCount > 0) {
                            strings.metricNonFollowersHintWhitelist
                        } else {
                            strings.metricNonFollowersHintDefault
                        },
                        dotColor = AccentCoral,
                        onClick = { onOpenTab(FilterTab.NON_FOLLOWERS) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        title = strings.metricFansTitle,
                        count = uiState.fansCount,
                        hint = strings.metricFansHint,
                        dotColor = AccentBlue,
                        onClick = { onOpenTab(FilterTab.FANS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        title = strings.metricFollowersTitle,
                        count = uiState.followersCount,
                        hint = strings.metricAllListHint,
                        dotColor = AccentEmerald,
                        onClick = { onOpenTab(FilterTab.ALL_FOLLOWERS) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        title = strings.metricFollowingTitle,
                        count = uiState.followingCount,
                        hint = strings.metricAllListHint,
                        dotColor = BrandRose,
                        onClick = { onOpenTab(FilterTab.ALL_FOLLOWING) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Gizli Hesap / Oturum Gerektiren Liste Uyarısı
        if (uiState.listAccessStatus != ListAccessStatus.FULL_DATA) {
            item {
                val (icon, tint, bodyText) = when (uiState.listAccessStatus) {
                    ListAccessStatus.PRIVATE_ACCOUNT -> Triple(
                        Icons.Rounded.Lock,
                        AccentAmber,
                        strings.accountListNoticePrivateDesc
                    )
                    ListAccessStatus.LOGIN_REQUIRED_FOR_LISTS -> Triple(
                        Icons.Rounded.Public,
                        AccentBlue,
                        strings.accountListNoticeLoginDesc
                    )
                    ListAccessStatus.PROFILE_NOT_FOUND -> Triple(
                        Icons.Rounded.WarningAmber,
                        AccentCoral,
                        strings.accountListNoticeNotFoundDesc
                    )
                    else -> Triple(
                        Icons.Rounded.WarningAmber,
                        AccentAmber,
                        strings.accountListNoticeErrorDesc
                    )
                }

                GlassCard(
                    padding = 16.dp,
                    borderColor = tint.copy(alpha = 0.45f)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(18.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            if (uiState.listAccessStatus == ListAccessStatus.PRIVATE_ACCOUNT) {
                                Text(
                                    text = strings.accountListNoticePrivateTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = bodyText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenWebSync,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.accentContrastBg,
                                contentColor = palette.accentContrastFg
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Icon(Icons.Rounded.Language, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(strings.webSignInActionShort, style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = onOpenFilePicker,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Icon(Icons.Rounded.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(strings.loadArchiveActionShort, style = MaterialTheme.typography.labelMedium)
                        }

                        if (uiState.listAccessStatus == ListAccessStatus.NETWORK_ERROR) {
                            OutlinedButton(
                                onClick = onRetryLookup,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Karşılıklı Takip Dağılımı
        item {
            GlassCard(padding = 18.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.followBalanceTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.textPrimary
                    )
                    val ratioFormatted = String.format(Locale.US, "%.2f", uiState.followBackRatio)
                    Text(
                        text = String.format(Locale.ROOT, strings.ratioFormat, ratioFormatted),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                RelationshipDonutChart(
                    segments = listOf(
                        DonutSegment(strings.donutMutual, uiState.mutualCount, AccentEmerald),
                        DonutSegment(strings.donutNonFollower, uiState.nonFollowersTotalCount, AccentCoral),
                        DonutSegment(strings.donutFan, uiState.fansCount, AccentBlue)
                    ),
                    centerPercent = uiState.reciprocityPercent,
                    centerLabel = strings.donutMatchLabel
                )
            }
        }

        // Son Değişimler
        item {
            val diff = uiState.latestDiff
            GlassCard(padding = 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.recentChangesTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = palette.textPrimary
                        )
                        Text(
                            text = if (diff != null) {
                                "${strings.lastRecordPrefix}${formatRelativeTime(diff.previousTimeMillis, strings)}"
                            } else {
                                strings.compareOnNextSync
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.textMuted
                        )
                    }
                    if (uiState.dataSourceType == DataSourceType.DEMO) {
                        Text(
                            text = strings.simulateAction,
                            style = MaterialTheme.typography.labelLarge,
                            color = BrandRose,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onSimulateChange)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = palette.border)

                GroupedNavRow(
                    title = strings.lostFollowersTitle,
                    subtitle = diff?.lostFollowers?.take(2)?.takeIf { it.isNotEmpty() }
                        ?.joinToString(", ") { "@$it" } ?: strings.noChangesText,
                    count = diff?.lostFollowers?.size ?: 0,
                    countColor = AccentCoral,
                    onClick = { onOpenTab(FilterTab.UNFOLLOWERS_DIFF) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 0.5.dp,
                    color = palette.border
                )

                GroupedNavRow(
                    title = strings.newFollowersTitle,
                    subtitle = diff?.newFollowers?.take(2)?.takeIf { it.isNotEmpty() }
                        ?.joinToString(", ") { "@$it" } ?: strings.noChangesText,
                    count = diff?.newFollowers?.size ?: 0,
                    countColor = AccentEmerald,
                    onClick = { onOpenTab(FilterTab.ALL_FOLLOWERS) }
                )

                if ((diff?.unfollowedByMe?.size ?: 0) > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 0.5.dp,
                        color = palette.border
                    )
                    GroupedNavRow(
                        title = strings.unfollowedByMeRowTitle,
                        subtitle = diff!!.unfollowedByMe.take(2).joinToString(", ") { "@$it" },
                        count = diff.unfollowedByMe.size,
                        countColor = palette.textSecondary,
                        onClick = { onOpenTab(FilterTab.ALL_FOLLOWING) }
                    )
                }
            }
        }

        // Diğer Listeler
        item {
            GlassCard(padding = 0.dp) {
                GroupedNavRow(
                    title = strings.mutualsRowTitle,
                    subtitle = strings.mutualsRowSubtitle,
                    count = uiState.mutualCount,
                    countColor = palette.textPrimary,
                    onClick = { onOpenTab(FilterTab.MUTUALS) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 0.5.dp,
                    color = palette.border
                )
                GroupedNavRow(
                    title = strings.pendingRowTitle,
                    subtitle = strings.pendingRowSubtitle,
                    count = uiState.pendingCount,
                    countColor = AccentAmber,
                    onClick = { onOpenTab(FilterTab.PENDING) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 0.5.dp,
                    color = palette.border
                )
                GroupedNavRow(
                    title = strings.whitelistRowTitle,
                    subtitle = strings.whitelistRowSubtitle,
                    count = uiState.whitelistCount,
                    countColor = AccentIndigo,
                    onClick = { onOpenTab(FilterTab.WHITELIST) }
                )
            }
        }

        // Sıralı İnceleme Aksiyonu
        item {
            GlassCard(padding = 16.dp) {
                Text(
                    text = strings.smartQueueTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.ROOT, strings.smartQueueDescFormat, uiState.nonFollowersActionableCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onStartQueue,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentContrastBg,
                        contentColor = palette.accentContrastFg
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.startReviewButton,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }
    }
}

@Composable
private fun MetricTile(
    title: String,
    count: Int,
    hint: String,
    dotColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalInstallowPalette.current
    GlassCard(
        modifier = modifier,
        padding = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = palette.textPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GroupedNavRow(
    title: String,
    subtitle: String,
    count: Int,
    countColor: Color,
    onClick: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = countColor
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = palette.textMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
