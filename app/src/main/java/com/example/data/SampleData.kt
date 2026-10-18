package com.example.data

import com.example.model.DailyJournal
import com.example.model.EconomicNewsItem
import com.example.model.Trade

object SampleData {

  val sampleEconomicNews = listOf(
    EconomicNewsItem(
      id = "1",
      title = "CPI m/m (Consumer Price Index)",
      impact = "HIGH",
      time = "08:30 AM",
      currency = "USD",
      forecast = "0.3%",
      previous = "0.2%"
    ),
    EconomicNewsItem(
      id = "2",
      title = "FOMC Meeting Minutes & Fed Speech",
      impact = "HIGH",
      time = "02:00 PM",
      currency = "USD",
      forecast = "5.25%",
      previous = "5.25%"
    ),
    EconomicNewsItem(
      id = "3",
      title = "Initial Jobless Claims",
      impact = "MEDIUM",
      time = "08:30 AM",
      currency = "USD",
      forecast = "215K",
      previous = "218K"
    ),
    EconomicNewsItem(
      id = "4",
      title = "Core Retail Sales m/m",
      impact = "HIGH",
      time = "08:30 AM",
      currency = "USD",
      forecast = "0.4%",
      previous = "0.1%"
    )
  )

  val sampleTrades = listOf(
    Trade(
      id = 1,
      date = "2026-10-12",
      time = "09:42",
      asset = "NQ",
      direction = "LONG",
      entryPrice = 20340.50,
      exitPrice = 20412.00,
      quantity = 2.0,
      pnl = 286.00,
      pnlPercent = 1.4,
      preEmotion = "Disciplined",
      duringEmotion = "Patient",
      postEmotion = "Satisfied",
      disciplineScore = 5,
      strategy = "Liquidity Sweep",
      notes = "Waited for 15m liquidity sweep of Asian session low. Clean displacement upward.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 2,
      date = "2026-10-13",
      time = "10:15",
      asset = "ES",
      direction = "SHORT",
      entryPrice = 5845.25,
      exitPrice = 5832.50,
      quantity = 1.0,
      pnl = 637.50,
      pnlPercent = 2.1,
      preEmotion = "Calm",
      duringEmotion = "Calm",
      postEmotion = "Disciplined",
      disciplineScore = 5,
      strategy = "Fair Value Gap",
      notes = "Entered on 5m FVG retest after London high rejection. Strict target at PDL.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 3,
      date = "2026-10-14",
      time = "11:05",
      asset = "NQ",
      direction = "LONG",
      entryPrice = 20480.00,
      exitPrice = 20462.00,
      quantity = 1.0,
      pnl = -360.00,
      pnlPercent = -0.9,
      preEmotion = "FOMO",
      duringEmotion = "Anxious",
      postEmotion = "Regretful",
      disciplineScore = 2,
      strategy = "Breakout & Retest",
      notes = "Chased green candle into lunchtime chop. Didn't wait for pullback. Cut loss according to plan.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 4,
      date = "2026-10-15",
      time = "09:50",
      asset = "BTC",
      direction = "LONG",
      entryPrice = 68420.00,
      exitPrice = 69280.00,
      quantity = 0.5,
      pnl = 430.00,
      pnlPercent = 1.25,
      preEmotion = "Confident",
      duringEmotion = "Patient",
      postEmotion = "Satisfied",
      disciplineScore = 4,
      strategy = "Trend Following",
      notes = "Higher highs on 4H. Took 15m pullback with trailing stop.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 5,
      date = "2026-10-16",
      time = "14:10",
      asset = "NQ",
      direction = "SHORT",
      entryPrice = 20560.00,
      exitPrice = 20490.00,
      quantity = 2.0,
      pnl = 700.00,
      pnlPercent = 2.4,
      preEmotion = "Disciplined",
      duringEmotion = "Calm",
      postEmotion = "Satisfied",
      disciplineScore = 5,
      strategy = "VWAP Bounce",
      notes = "Afternoon trend continuation rejection off VWAP. Clean runner hit TP2.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 6,
      date = "2026-10-02",
      time = "10:00",
      asset = "ES",
      direction = "LONG",
      entryPrice = 5790.00,
      exitPrice = 5815.00,
      quantity = 1.0,
      pnl = 1250.00,
      pnlPercent = 3.5,
      preEmotion = "Disciplined",
      duringEmotion = "Patient",
      postEmotion = "Satisfied",
      disciplineScore = 5,
      strategy = "Liquidity Sweep",
      notes = "NFP morning reaction settled down. Took the second leg higher.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 7,
      date = "2026-10-06",
      time = "09:35",
      asset = "NQ",
      direction = "SHORT",
      entryPrice = 20210.00,
      exitPrice = 20235.00,
      quantity = 1.0,
      pnl = -500.00,
      pnlPercent = -1.2,
      preEmotion = "Hesitant",
      duringEmotion = "Anxious",
      postEmotion = "Calm",
      disciplineScore = 3,
      strategy = "Breakout & Retest",
      notes = "Fake breakdown. Quickly stopped out.",
      accountName = "Apex 50k"
    ),
    Trade(
      id = 8,
      date = "2026-10-09",
      time = "13:30",
      asset = "EURUSD",
      direction = "SHORT",
      entryPrice = 1.0920,
      exitPrice = 1.0885,
      quantity = 2.0,
      pnl = 700.00,
      pnlPercent = 1.8,
      preEmotion = "Disciplined",
      duringEmotion = "Patient",
      postEmotion = "Satisfied",
      disciplineScore = 5,
      strategy = "Fair Value Gap",
      notes = "Dollar strength trend. Clean 35 pips move.",
      accountName = "Apex 50k"
    )
  )

