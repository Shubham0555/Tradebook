package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.DailyJournal
import com.example.model.DriveBackupPayload
import com.example.model.Trade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoogleDriveBackupManager(private val context: Context) {

  private val prefs = context.getSharedPreferences("google_drive_sync_prefs", Context.MODE_PRIVATE)

  fun getLastSyncTime(): String {
    return prefs.getString("last_sync_time", "Not synced yet") ?: "Not synced yet"
  }

  fun setLastSyncTime(time: String) {
    prefs.edit().putString("last_sync_time", time).apply()
  }

  fun isAutoSyncEnabled(): Boolean {
    return prefs.getBoolean("auto_drive_sync", true)
  }

  fun setAutoSyncEnabled(enabled: Boolean) {
    prefs.edit().putBoolean("auto_drive_sync", enabled).apply()
  }

  fun getUserEmail(): String {
    return prefs.getString("drive_account_email", "shubhamsoni2307@gmail.com") ?: "shubhamsoni2307@gmail.com"
  }

  fun setUserEmail(email: String) {
    prefs.edit().putString("drive_account_email", email).apply()
  }

  suspend fun exportToDriveUri(uri: Uri, trades: List<Trade>, journals: List<DailyJournal>): Result<String> {
    return withContext(Dispatchers.IO) {
      try {
        val root = JSONObject()
        root.put("version", 1)
        val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        root.put("exportedAt", nowStr)
        root.put("userEmail", getUserEmail())

        // Trades array
        val tradesArray = JSONArray()
        for (t in trades) {
          val obj = JSONObject()
          obj.put("id", t.id)
          obj.put("date", t.date)
          obj.put("time", t.time)
          obj.put("asset", t.asset)
          obj.put("direction", t.direction)
          obj.put("entryPrice", t.entryPrice)
          obj.put("exitPrice", t.exitPrice)
          obj.put("quantity", t.quantity)
          obj.put("pnl", t.pnl)
          obj.put("pnlPercent", t.pnlPercent)
          obj.put("preEmotion", t.preEmotion)
          obj.put("duringEmotion", t.duringEmotion)
          obj.put("postEmotion", t.postEmotion)
          obj.put("disciplineScore", t.disciplineScore)
          obj.put("strategy", t.strategy)
          obj.put("notes", t.notes)
          obj.put("screenshotUri", t.screenshotUri ?: "")
          obj.put("accountName", t.accountName)
          tradesArray.put(obj)
        }
        root.put("trades", tradesArray)

        // Journals array
        val journalsArray = JSONArray()
        for (j in journals) {
          val obj = JSONObject()
          obj.put("date", j.date)
          if (j.manualPnl != null) {
            obj.put("manualPnl", j.manualPnl)
          }
          obj.put("notes", j.notes)
          obj.put("strategy", j.strategy)
          obj.put("thoughts", j.thoughts)
          obj.put("emotionalState", j.emotionalState)
          obj.put("screenshotUri", j.screenshotUri ?: "")
          obj.put("accountName", j.accountName)
          journalsArray.put(obj)
        }
        root.put("dailyJournals", journalsArray)

        context.contentResolver.openOutputStream(uri)?.use { os ->
          OutputStreamWriter(os).use { writer ->
            writer.write(root.toString(2))
          }
        }
        val syncTime = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
        setLastSyncTime(syncTime)
        Result.success("Successfully exported ${trades.size} trades to Google Drive at $syncTime")
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun importFromDriveUri(uri: Uri): Result<DriveBackupPayload> {
    return withContext(Dispatchers.IO) {
      try {
        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
          BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line = reader.readLine()
            while (line != null) {
              stringBuilder.append(line)
              line = reader.readLine()
            }
          }
        }

        val json = JSONObject(stringBuilder.toString())
        val version = json.optInt("version", 1)
        val exportedAt = json.optString("exportedAt", "")
        val userEmail = json.optString("userEmail", getUserEmail())

        val tradesList = mutableListOf<Trade>()
        val tradesArray = json.optJSONArray("trades") ?: JSONArray()
        for (i in 0 until tradesArray.length()) {
          val obj = tradesArray.getJSONObject(i)
          tradesList.add(
            Trade(
              id = obj.optLong("id", 0L),
              date = obj.optString("date", ""),
              time = obj.optString("time", "09:30"),
              asset = obj.optString("asset", "NQ"),
              direction = obj.optString("direction", "LONG"),
              entryPrice = obj.optDouble("entryPrice", 0.0),
              exitPrice = obj.optDouble("exitPrice", 0.0),
              quantity = obj.optDouble("quantity", 1.0),
              pnl = obj.optDouble("pnl", 0.0),
              pnlPercent = obj.optDouble("pnlPercent", 0.0),
              preEmotion = obj.optString("preEmotion", "Disciplined"),
              duringEmotion = obj.optString("duringEmotion", "Calm"),
              postEmotion = obj.optString("postEmotion", "Satisfied"),
              disciplineScore = obj.optInt("disciplineScore", 5),
              strategy = obj.optString("strategy", "Breakout & Retest"),
              notes = obj.optString("notes", ""),
              screenshotUri = obj.optString("screenshotUri").takeIf { it.isNotBlank() },
              accountName = obj.optString("accountName", "Apex 50k")
            )
          )
        }

        val journalsList = mutableListOf<DailyJournal>()
        val journalsArray = json.optJSONArray("dailyJournals") ?: JSONArray()
        for (i in 0 until journalsArray.length()) {
          val obj = journalsArray.getJSONObject(i)
          val manualPnl = if (obj.has("manualPnl")) obj.getDouble("manualPnl") else null
          journalsList.add(
            DailyJournal(
              date = obj.optString("date", ""),
              manualPnl = manualPnl,
              notes = obj.optString("notes", ""),
              strategy = obj.optString("strategy", ""),
              thoughts = obj.optString("thoughts", ""),
              emotionalState = obj.optString("emotionalState", "Disciplined"),
              screenshotUri = obj.optString("screenshotUri").takeIf { it.isNotBlank() },
              accountName = obj.optString("accountName", "All Accounts")
            )
          )
        }

        val syncTime = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
        setLastSyncTime(syncTime)

        Result.success(
          DriveBackupPayload(
            version = version,
            exportedAt = exportedAt,
            userEmail = userEmail,
            trades = tradesList,
            dailyJournals = journalsList
          )
        )
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }
}
