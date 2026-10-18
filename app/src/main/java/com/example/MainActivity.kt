package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Trade
import com.example.ui.TradeJournalViewModel
import com.example.ui.components.DayDetailModal
import com.example.ui.components.GoogleDriveSyncSheet
import com.example.ui.components.TradeEditorSheet
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.TradesScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        TradeJournalApp()
      }
    }
  }
}

enum class ScreenTab {
  DASHBOARD,
  CALENDAR,
  TRADES,
  ANALYTICS
}

@Composable
fun TradeJournalApp(viewModel: TradeJournalViewModel = viewModel()) {
  var currentTab by remember { mutableStateOf(ScreenTab.DASHBOARD) }
  var activeDayModalDate by remember { mutableStateOf<String?>("2026-10-18") } // Pre-open Oct 18 per screenshot 1!
  var isTradeEditorOpen by remember { mutableStateOf(false) }
  var tradeToEdit by remember { mutableStateOf<Trade?>(null) }
  var isDriveSyncOpen by remember { mutableStateOf(false) }

  val driveEmail by viewModel.driveAccountEmail.collectAsState()
  val lastSyncTime by viewModel.lastSyncTime.collectAsState()
  val isAutoSync by viewModel.isAutoSyncEnabled.collectAsState()
  val syncStatusMessage by viewModel.syncStatusMessage.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // SAF Google Drive Export Launcher
  val createDocLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/json")
  ) { uri ->
    if (uri != null) {
      viewModel.exportToGoogleDrive(uri)
    }
  }

  // SAF Google Drive Import Launcher
  val openDocLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri ->
    if (uri != null) {
      viewModel.importFromGoogleDrive(uri)
    }
  }

  // Show status snackbars
  LaunchedEffect(syncStatusMessage) {
    syncStatusMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearStatusMessage()
    }
  }

  BackHandler(enabled = currentTab != ScreenTab.DASHBOARD || activeDayModalDate != null || isTradeEditorOpen || isDriveSyncOpen) {
    when {
      isTradeEditorOpen -> isTradeEditorOpen = false
      activeDayModalDate != null -> activeDayModalDate = null
      isDriveSyncOpen -> isDriveSyncOpen = false
      currentTab != ScreenTab.DASHBOARD -> currentTab = ScreenTab.DASHBOARD
    }
  }

  Scaffold(
    containerColor = BackgroundDark,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .navigationBarsPadding()
          .testTag("main_bottom_nav"),
        containerColor = BackgroundDark,
        tonalElevation = 0.dp
      ) {
        NavigationBarItem(
          selected = currentTab == ScreenTab.DASHBOARD,
          onClick = { currentTab = ScreenTab.DASHBOARD },
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard", modifier = Modifier.size(22.dp)) },
          label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BackgroundDark,
            selectedTextColor = ProfitGreen,
            indicatorColor = ProfitGreen,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted
          )
        )

        NavigationBarItem(
          selected = currentTab == ScreenTab.CALENDAR,
          onClick = { currentTab = ScreenTab.CALENDAR },
          icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", modifier = Modifier.size(22.dp)) },
          label = { Text("Calendar", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BackgroundDark,
            selectedTextColor = ProfitGreen,
            indicatorColor = ProfitGreen,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted
          )
        )

        NavigationBarItem(
          selected = currentTab == ScreenTab.TRADES,
          onClick = { currentTab = ScreenTab.TRADES },
          icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Trades", modifier = Modifier.size(22.dp)) },
          label = { Text("Trades", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BackgroundDark,
            selectedTextColor = ProfitGreen,
            indicatorColor = ProfitGreen,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted
          )
        )

        NavigationBarItem(
          selected = currentTab == ScreenTab.ANALYTICS,
          onClick = { currentTab = ScreenTab.ANALYTICS },
          icon = { Icon(Icons.Default.Insights, contentDescription = "Psychology", modifier = Modifier.size(22.dp)) },
          label = { Text("Psychology", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BackgroundDark,
            selectedTextColor = ProfitGreen,
            indicatorColor = ProfitGreen,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted
          )
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (currentTab) {
        ScreenTab.DASHBOARD -> DashboardScreen(
          viewModel = viewModel,
          onOpenDayModal = { dateStr ->
            viewModel.selectDate(dateStr)
            activeDayModalDate = dateStr
          },
          onOpenTradeEditor = {
            tradeToEdit = null
            isTradeEditorOpen = true
          },
          onOpenDriveSync = { isDriveSyncOpen = true },
          onNavigateToCalendar = { currentTab = ScreenTab.CALENDAR },
          onNavigateToTrades = { currentTab = ScreenTab.TRADES }
        )

        ScreenTab.CALENDAR -> CalendarScreen(
          viewModel = viewModel,
          onOpenDayModal = { dateStr ->
            viewModel.selectDate(dateStr)
            activeDayModalDate = dateStr
          },
          onOpenDriveSync = { isDriveSyncOpen = true },
          onNavigateToDashboard = { currentTab = ScreenTab.DASHBOARD }
        )

        ScreenTab.TRADES -> TradesScreen(
          viewModel = viewModel,
          onBack = { currentTab = ScreenTab.DASHBOARD },
          onOpenTradeEditor = { trade ->
            tradeToEdit = trade
            isTradeEditorOpen = true
          }
        )

        ScreenTab.ANALYTICS -> AnalyticsScreen(
          viewModel = viewModel,
          onBack = { currentTab = ScreenTab.DASHBOARD }
        )
      }

      // Day Detail Modal (Screenshot 1)
      activeDayModalDate?.let { dateStr ->
        val selectedDayJournal by viewModel.selectedDayJournal.collectAsState()
        val selectedDayTrades by viewModel.selectedDayTrades.collectAsState()

        DayDetailModal(
          dateStr = dateStr,
          journal = selectedDayJournal,
          trades = selectedDayTrades,
          onDismiss = { activeDayModalDate = null },
          onSaveJournal = { d, notes, thoughts, screenshot, pnl ->
            viewModel.updateDailyNotesAndScreenshot(d, notes, thoughts, screenshot, pnl)
          },
          onAddTradeClick = {
            tradeToEdit = null
            isTradeEditorOpen = true
          },
          onDeleteTrade = { tradeId ->
            viewModel.deleteTrade(tradeId)
          },
          onUploadScreenshotUri = { uri, onResult ->
            viewModel.saveScreenshotUri(uri, onResult)
          }
        )
      }

      // Trade Editor Sheet
      if (isTradeEditorOpen) {
        val activeDate by viewModel.selectedDate.collectAsState()
        TradeEditorSheet(
          initialDate = activeDate,
          existingTrade = tradeToEdit,
          onDismiss = { isTradeEditorOpen = false },
          onSave = { trade ->
            viewModel.saveTrade(trade)
          },
          onUploadScreenshot = { uri, onResult ->
            viewModel.saveScreenshotUri(uri, onResult)
          }
        )
      }

      // Google Drive Cloud Sync Sheet
      if (isDriveSyncOpen) {
        GoogleDriveSyncSheet(
          accountEmail = driveEmail,
          lastSyncTime = lastSyncTime,
          isAutoSyncEnabled = isAutoSync,
          statusMessage = syncStatusMessage,
          onAutoSyncToggle = { viewModel.isAutoSyncEnabled.value = it },
          onExportToDriveClick = {
            val defaultFileName = "tradepulse_backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())}.json"
            createDocLauncher.launch(defaultFileName)
          },
          onImportFromDriveClick = {
            openDocLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
          },
          onDismiss = { isDriveSyncOpen = false }
        )
      }
    }
  }
}