  val sampleDailyJournals = listOf(
    DailyJournal(
      date = "2026-10-12",
      notes = "Solid start to the week. Followed trade plan and waited 15 mins after open.",
      strategy = "Liquidity Sweep Lows",
      thoughts = "Patience pays off. No rushing.",
      emotionalState = "Disciplined",
      accountName = "Apex 50k"
    ),
    DailyJournal(
      date = "2026-10-13",
      notes = "ES short execution was textbook. Took profits at key support level.",
      strategy = "FVG Retest",
      thoughts = "Very relaxed mind, got good sleep.",
      emotionalState = "Calm",
      accountName = "Apex 50k"
    ),
    DailyJournal(
      date = "2026-10-14",
      notes = "Felt FOMO after missing the morning move and entered late. Good thing I honored my stop loss.",
      strategy = "Chasing Breakout (Mistake)",
      thoughts = "Need to step away from screen between 11:30 and 13:00.",
      emotionalState = "FOMO / Anxious",
      accountName = "Apex 50k"
    ),
    DailyJournal(
      date = "2026-10-15",
      notes = "Recovered discipline. Kept position sizing conservative.",
      strategy = "Trend Following Pullback",
      thoughts = "Rules respected.",
      emotionalState = "Disciplined",
      accountName = "Apex 50k"
    ),
    DailyJournal(
      date = "2026-10-16",
      notes = "Great Friday close. Up +$700 on NQ VWAP rejection.",
      strategy = "VWAP Bounce",
      thoughts = "Closing terminal for the weekend with peace of mind.",
      emotionalState = "Satisfied",
      accountName = "Apex 50k"
    ),
    DailyJournal(
      date = "2026-10-18",
      notes = "Sunday prep day. Reviewed week charts and marked key levels for Monday.",
      strategy = "Weekly Preparation & Risk Management",
      thoughts = "Goal for next week: Max 2 trades per day. Stick to A+ setups only.",
      emotionalState = "Disciplined",
      accountName = "Apex 50k"
    )
  )
}
