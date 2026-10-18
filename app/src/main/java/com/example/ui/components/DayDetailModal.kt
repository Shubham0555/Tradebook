package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.DailyJournal
import com.example.model.Trade
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LossRed
import com.example.ui.theme.LossRedBg
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DayDetailModal(
  dateStr: String, // e.g. "2026-10-18"
  journal: DailyJournal?,
  trades: List<Trade>,
  onDismiss: () -> Unit,
  onSaveJournal: (String, String, String, String?, Double?) -> Unit,
  onAddTradeClick: () -> Unit,
  onDeleteTrade: (Long) -> Unit,
  onUploadScreenshotUri: (Uri, (String?) -> Unit) -> Unit
) {
  var notesText by remember(journal) { mutableStateOf(journal?.notes ?: "") }
  var thoughtsText by remember(journal) { mutableStateOf(journal?.thoughts ?: "") }
  var strategyText by remember(journal) { mutableStateOf(journal?.strategy ?: "") }
  var currentScreenshotPath by remember(journal) { mutableStateOf(journal?.screenshotUri) }
  var isEditingNotes by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onUploadScreenshotUri(uri) { savedPath ->
        currentScreenshotPath = savedPath
        onSaveJournal(dateStr, notesText, thoughtsText, savedPath, journal?.manualPnl)
      }
    }
  }

  // Format header date (e.g. "Oct 18, 2026")
  val formattedHeaderDate = remember(dateStr) {
    try {
      val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      val date = parser.parse(dateStr)
      if (date != null) {
        SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
      } else dateStr
    } catch (e: Exception) {
      dateStr
    }
  }

  val totalPnl = trades.sumOf { it.pnl }
  val pnlPercent = if (trades.isNotEmpty()) {
    trades.map { it.pnlPercent }.average()
  } else 0.0

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
      color = BackgroundDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Modal Header matching Screenshot 1
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = formattedHeaderDate,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "View your trades and statistics for this day.",
              fontSize = 13.sp,
              color = TextMuted
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Day P&L Badge
            val pnlBadgeBg = if (totalPnl > 0) ProfitGreenBg else if (totalPnl < 0) LossRedBg else SurfaceVariantDark
            val pnlBadgeColor = if (totalPnl > 0) ProfitGreen else if (totalPnl < 0) LossRed else TextSecondary
            val sign = if (totalPnl > 0) "+" else ""

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(pnlBadgeBg)
                .border(1.dp, pnlBadgeColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "$sign$${String.format(Locale.US, "%.0f", totalPnl)} (${String.format(Locale.US, "%.1f", pnlPercent)}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = pnlBadgeColor
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(36.dp)
                .testTag("close_day_modal_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          // Notes and Screenshot Panels (side by side or stacked based on width)
          item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
              val isWide = maxWidth > 560.dp

              if (isWide) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                  NotesCard(
                    modifier = Modifier.weight(1f),
                    notesText = notesText,
                    isEditing = isEditingNotes,
                    onEditClick = { isEditingNotes = !isEditingNotes },
                    onNotesChange = {
                      notesText = it
                      onSaveJournal(dateStr, it, thoughtsText, currentScreenshotPath, journal?.manualPnl)
                    }
                  )

                  ScreenshotCard(
                    modifier = Modifier.weight(1f),
                    screenshotPath = currentScreenshotPath,
                    onUploadClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    onRemoveScreenshot = {
                      currentScreenshotPath = null
                      onSaveJournal(dateStr, notesText, thoughtsText, null, journal?.manualPnl)
                    }
                  )
                }
              } else {
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                  NotesCard(
                    modifier = Modifier.fillMaxWidth(),
                    notesText = notesText,
                    isEditing = isEditingNotes,
                    onEditClick = { isEditingNotes = !isEditingNotes },
                    onNotesChange = {
                      notesText = it
                      onSaveJournal(dateStr, it, thoughtsText, currentScreenshotPath, journal?.manualPnl)
                    }
                  )

                  ScreenshotCard(
                    modifier = Modifier.fillMaxWidth(),
                    screenshotPath = currentScreenshotPath,
                    onUploadClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    onRemoveScreenshot = {
                      currentScreenshotPath = null
                      onSaveJournal(dateStr, notesText, thoughtsText, null, journal?.manualPnl)
                    }
                  )
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(20.dp))

            // Strategy and Thoughts Reflection Section
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = CardBackground),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "Trading Psychology & Strategy Plan",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                  value = strategyText,
                  onValueChange = {
                    strategyText = it
                    onSaveJournal(dateStr, notesText, thoughtsText, currentScreenshotPath, journal?.manualPnl)
                  },
                  label = { Text("Strategy / Setups Traded", color = TextSecondary) },
                  placeholder = { Text("e.g. Liquidity Sweep at Asia low, FVG pullback...", color = TextMuted) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("day_strategy_input"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                  )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = thoughtsText,
                  onValueChange = {
                    thoughtsText = it
                    onSaveJournal(dateStr, notesText, it, currentScreenshotPath, journal?.manualPnl)
                  },
                  label = { Text("Mental State & Day Reflections", color = TextSecondary) },
                  placeholder = { Text("e.g. Felt very calm and disciplined today. Stuck to max 2 trades rule.", color = TextMuted) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("day_thoughts_input"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                  )
                )
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(20.dp))

            // Trades for this date header with Add button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Trades on this day (${trades.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )

              Button(
                onClick = onAddTradeClick,
                colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen, contentColor = BackgroundDark),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_trade_for_day_button")
              ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Trade", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
          }

          if (trades.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(SurfaceDark, RoundedCornerShape(10.dp))
                  .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "No trades logged for $formattedHeaderDate",
                    color = TextMuted,
                    fontSize = 13.sp
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  OutlinedButton(
                    onClick = onAddTradeClick,
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("+ Add First Trade", color = ProfitGreen, fontSize = 12.sp)
                  }
                }
              }
            }
          } else {
            items(trades) { trade ->
              TradeItemCard(trade = trade, onDelete = { onDeleteTrade(trade.id) })
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
fun NotesCard(
  notesText: String,
  isEditing: Boolean,
  onEditClick: () -> Unit,
  onNotesChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.heightIn(min = 200.dp),
    colors = CardDefaults.cardColors(containerColor = CardBackground),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Notes",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceVariantDark)
            .clickable { onEditClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("open_notes_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.OpenInNew,
              contentDescription = null,
              tint = TextSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isEditing) "Done" else "Open Notes",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (isEditing) {
        OutlinedTextField(
          value = notesText,
          onValueChange = onNotesChange,
          placeholder = { Text("Enter your trade notes here...", color = TextMuted) },
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("notes_textarea_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = CardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )
      } else {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clickable { onEditClick() },
          contentAlignment = if (notesText.isBlank()) Alignment.Center else Alignment.TopStart
        ) {
          if (notesText.isBlank()) {
            Text(
              text = "Enter your trade notes here",
              color = TextMuted,
              fontSize = 13.sp
            )
          } else {
            Text(
              text = notesText,
              color = TextPrimary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun ScreenshotCard(
  screenshotPath: String?,
  onUploadClick: () -> Unit,
  onRemoveScreenshot: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.heightIn(min = 200.dp),
    colors = CardDefaults.cardColors(containerColor = CardBackground),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Screenshot",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )

        if (screenshotPath != null) {
          IconButton(
            onClick = onRemoveScreenshot,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Remove Screenshot",
              tint = LossRed,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (screenshotPath != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .clickable { onUploadClick() }
        ) {
          AsyncImage(
            model = File(screenshotPath),
            contentDescription = "Trade Screenshot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
      } else {
        // Upload Placeholder matching Screenshot 1
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .clickable { onUploadClick() }
            .padding(16.dp)
            .testTag("upload_screenshot_area"),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.CloudUpload,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Upload Screenshot",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tap to pick from gallery or take snapshot",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }
      }
    }
  }
}

@Composable
fun TradeItemCard(
  trade: Trade,
  onDelete: () -> Unit
) {
  val isPositive = trade.pnl >= 0
  val pnlColor = if (isPositive) ProfitGreen else LossRed
  val pnlBg = if (isPositive) ProfitGreenBg else LossRedBg
  val sign = if (isPositive) "+" else ""

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = CardBackground),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Direction pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (trade.direction == "LONG") ProfitGreenBg else LossRedBg)
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = trade.direction,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (trade.direction == "LONG") ProfitGreen else LossRed
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = trade.asset,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "@ ${trade.time}",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
          Text(
            text = "Strategy: ${trade.strategy} | Emotion: ${trade.preEmotion}",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "$sign$${String.format(Locale.US, "%.2f", trade.pnl)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = pnlColor
          )
          Text(
            text = "Entry: ${trade.entryPrice} -> Exit: ${trade.exitPrice}",
            fontSize = 10.sp,
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Trade",
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
