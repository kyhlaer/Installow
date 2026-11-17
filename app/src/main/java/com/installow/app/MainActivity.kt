package com.installow.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.installow.app.model.AppScreen
import com.installow.app.model.ThemePreference
import com.installow.app.ui.screens.AnalyticsScreen
import com.installow.app.ui.screens.ConnectionsScreen
import com.installow.app.ui.screens.DashboardScreen
import com.installow.app.ui.screens.DataSyncScreen
import com.installow.app.ui.screens.InstagramWebSyncDialog
import com.installow.app.ui.theme.InstallowTheme
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.ui.viewmodel.InstallowViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: InstallowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDarkActive = when (uiState.themePreference) {
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }

            LaunchedEffect(isDarkActive) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkActive) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDarkActive) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }

            InstallowTheme(themePreference = uiState.themePreference) {
                InstallowRootApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun InstallowRootApp(viewModel: InstallowViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = LocalInstallowPalette.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showExitDialog by remember { mutableStateOf(false) }
    var showSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(showSplash) {
        if (showSplash) {
            delay(1150L)
            showSplash = false
        }
    }

    val pagerState = rememberPagerState(
        initialPage = uiState.currentScreen.ordinal,
        pageCount = { AppScreen.entries.size }
    )

    LaunchedEffect(uiState.currentScreen) {
        val targetPage = uiState.currentScreen.ordinal
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            val swipedScreen = AppScreen.entries[pagerState.currentPage]
            if (uiState.currentScreen != swipedScreen) {
                viewModel.navigateTo(swipedScreen)
            }
        }
    }

    BackHandler(enabled = !showSplash && !uiState.showWebSyncDialog && uiState.selectedAccountForSheet == null) {
        showExitDialog = true
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importFromUris(context, uris)
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
        containerColor = palette.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = palette.surfaceElevated,
                    contentColor = palette.textPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        bottomBar = {
            InstallowBottomBar(
                currentScreen = uiState.currentScreen,
                onSelect = viewModel::navigateTo
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (AppScreen.entries[page]) {
                    AppScreen.DASHBOARD -> DashboardScreen(
                        uiState = uiState,
                        onOpenTab = viewModel::openConnectionsWithTab,
                        onNavigateScreen = viewModel::navigateTo,
                        onStartQueue = viewModel::startReviewQueue,
                        onOpenFilePicker = { filePickerLauncher.launch(arrayOf("*/*")) },
                        onSimulateChange = viewModel::simulateSnapshotChange
                    )

                    AppScreen.CONNECTIONS -> ConnectionsScreen(
                        uiState = uiState,
                        onSelectTab = viewModel::selectTab,
                        onSearchChange = viewModel::updateSearchQuery,
                        onSortChange = viewModel::updateSortOption,
                        onToggleHideWhitelisted = viewModel::toggleHideWhitelistedInNonFollowers,
                        onOpenAccountSheet = viewModel::openAccountSheet,
                        onToggleWhitelist = viewModel::toggleWhitelist,
                        onUpdateTagAndNote = viewModel::updateTagAndNote,
                        onMarkUnfollowed = viewModel::markUnfollowedLocally,
                        onStartQueue = viewModel::startReviewQueue,
                        onNextQueue = viewModel::nextInQueue,
                        onPrevQueue = viewModel::previousInQueue,
                        onStopQueue = viewModel::stopQueue,
                        onBuildExportText = viewModel::exportCurrentListText
                    )

                    AppScreen.ANALYTICS -> AnalyticsScreen(
                        uiState = uiState,
                        onOpenTab = viewModel::openConnectionsWithTab,
                        onOpenAccountSheet = { acc ->
                            viewModel.navigateTo(AppScreen.CONNECTIONS)
                            viewModel.openAccountSheet(acc)
                        }
                    )

                    AppScreen.DATA_SYNC -> DataSyncScreen(
                        uiState = uiState,
                        onOpenFilePicker = { filePickerLauncher.launch(arrayOf("*/*")) },
                        onOpenWebSync = { viewModel.setShowWebSyncDialog(true) },
                        onSimulateChange = viewModel::simulateSnapshotChange,
                        onResetDemo = { viewModel.resetToDemoData(silent = false) },
                        onThemeSelect = viewModel::setThemePreference,
                        onBuildExportText = viewModel::exportCurrentListText,
                        onRunSecurityAudit = viewModel::runSecurityAudit,
                        onClearWebCookies = { viewModel.clearWebSessionCookies(context) }
                    )
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = palette.surfaceElevated,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
            title = {
                Text(
                    text = "Uygulamadan çıkılsın mı?",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Installow'dan çıkmak istediğine emin misin?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text(
                        text = "Çık",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(
                        text = "Vazgeç",
                        color = palette.textPrimary
                    )
                }
            }
        )
    }

    if (uiState.showWebSyncDialog) {
        InstagramWebSyncDialog(
            onDismiss = { viewModel.setShowWebSyncDialog(false) },
            onSyncComplete = { owner, followers, following ->
                viewModel.applyWebSyncData(owner, followers, following)
            }
        )
    }

    AnimatedVisibility(
        visible = showSplash,
        enter = EnterTransition.None,
        exit = fadeOut(animationSpec = tween(durationMillis = 350))
    ) {
        InstallowSplashOverlay()
    }
}

@Composable
private fun InstallowSplashOverlay() {
    val palette = LocalInstallowPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Installow",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
                color = palette.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "by kyhlaer",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp,
                color = palette.textSecondary
            )
        }
    }
}

private data class NavTabItem(
    val screen: AppScreen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
private fun InstallowBottomBar(
    currentScreen: AppScreen,
    onSelect: (AppScreen) -> Unit
) {
    val palette = LocalInstallowPalette.current
    val items = listOf(
        NavTabItem(AppScreen.DASHBOARD, Icons.Rounded.GridView, Icons.Outlined.GridView),
        NavTabItem(AppScreen.CONNECTIONS, Icons.Rounded.Group, Icons.Outlined.Group),
        NavTabItem(AppScreen.ANALYTICS, Icons.Rounded.BarChart, Icons.Outlined.BarChart),
        NavTabItem(AppScreen.DATA_SYNC, Icons.Rounded.Tune, Icons.Outlined.Tune)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = palette.border)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentScreen == item.screen
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelect(item.screen) }
                        )
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.screen.labelTr,
                        tint = if (selected) palette.textPrimary else palette.textMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.screen.labelTr,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) palette.textPrimary else palette.textMuted
                    )
                }
            }
        }
    }
}
