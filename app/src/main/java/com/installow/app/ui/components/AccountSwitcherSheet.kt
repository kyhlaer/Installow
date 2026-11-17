package com.installow.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.installow.app.model.AccountWorkspace
import com.installow.app.model.DataSourceType
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.theme.AccentAmber
import com.installow.app.ui.theme.AccentBlue
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.LocalInstallowPalette
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSwitcherBottomSheet(
    workspaces: List<AccountWorkspace>,
    activeWorkspaceId: String,
    isLookingUp: Boolean,
    onDismiss: () -> Unit,
    onLookupAndAdd: (String) -> Unit,
    onSwitchWorkspace: (String) -> Unit,
    onRemoveWorkspace: (String) -> Unit
) {
    val palette = LocalInstallowPalette.current
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var usernameInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.surfaceElevated,
        contentColor = palette.textPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text(
                    text = strings.accountsSheetTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary
                )
                Text(
                    text = strings.accountsSheetSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }

            // Kullanıcı Adıyla Hesap Ekle / Çek
            GlassCard(containerColor = palette.surface, padding = 14.dp) {
                Text(
                    text = strings.addOrLookupAccountTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it.take(35) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLookingUp,
                    prefix = { Text("@", color = palette.textSecondary) },
                    placeholder = { Text(strings.usernameInputPlaceholder) },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!isLookingUp && usernameInput.isNotBlank()) {
                                onLookupAndAdd(usernameInput)
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onLookupAndAdd(usernameInput) },
                    enabled = !isLookingUp && usernameInput.trim().removePrefix("@").isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentContrastBg,
                        contentColor = palette.accentContrastFg
                    )
                ) {
                    if (isLookingUp) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(17.dp),
                            strokeWidth = 2.dp,
                            color = palette.accentContrastFg
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(strings.lookingUpAccountButton, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Rounded.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.lookupAndAddButton, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Kayıtlı Hesaplar Listesi
            Text(
                text = String.format(Locale.ROOT, strings.savedAccountsTitle, workspaces.size),
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = workspaces, key = { it.id }) { ws ->
                    val isActive = ws.id == activeWorkspaceId
                    val dotColor = when (ws.dataSourceType) {
                        DataSourceType.DEMO -> AccentAmber
                        DataSourceType.PUBLIC_LOOKUP -> AccentBlue
                        DataSourceType.OFFICIAL_EXPORT -> AccentEmerald
                        DataSourceType.WEB_SYNC -> AccentEmerald
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSwitchWorkspace(ws.id) },
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surface,
                        border = BorderStroke(
                            width = if (isActive) 1.25.dp else 0.75.dp,
                            color = if (isActive) palette.textPrimary else palette.border
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(palette.surfaceVariant)
                                    .border(0.75.dp, palette.border, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ws.ownerUsername.take(2).uppercase(Locale.ROOT),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "@${ws.ownerUsername}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = palette.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (isActive) {
                                        PillBadge(strings.activeBadge, AccentEmerald)
                                    }
                                    if (ws.isPrivateAccount == true) {
                                        Icon(
                                            imageVector = Icons.Rounded.Lock,
                                            contentDescription = strings.privateBadge,
                                            tint = AccentAmber,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    } else if (ws.isPrivateAccount == false) {
                                        Icon(
                                            imageVector = Icons.Rounded.Public,
                                            contentDescription = strings.publicBadge,
                                            tint = AccentBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                    Text(
                                        text = "${strings.dataSourceLabel(ws.dataSourceType)} · ${
                                            String.format(
                                                Locale.ROOT,
                                                strings.accountCountsShortFormat,
                                                ws.summaryFollowersCount,
                                                ws.summaryFollowingCount
                                            )
                                        }",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (isActive) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (workspaces.size > 1) {
                                IconButton(
                                    onClick = { onRemoveWorkspace(ws.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = strings.removeAccountDesc,
                                        tint = AccentCoral,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
