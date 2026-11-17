package com.installow.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.installow.app.ui.components.GlassCard
import com.installow.app.ui.components.PillBadge
import com.installow.app.ui.components.shareTextReport
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.theme.AccentBlue
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.ui.viewmodel.InstallowUiState
import java.util.Locale

@Composable
fun DataScreen(
    uiState: InstallowUiState,
    onOpenAccountSwitcher: () -> Unit,
    onOpenFilePicker: () -> Unit,
    onOpenWebSync: () -> Unit,
    onSimulateChange: () -> Unit,
    onResetDemo: () -> Unit,
    onBuildExportText: () -> String
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var showGuide by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = strings.dataTabTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.dataTabSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
        }

        // Çoklu Hesap Yönetimi & Kullanıcı Adından Hızlı Çekim
        item {
            GlassCard(padding = 18.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ManageAccounts,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = strings.accountsCardTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = palette.textPrimary
                        )
                    }
                    PillBadge("${uiState.workspaces.size}", AccentBlue)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = String.format(
                        Locale.ROOT,
                        strings.accountsCardDescFormat,
                        uiState.activeOwnerUsername,
                        uiState.workspaces.size
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.publicLookupBannerDesc,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenAccountSwitcher,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentContrastBg,
                        contentColor = palette.accentContrastFg
                    )
                ) {
                    Icon(Icons.Rounded.PersonSearch, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.manageAccountsButton, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Yöntem 1: Resmi Instagram Arşivi
        item {
            GlassCard(padding = 18.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.officialArchiveTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PillBadge(strings.zeroRiskRecommendedBadge, AccentEmerald)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.officialArchiveDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenFilePicker,
                    enabled = !uiState.isImporting,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentContrastBg,
                        contentColor = palette.accentContrastFg
                    )
                ) {
                    if (uiState.isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(17.dp),
                            strokeWidth = 2.dp,
                            color = palette.accentContrastFg
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(strings.processingFilesText, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Rounded.Download, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(strings.selectArchiveButton, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceElevated)
                        .clickable { showGuide = !showGuide }
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.howToExportTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textPrimary
                    )
                    Icon(
                        imageVector = if (showGuide) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = palette.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = showGuide) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GuideStep("1", strings.guideStep1)
                        GuideStep("2", strings.guideStep2)
                        GuideStep("3", strings.guideStep3)
                        GuideStep("4", strings.guideStep4)
                    }
                }
            }
        }

        // Yöntem 2: Canlı Web Senkronizasyonu
        item {
            GlassCard(padding = 18.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.liveWebSyncTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PillBadge(strings.slowModeBadge, palette.textSecondary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.liveWebSyncDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onOpenWebSync,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Language, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.connectInstagramWebButton, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Veri Araçları
        item {
            GlassCard(padding = 0.dp) {
                DataActionRow(
                    icon = Icons.Rounded.Share,
                    title = strings.exportCsvRowTitle,
                    subtitle = String.format(
                        Locale.ROOT,
                        strings.exportCsvRowSubtitleFormat,
                        strings.tabLabel(uiState.selectedTab),
                        uiState.filteredAccounts.size
                    ),
                    onClick = {
                        shareTextReport(
                            context = context,
                            title = "${strings.shareReportSubjectPrefix}${strings.tabLabel(uiState.selectedTab)}",
                            content = onBuildExportText(),
                            chooserTitle = strings.shareChooserTitle
                        )
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 52.dp),
                    thickness = 0.5.dp,
                    color = palette.border
                )
                DataActionRow(
                    icon = Icons.Rounded.AutoFixHigh,
                    title = strings.simulateRowTitle,
                    subtitle = strings.simulateRowSubtitle,
                    onClick = onSimulateChange
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 52.dp),
                    thickness = 0.5.dp,
                    color = palette.border
                )
                DataActionRow(
                    icon = Icons.Rounded.RestartAlt,
                    title = strings.resetDemoRowTitle,
                    subtitle = strings.resetDemoRowSubtitle,
                    onClick = onResetDemo
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun DataActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = palette.textMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun GuideStep(step: String, text: String) {
    val palette = LocalInstallowPalette.current
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(palette.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}
