package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DraftViewModel
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.HeaderStats
import com.example.ui.components.SaveSquadDialog
import com.example.ui.screens.PersonalCabinetScreen
import com.example.ui.screens.RaceSettingsScreen
import com.example.ui.screens.RidersDatabaseScreen
import com.example.ui.screens.RosterScreen
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.CyclingFantasyTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyclingFantasyTheme {
                CyclingFantasyApp()
            }
        }
    }
}

@Composable
fun CyclingFantasyApp(
    viewModel: DraftViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.setMessage(null)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = BrandDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HeaderStats(uiState = uiState)
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = BrandCard,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Ростер"
                        )
                    },
                    label = {
                        Text(
                            text = "Ростер",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandDark,
                        selectedTextColor = BrandYellow,
                        indicatorColor = BrandYellow,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_roster")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "Гонщики"
                        )
                    },
                    label = {
                        Text(
                            text = "Гонщики",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandDark,
                        selectedTextColor = BrandCyan,
                        indicatorColor = BrandCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_riders")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Гонка"
                        )
                    },
                    label = {
                        Text(
                            text = "Гонка",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandDark,
                        selectedTextColor = BrandYellow,
                        indicatorColor = BrandYellow,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_race")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Кабинет"
                        )
                    },
                    label = {
                        Text(
                            text = "Кабинет",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandDark,
                        selectedTextColor = BrandCyan,
                        indicatorColor = BrandCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_cabinet")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                0 -> RosterScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateToRiders = { viewModel.selectTab(1) }
                )
                1 -> RidersDatabaseScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
                2 -> RaceSettingsScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
                3 -> PersonalCabinetScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Modals
    if (uiState.showExportDialog) {
        ExportReportDialog(
            reportText = viewModel.generateAnalyticalReport(),
            onDismiss = { viewModel.openExportDialog(false) }
        )
    }

    if (uiState.showSaveSquadDialog) {
        SaveSquadDialog(
            defaultTitle = "Состав: ${uiState.currentRace.name}",
            onSave = { title, notes ->
                viewModel.saveCurrentSquad(title, notes)
            },
            onDismiss = { viewModel.openSaveDialog(false) }
        )
    }
}

// Fallback Composable for tests
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CyclingFantasyTheme { Greeting("Android") }
}
