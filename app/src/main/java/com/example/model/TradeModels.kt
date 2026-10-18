package com.example.model

data class Trade(
  val id: Long = 0,
  val date: String, // Format: YYYY-MM-DD (e.g. "2026-10-18")
  val time: String = "09:30", // Format: HH:mm
  val asset: String = "NQ", // e.g. "NQ", "ES", "BTC", "EURUSD"
  val direction: String = "LONG", // "LONG" or "SHORT"
  val entryPrice: Double = 0.0,
  val exitPrice: Double = 0.0,
  val quantity: Double = 1.0,
  val pnl: Double = 0.0, // In currency units, positive or negative
  val pnlPercent: Double = 0.0,
  val preEmotion: String = "Disciplined", // e.g. "Disciplined", "Calm", "FOMO", "Confident", "Anxious", "Greedy"
  val duringEmotion: String = "Calm", // e.g. "Patient", "Calm", "Anxious", "Panicked", "Excited"
  val postEmotion: String = "Satisfied", // e.g. "Satisfied", "Disciplined", "Regretful", "Relieved", "Angry"
  val disciplineScore: Int = 5, // 1 to 5 stars
  val strategy: String = "Breakout & Retest", // e.g. "Breakout & Retest", "Liquidity Sweep", "Fair Value Gap", "Trend Following"
  val notes: String = "",
  val screenshotUri: String? = null,
  val accountName: String = "Apex 50k",
  val tags: List<String> = emptyList()
)

data class DailyJournal(
  val date: String, // Format: YYYY-MM-DD
  val manualPnl: Double? = null,
  val notes: String = "",
  val strategy: String = "",
  val thoughts: String = "",
  val emotionalState: String = "Disciplined",
  val screenshotUri: String? = null,
  val accountName: String = "All Accounts"
)

data class TradingAccount(
  val id: String,
  val name: String,
  val balance: Double,
  val currency: String = "USD"
)

data class EconomicNewsItem(
  val id: String,
  val title: String,
  val impact: String, // "HIGH", "MEDIUM", "LOW"
  val time: String,
  val currency: String,
  val forecast: String = "",
  val previous: String = ""
)

data class WeekDayPnl(
  val dayLabel: String, // e.g. "Mon 12"
  val date: String, // YYYY-MM-DD
  val pnl: Double,
  val tradeCount: Int,
  val isCurrent: Boolean
)

data class MonthPerformance(
  val monthName: String, // e.g. "Jan", "Feb"
  val monthNum: Int,
  val pnl: Double,
  val tradeCount: Int,
  val hasData: Boolean
)

data class AssetStat(
  val asset: String,
  val tradeCount: Int,
  val winCount: Int,
  val winRate: Float,
  val totalPnl: Double
)

data class EquityPoint(
  val label: String,
  val balance: Double,
  val pnl: Double
)

data class DriveBackupPayload(
  val version: Int = 1,
  val exportedAt: String,
  val userEmail: String,
  val trades: List<Trade>,
  val dailyJournals: List<DailyJournal>
)
