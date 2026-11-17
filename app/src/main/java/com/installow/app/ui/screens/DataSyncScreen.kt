package com.installow.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.installow.app.model.LanguagePreference
import com.installow.app.model.ThemePreference
import com.installow.app.ui.components.GlassCard
import com.installow.app.ui.components.PillBadge
import com.installow.app.ui.components.shareTextReport
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.ui.viewmodel.InstallowUiState
import java.util.Locale

@Composable
fun DataSyncScreen(
    uiState: InstallowUiState,
    onOpenFilePicker: () -> Unit,
    onOpenWebSync: () -> Unit,
    onSimulateChange: () -> Unit,
    onResetDemo: () -> Unit,
    onThemeSelect: (ThemePreference) -> Unit,
    onLanguageSelect: (LanguagePreference) -> Unit,
    onBuildExportText: () -> String,
    onRunSecurityAudit: () -> Unit,
    onClearWebCookies: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var showGuide by remember { mutableStateOf(false) }
    var showReleaseNotes by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = strings.dataSettingsTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.dataSettingsSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
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
                        Icon(Icons.Rounded.FileUpload, null, modifier = Modifier.size(18.dp))
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

        // Görünüm & Dil (Segmented Kontroller)
        item {
            GlassCard(padding = 16.dp) {
                Text(
                    text = strings.appearanceTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedPreferenceRow(
                    options = listOf(
                        ThemePreference.DARK to strings.themeDark,
                        ThemePreference.LIGHT to strings.themeLight,
                        ThemePreference.SYSTEM to strings.themeSystem
                    ),
                    selectedValue = uiState.themePreference,
                    onSelect = onThemeSelect
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.languageTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedPreferenceRow(
                    options = listOf(
                        LanguagePreference.TURKISH to strings.langTurkish,
                        LanguagePreference.ENGLISH to strings.langEnglish,
                        LanguagePreference.SYSTEM to strings.langSystem
                    ),
                    selectedValue = uiState.languagePreference,
                    onSelect = onLanguageSelect
                )
            }
        }

        // Siber Güvenlik & Gizlilik Kalkanı
        item {
            val checks = uiState.securityChecks
            val passedCount = checks.count { it.passed }
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
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = strings.cyberShieldTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = palette.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    PillBadge(
                        text = if (checks.isEmpty()) {
                            strings.cyberShieldActiveBadge
                        } else {
                            String.format(Locale.ROOT, strings.cyberShieldVerifiedFormat, passedCount, checks.size)
                        },
                        color = if (checks.isEmpty() || passedCount == checks.size) AccentEmerald else AccentCoral
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SecurityMeasureLine(strings.shieldLineAes)
                    SecurityMeasureLine(strings.shieldLineHttps)
                    SecurityMeasureLine(strings.shieldLineBridge)
                    SecurityMeasureLine(strings.shieldLineZipCsv)
                    SecurityMeasureLine(strings.shieldLineBackup)
                }

                if (checks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceElevated)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        checks.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.passed) Icons.Rounded.CheckCircle else Icons.Rounded.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (item.passed) AccentEmerald else AccentCoral,
                                    modifier = Modifier
                                        .padding(top = 1.dp)
                                        .size(16.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = palette.textPrimary
                                    )
                                    Text(
                                        text = item.detail,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.textMuted
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onRunSecurityAudit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentContrastBg,
                        contentColor = palette.accentContrastFg
                    )
                ) {
                    Icon(Icons.Rounded.Security, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.runSecurityTestButton, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onClearWebCookies,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.DeleteSweep, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.clearWebCookiesButton, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Araçlar ve Dışa Aktarma (Gruplu Satırlar)
        item {
            GlassCard(padding = 0.dp) {
                SettingsActionRow(
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
                SettingsActionRow(
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
                SettingsActionRow(
                    icon = Icons.Rounded.RestartAlt,
                    title = strings.resetDemoRowTitle,
                    subtitle = strings.resetDemoRowSubtitle,
                    onClick = onResetDemo
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 52.dp),
                    thickness = 0.5.dp,
                    color = palette.border
                )
                SettingsActionRow(
                    icon = Icons.Rounded.NewReleases,
                    title = strings.releaseNotesRowTitle,
                    subtitle = strings.releaseNotesRowSubtitle,
                    onClick = { showReleaseNotes = true }
                )
            }
        }

        item {
            Text(
                text = strings.footerSignature,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }

    if (showReleaseNotes) {
        AlertDialog(
            onDismissRequest = { showReleaseNotes = false },
            containerColor = palette.surfaceElevated,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
            title = {
                Text(
                    text = strings.releaseNotesDialogTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = strings.releaseNotesDialogBody,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showReleaseNotes = false }) {
                    Text(
                        text = strings.releaseNotesClose,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )
    }
}

@Composable
private fun <T> SegmentedPreferenceRow(
    options: List<Pair<T, String>>,
    selectedValue: T,
    onSelect: (T) -> Unit
) {
    val palette = LocalInstallowPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceElevated)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (value, title) ->
            val selected = selectedValue == value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (selected) palette.surface else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .then(
                        if (selected) {
                            Modifier.border(0.75.dp, palette.border, RoundedCornerShape(9.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSelect(value) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) palette.textPrimary else palette.textSecondary
                )
            }
        }
    }
}

@Composable
private fun SecurityMeasureLine(text: String) {
    val palette = LocalInstallowPalette.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = AccentEmerald,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = palette.textSecondary
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
