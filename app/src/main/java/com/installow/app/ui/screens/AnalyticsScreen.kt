package com.installow.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.installow.app.model.AppLanguage
import com.installow.app.model.FilterTab
import com.installow.app.model.InstaAccount
import com.installow.app.ui.components.GlassCard
import com.installow.app.ui.components.GradientAvatar
import com.installow.app.ui.components.MonthlyActivityBarChart
import com.installow.app.ui.components.PillBadge
import com.installow.app.ui.components.formatEpochSeconds
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.theme.AccentAmber
import com.installow.app.ui.theme.AccentBlue
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.ui.viewmodel.InstallowUiState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ANALYTICS_DATE_FORMATTER_TR: DateTimeFormatter = DateTimeFormatter
    .ofPattern("d MMM yyyy · HH:mm", Locale("tr", "TR"))
    .withZone(ZoneId.systemDefault())
private val ANALYTICS_DATE_FORMATTER_EN: DateTimeFormatter = DateTimeFormatter
    .ofPattern("MMM d, yyyy · HH:mm", Locale.US)
    .withZone(ZoneId.systemDefault())
private val ANALYTICS_DATE_FORMATTER_DE: DateTimeFormatter = DateTimeFormatter
    .ofPattern("d. MMM yyyy · HH:mm", Locale.GERMANY)
    .withZone(ZoneId.systemDefault())
private val ANALYTICS_DATE_FORMATTER_FR: DateTimeFormatter = DateTimeFormatter
    .ofPattern("d MMM yyyy · HH:mm", Locale.FRANCE)
    .withZone(ZoneId.systemDefault())
private val ANALYTICS_DATE_FORMATTER_ES: DateTimeFormatter = DateTimeFormatter
    .ofPattern("d MMM yyyy · HH:mm", Locale("es", "ES"))
    .withZone(ZoneId.systemDefault())

@Composable
fun AnalyticsScreen(
    uiState: InstallowUiState,
    onOpenTab: (FilterTab) -> Unit,
    onOpenAccountSheet: (InstaAccount) -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    val dateFormatter = when (strings.language) {
        AppLanguage.TR -> ANALYTICS_DATE_FORMATTER_TR
        AppLanguage.DE -> ANALYTICS_DATE_FORMATTER_DE
        AppLanguage.FR -> ANALYTICS_DATE_FORMATTER_FR
        AppLanguage.ES -> ANALYTICS_DATE_FORMATTER_ES
        AppLanguage.EN -> ANALYTICS_DATE_FORMATTER_EN
    }

    val healthTitle = when {
        uiState.reciprocityPercent >= 75 -> strings.healthStrong
        uiState.reciprocityPercent >= 50 -> strings.healthBalanced
        else -> strings.healthCleanupSuggested
    }
    val healthColor = when {
        uiState.reciprocityPercent >= 75 -> AccentEmerald
        uiState.reciprocityPercent >= 50 -> AccentBlue
        else -> AccentAmber
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = strings.analyticsTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.analyticsSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
        }

        // Özet Durum Kartı
        item {
            GlassCard(padding = 18.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.accountStatusLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.textMuted
                        )
                        Text(
                            text = healthTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = palette.textPrimary
                        )
                    }
                    PillBadge(
                        text = String.format(Locale.ROOT, strings.matchBadgeFormat, uiState.reciprocityPercent),
                        color = healthColor
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        label = strings.metricReturnRate,
                        value = if (strings.language == AppLanguage.TR) {
                            "%${uiState.fanReturnPercent}"
                        } else {
                            "${uiState.fanReturnPercent}%"
                        },
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = strings.metricNonFollowerShort,
                        value = "${uiState.nonFollowersTotalCount}",
                        color = AccentCoral,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = strings.metricFansShort,
                        value = "${uiState.fansCount}",
                        color = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Aylık Grafik
        item {
            GlassCard(padding = 18.dp) {
                Text(
                    text = strings.last6MonthsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.last6MonthsSubtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(14.dp))
                MonthlyActivityBarChart(stats = uiState.monthlyStats)
            }
        }

        // Öne Çıkanlar (Tek Gruplu Kart)
        item {
            GlassCard(padding = 0.dp) {
                Text(
                    text = strings.highlightsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp)
                )
                HorizontalDivider(thickness = 0.5.dp, color = palette.border)

                uiState.earliestFollower?.let { acc ->
                    MilestoneRow(
                        label = strings.firstFollowerLabel,
                        account = acc,
                        dateText = formatEpochSeconds(acc.followedMeAt, strings),
                        onClick = { onOpenAccountSheet(acc) }
                    )
                }

                uiState.earliestNonFollower?.let { acc ->
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 70.dp),
                        thickness = 0.5.dp,
                        color = palette.border
                    )
                    MilestoneRow(
                        label = strings.oldestNonFollowerLabel,
                        account = acc,
                        dateText = formatEpochSeconds(acc.iFollowedAt, strings),
                        onClick = { onOpenAccountSheet(acc) }
                    )
                }

                uiState.longestPending?.let { acc ->
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 70.dp),
                        thickness = 0.5.dp,
                        color = palette.border
                    )
                    MilestoneRow(
                        label = strings.longestPendingLabel,
                        account = acc,
                        dateText = formatEpochSeconds(acc.pendingRequestAt, strings),
                        onClick = { onOpenTab(FilterTab.PENDING) }
                    )
                }
            }
        }

        // Kayıt Geçmişi (Tek Gruplu Kart)
        item {
            val reversedSnaps = uiState.snapshots.reversed()
            GlassCard(padding = 0.dp) {
                Text(
                    text = String.format(Locale.ROOT, strings.snapshotHistoryFormat, reversedSnaps.size),
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp)
                )
                HorizontalDivider(thickness = 0.5.dp, color = palette.border)

                reversedSnaps.forEachIndexed { idx, snap ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = snap.sourceLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.textPrimary
                            )
                            Text(
                                text = dateFormatter.format(Instant.ofEpochMilli(snap.createdAtMillis)),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format(Locale.ROOT, strings.followersCountUnit, snap.followerCount),
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = String.format(Locale.ROOT, strings.followingCountUnit, snap.followingCount),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textSecondary
                            )
                        }
                    }
                    if (idx < reversedSnaps.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp),
                            thickness = 0.5.dp,
                            color = palette.border
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalInstallowPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceElevated)
            .padding(horizontal = 10.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary
        )
    }
}

@Composable
private fun MilestoneRow(
    label: String,
    account: InstaAccount,
    dateText: String,
    onClick: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GradientAvatar(account = account, size = 42.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted
            )
            Text(
                text = "${account.displayName.ifBlank { account.username }} (@${account.username})",
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
        }
    }
}
