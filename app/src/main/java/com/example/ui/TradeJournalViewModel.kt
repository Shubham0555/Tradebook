package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GoogleDriveBackupManager
import com.example.data.SampleData
import com.example.data.TradeDatabase
import com.example.data.TradeRepository
import com.example.model.AssetStat
import com.example.model.DailyJournal
import com.example.model.EconomicNewsItem
import com.example.model.EquityPoint
import com.example.model.MonthPerformance
import com.example.model.Trade
import com.example.model.WeekDayPnl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TradeJournalViewModel(application: Application) : AndroidViewModel(application) {

  private val database = TradeDatabase.getDatabase(application)
  private val driveManager = GoogleDriveBackupManager(application)
  val repository = TradeRepository(application, database.tradeDao(), database.dailyJournalDao(), driveManager)

  // Filters
  val selectedAccount = MutableStateFlow("All Accounts")
  val selectedTimeframe = MutableStateFlow("Current Week") // "Current Week", "Month", "Year", "All"

  // Calendar navigation: Default to October 2026 per screenshot
  val calendarYear = MutableStateFlow(2026)
  val calendarMonth = MutableStateFlow(10) // 1-indexed (10 = October)
  val selectedDate = MutableStateFlow("2026-10-18") // YYYY-MM-DD

  // Google Drive state
  val driveAccountEmail = MutableStateFlow(driveManager.getUserEmail())
  val lastSyncTime = MutableStateFlow(driveManager.getLastSyncTime())
  val isAutoSyncEnabled = MutableStateFlow(driveManager.isAutoSyncEnabled())
  val syncStatusMessage = MutableStateFlow<String?>(null)

  // Economic News
  val newsList = MutableStateFlow<List<EconomicNewsItem>>(SampleData.sampleEconomicNews)

  init {
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  val allTrades: StateFlow<List<Trade>> = repository.allTrades
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allJournals: StateFlow<List<DailyJournal>> = repository.allDailyJournals
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Filtered trades by account
  val filteredTrades: StateFlow<List<Trade>> = combine(allTrades, selectedAccount) { trades, account ->
    if (account == "All Accounts") trades else trades.filter { it.accountName == account }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected Day Trades
  val selectedDayTrades: StateFlow<List<Trade>> = combine(filteredTrades, selectedDate) { trades, date ->
    trades.filter { it.date == date }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected Day Journal
  val selectedDayJournal: StateFlow<DailyJournal?> = combine(allJournals, selectedDate) { journals, date ->
    journals.find { it.date == date }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Aggregated Stats
  val totalTradesCount = combine(filteredTrades) { it.first().size }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val winningTradesCount = combine(filteredTrades) { trades ->
    trades.first().count { it.pnl > 0 }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val breakevenTradesCount = combine(filteredTrades) { trades ->
    trades.first().count { it.pnl == 0.0 }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val losingTradesCount = combine(filteredTrades) { trades ->
    trades.first().count { it.pnl < 0 }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val winRate = combine(filteredTrades) { trades ->
    val list = trades.first()
    if (list.isEmpty()) 0f else (list.count { it.pnl > 0 }.toFloat() / list.size.toFloat()) * 100f
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

  val totalProfit = combine(filteredTrades) { trades ->
    trades.first().filter { it.pnl > 0 }.sumOf { it.pnl }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val totalLoss = combine(filteredTrades) { trades ->
    Math.abs(trades.first().filter { it.pnl < 0 }.sumOf { it.pnl })
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val profitFactor = combine(totalProfit, totalLoss) { profit, loss ->
    if (loss == 0.0) {
      if (profit > 0) 99.0 else 1.0
    } else {
      profit / loss
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0)

  val netPnl = combine(filteredTrades) { trades ->
    trades.first().sumOf { it.pnl }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val initialAccountBalance = MutableStateFlow(50000.0) // E.g. $50k funded prop account

  val currentAccountBalance = combine(initialAccountBalance, netPnl) { initial, pnl ->
    initial + pnl
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50000.0)

  // Most Traded Assets
  val mostTradedAssets: StateFlow<List<AssetStat>> = combine(filteredTrades) { trades ->
    val list = trades.first()
    val grouped = list.groupBy { it.asset }
    grouped.map { (asset, assetTrades) ->
      val count = assetTrades.size
      val wins = assetTrades.count { it.pnl > 0 }
      val winRate = if (count > 0) (wins.toFloat() / count.toFloat()) * 100f else 0f
      val pnl = assetTrades.sumOf { it.pnl }
      AssetStat(asset, count, wins, winRate, pnl)
    }.sortedByDescending { it.tradeCount }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Weekly Strip (Mon 12 to Sun 18 Oct 2026 as per screenshot)
  val weeklyPnlStrip: StateFlow<List<WeekDayPnl>> = combine(filteredTrades, selectedDate) { trades, activeDate ->
    val result = mutableListOf<WeekDayPnl>()
    // Target week of Oct 12 to Oct 18, 2026
    val days = listOf(
      "Mon 12" to "2026-10-12",
      "Tue 13" to "2026-10-13",
      "Wed 14" to "2026-10-14",
      "Thu 15" to "2026-10-15",
      "Fri 16" to "2026-10-16",
      "Sat 17" to "2026-10-17",
      "Sun 18" to "2026-10-18"
    )
    for ((label, dateStr) in days) {
      val dayTrades = trades.filter { it.date == dateStr }
      val pnl = dayTrades.sumOf { it.pnl }
      result.add(
        WeekDayPnl(
          dayLabel = label,
          date = dateStr,
          pnl = pnl,
          tradeCount = dayTrades.size,
          isCurrent = dateStr == activeDate
        )
      )
    }
    result
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Equity Curve Points for selected timeframe
  val equityPoints: StateFlow<List<EquityPoint>> = combine(filteredTrades, initialAccountBalance) { trades, baseBalance ->
    val sorted = trades.sortedBy { "${it.date} ${it.time}" }
    val points = mutableListOf<EquityPoint>()
    points.add(EquityPoint("Start", baseBalance, 0.0))
    var cumPnl = 0.0
    for (t in sorted) {
      cumPnl += t.pnl
      val shortDate = if (t.date.length >= 10) t.date.substring(5) else t.date
      points.add(EquityPoint(shortDate, baseBalance + cumPnl, cumPnl))
    }
    if (points.size == 1) {
      points.add(EquityPoint("Oct 18", baseBalance, 0.0))
    }
    points
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Yearly monthly performance matrix (Jan - Dec + YTD) matching Screenshot 2
  val yearlyPerformance: StateFlow<List<MonthPerformance>> = combine(filteredTrades, calendarYear) { trades, year ->
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    months.mapIndexed { idx, name ->
      val monthNum = idx + 1
      val monthPrefix = String.format(Locale.US, "%04d-%02d", year, monthNum)
      val monthTrades = trades.filter { it.date.startsWith(monthPrefix) }
      val pnl = monthTrades.sumOf { it.pnl }
      MonthPerformance(
        monthName = name,
        monthNum = monthNum,
        pnl = pnl,
        tradeCount = monthTrades.size,
        hasData = monthTrades.isNotEmpty()
      )
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Daily map of P&L and Trade count for calendar rendering
  val calendarDateStats: StateFlow<Map<String, Pair<Double, Int>>> = combine(filteredTrades, allJournals) { trades, journals ->
    val map = mutableMapOf<String, Pair<Double, Int>>()
    // Group trades by date
    val tradeGroups = trades.groupBy { it.date }
    for ((d, list) in tradeGroups) {
      map[d] = Pair(list.sumOf { it.pnl }, list.size)
    }
    // Check journals for manual PnL override or entries without trades
    for (j in journals) {
      if (j.manualPnl != null && !map.containsKey(j.date)) {
        map[j.date] = Pair(j.manualPnl, 0)
      }
    }
    map
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  fun selectDate(date: String) {
    selectedDate.value = date
  }

  fun changeCalendarMonth(delta: Int) {
    var newMonth = calendarMonth.value + delta
    var newYear = calendarYear.value
    if (newMonth > 12) {
      newMonth = 1
      newYear += 1
    } else if (newMonth < 1) {
      newMonth = 12
      newYear -= 1
    }
    calendarMonth.value = newMonth
    calendarYear.value = newYear
  }

  fun setCalendarMonth(month: Int) {
    calendarMonth.value = month.coerceIn(1, 12)
  }

  fun saveTrade(trade: Trade) {
    viewModelScope.launch {
      if (trade.id == 0L) {
        repository.insertTrade(trade)
      } else {
        repository.updateTrade(trade)
      }
    }
  }

  fun deleteTrade(id: Long) {
    viewModelScope.launch {
      repository.deleteTrade(id)
    }
  }

  fun saveDailyJournal(journal: DailyJournal) {
    viewModelScope.launch {
      repository.saveDailyJournal(journal)
    }
  }

  fun updateDailyNotesAndScreenshot(date: String, notes: String, thoughts: String, screenshotUri: String?, manualPnl: Double?) {
    viewModelScope.launch {
      val existing = selectedDayJournal.value ?: DailyJournal(date = date)
      val updated = existing.copy(
        date = date,
        notes = notes,
        thoughts = thoughts,
        screenshotUri = screenshotUri ?: existing.screenshotUri,
        manualPnl = manualPnl ?: existing.manualPnl
      )
      repository.saveDailyJournal(updated)
    }
  }

  fun saveScreenshotUri(sourceUri: Uri, onResult: (String?) -> Unit) {
    viewModelScope.launch {
      val path = repository.copyUriToInternalStorage(sourceUri)
      onResult(path)
    }
  }

  fun exportToGoogleDrive(uri: Uri) {
    viewModelScope.launch {
      val result = repository.driveBackupManager.exportToDriveUri(uri, allTrades.value, allJournals.value)
      if (result.isSuccess) {
        lastSyncTime.value = driveManager.getLastSyncTime()
        syncStatusMessage.value = "Google Drive Backup created successfully!"
      } else {
        syncStatusMessage.value = "Backup failed: ${result.exceptionOrNull()?.message}"
      }
    }
  }

  fun importFromGoogleDrive(uri: Uri) {
    viewModelScope.launch {
      val result = repository.driveBackupManager.importFromDriveUri(uri)
      if (result.isSuccess) {
        val payload = result.getOrNull()
        if (payload != null) {
          repository.restoreFromPayload(payload.trades, payload.dailyJournals)
          lastSyncTime.value = driveManager.getLastSyncTime()
          driveAccountEmail.value = payload.userEmail
          syncStatusMessage.value = "Restored ${payload.trades.size} trades from Google Drive!"
        }
      } else {
        syncStatusMessage.value = "Restore failed: ${result.exceptionOrNull()?.message}"
      }
    }
  }

  fun clearStatusMessage() {
    syncStatusMessage.value = null
  }
}
