package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.DailyJournal
import com.example.model.Trade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class TradeRepository(
  private val context: Context,
  private val tradeDao: TradeDao,
  private val journalDao: DailyJournalDao,
  val driveBackupManager: GoogleDriveBackupManager
) {

  val allTrades: Flow<List<Trade>> = tradeDao.getAllTrades().map { list ->
    list.map { it.toModel() }
  }

  val allDailyJournals: Flow<List<DailyJournal>> = journalDao.getAllJournals().map { list ->
    list.map { it.toModel() }
  }

  fun getTradesForDate(date: String): Flow<List<Trade>> =
    tradeDao.getTradesByDate(date).map { list -> list.map { it.toModel() } }

  fun getJournalForDate(date: String): Flow<DailyJournal?> =
    journalDao.getJournalByDate(date).map { it?.toModel() }

  suspend fun insertTrade(trade: Trade): Long = withContext(Dispatchers.IO) {
    tradeDao.insert(TradeEntity.fromModel(trade))
  }

  suspend fun updateTrade(trade: Trade) = withContext(Dispatchers.IO) {
    tradeDao.update(TradeEntity.fromModel(trade))
  }

  suspend fun deleteTrade(id: Long) = withContext(Dispatchers.IO) {
    tradeDao.deleteById(id)
  }

  suspend fun saveDailyJournal(journal: DailyJournal) = withContext(Dispatchers.IO) {
    journalDao.insertOrUpdate(DailyJournalEntity.fromModel(journal))
  }

  suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
    val existing = tradeDao.getAllTrades().first()
    if (existing.isEmpty()) {
      tradeDao.insertAll(SampleData.sampleTrades.map { TradeEntity.fromModel(it) })
      journalDao.insertAll(SampleData.sampleDailyJournals.map { DailyJournalEntity.fromModel(it) })
    }
  }

  suspend fun restoreFromPayload(trades: List<Trade>, journals: List<DailyJournal>, clearExisting: Boolean = false) = withContext(Dispatchers.IO) {
    if (clearExisting) {
      tradeDao.clearAll()
      journalDao.clearAll()
    }
    tradeDao.insertAll(trades.map { TradeEntity.fromModel(it) })
    journalDao.insertAll(journals.map { DailyJournalEntity.fromModel(it) })
  }

  suspend fun copyUriToInternalStorage(sourceUri: Uri): String? = withContext(Dispatchers.IO) {
    try {
      val dir = File(context.filesDir, "trade_screenshots")
      if (!dir.exists()) dir.mkdirs()
      val filename = "screenshot_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
      val destFile = File(dir, filename)

      context.contentResolver.openInputStream(sourceUri)?.use { input ->
        FileOutputStream(destFile).use { output ->
          input.copyTo(output)
        }
      }
      destFile.absolutePath
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}
