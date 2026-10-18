package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DailyJournal
import com.example.model.Trade

@Entity(tableName = "trades")
data class TradeEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val date: String,
  val time: String,
  val asset: String,
  val direction: String,
  val entryPrice: Double,
  val exitPrice: Double,
  val quantity: Double,
  val pnl: Double,
  val pnlPercent: Double,
  val preEmotion: String,
  val duringEmotion: String,
  val postEmotion: String,
  val disciplineScore: Int,
  val strategy: String,
  val notes: String,
  val screenshotUri: String?,
  val accountName: String
) {
  fun toModel(): Trade = Trade(
    id = id,
    date = date,
    time = time,
    asset = asset,
    direction = direction,
    entryPrice = entryPrice,
    exitPrice = exitPrice,
    quantity = quantity,
    pnl = pnl,
    pnlPercent = pnlPercent,
    preEmotion = preEmotion,
    duringEmotion = duringEmotion,
    postEmotion = postEmotion,
    disciplineScore = disciplineScore,
    strategy = strategy,
    notes = notes,
    screenshotUri = screenshotUri,
    accountName = accountName
  )

  companion object {
    fun fromModel(model: Trade): TradeEntity = TradeEntity(
      id = model.id,
      date = model.date,
      time = model.time,
      asset = model.asset,
      direction = model.direction,
      entryPrice = model.entryPrice,
      exitPrice = model.exitPrice,
      quantity = model.quantity,
      pnl = model.pnl,
      pnlPercent = model.pnlPercent,
      preEmotion = model.preEmotion,
      duringEmotion = model.duringEmotion,
      postEmotion = model.postEmotion,
      disciplineScore = model.disciplineScore,
      strategy = model.strategy,
      notes = model.notes,
      screenshotUri = model.screenshotUri,
      accountName = model.accountName
    )
  }
}

@Entity(tableName = "daily_journals")
data class DailyJournalEntity(
  @PrimaryKey
  val date: String,
  val manualPnl: Double?,
  val notes: String,
  val strategy: String,
  val thoughts: String,
  val emotionalState: String,
  val screenshotUri: String?,
  val accountName: String
) {
  fun toModel(): DailyJournal = DailyJournal(
    date = date,
    manualPnl = manualPnl,
    notes = notes,
    strategy = strategy,
    thoughts = thoughts,
    emotionalState = emotionalState,
    screenshotUri = screenshotUri,
    accountName = accountName
  )

  companion object {
    fun fromModel(model: DailyJournal): DailyJournalEntity = DailyJournalEntity(
      date = model.date,
      manualPnl = model.manualPnl,
      notes = model.notes,
      strategy = model.strategy,
      thoughts = model.thoughts,
      emotionalState = model.emotionalState,
      screenshotUri = model.screenshotUri,
      accountName = model.accountName
    )
  }
}
