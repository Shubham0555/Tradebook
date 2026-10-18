package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
  @Query("SELECT * FROM trades ORDER BY date DESC, time DESC")
  fun getAllTrades(): Flow<List<TradeEntity>>

  @Query("SELECT * FROM trades WHERE date = :date ORDER BY time DESC")
  fun getTradesByDate(date: String): Flow<List<TradeEntity>>

  @Query("SELECT * FROM trades WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, time ASC")
  fun getTradesInRange(startDate: String, endDate: String): Flow<List<TradeEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(trade: TradeEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(trades: List<TradeEntity>)

  @Update
  suspend fun update(trade: TradeEntity)

  @Delete
  suspend fun delete(trade: TradeEntity)

  @Query("DELETE FROM trades WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM trades")
  suspend fun clearAll()
}

@Dao
interface DailyJournalDao {
  @Query("SELECT * FROM daily_journals")
  fun getAllJournals(): Flow<List<DailyJournalEntity>>

  @Query("SELECT * FROM daily_journals WHERE date = :date LIMIT 1")
  fun getJournalByDate(date: String): Flow<DailyJournalEntity?>

  @Query("SELECT * FROM daily_journals WHERE date = :date LIMIT 1")
  suspend fun getJournalByDateDirect(date: String): DailyJournalEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(journal: DailyJournalEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(journals: List<DailyJournalEntity>)

  @Query("DELETE FROM daily_journals WHERE date = :date")
  suspend fun deleteByDate(date: String)

  @Query("DELETE FROM daily_journals")
  suspend fun clearAll()
}
