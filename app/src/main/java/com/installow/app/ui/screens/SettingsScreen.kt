package com.installow.app.ui.screens

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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.installow.app.model.LanguagePreference
import com.installow.app.model.ThemePreference
import com.installow.app.ui.components.GlassCard
import com.installow.app.ui.components.PillBadge
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.ui.viewmodel.InstallowUiState
import java.util.Locale

@Composable
fun SettingsScreen(
    uiState: InstallowUiState,
    onThemeSelect: (ThemePreference) -> Unit,
    onLanguageSelect: (LanguagePreference) -> Unit,
    onRunSecurityAudit: () -> Unit,
    onClearWebCookies: () -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    var showReleaseNotes by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = strings.settingsTabTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.settingsTabSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
        }

        // Görünüm & Dil
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

                Spacer(modifier = Modifier.height(16.dp))

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

        // Sürüm Notları
        item {
            GlassCard(padding = 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReleaseNotes = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NewReleases,
                        contentDescription = null,
                        tint = palette.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.releaseNotesRowTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary
                        )
                        Text(
                            text = strings.releaseNotesRowSubtitle,
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
                    .background(if (selected) palette.surface else Color.Transparent)
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
