package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.Trade
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
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
fun TradeEditorSheet(
  initialDate: String,
  existingTrade: Trade? = null,
  onDismiss: () -> Unit,
  onSave: (Trade) -> Unit,
  onUploadScreenshot: (Uri, (String?) -> Unit) -> Unit
) {
  var asset by remember { mutableStateOf(existingTrade?.asset ?: "NQ") }
  var direction by remember { mutableStateOf(existingTrade?.direction ?: "LONG") }
  var dateStr by remember { mutableStateOf(existingTrade?.date ?: initialDate) }
  var timeStr by remember {
    mutableStateOf(existingTrade?.time ?: SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
  }
  var entryPriceStr by remember { mutableStateOf(existingTrade?.entryPrice?.toString() ?: "20350.00") }
  var exitPriceStr by remember { mutableStateOf(existingTrade?.exitPrice?.toString() ?: "20420.00") }
  var quantityStr by remember { mutableStateOf(existingTrade?.quantity?.toString() ?: "1.0") }
  var manualPnlStr by remember { mutableStateOf(existingTrade?.pnl?.toString() ?: "") }
  var strategy by remember { mutableStateOf(existingTrade?.strategy ?: "Liquidity Sweep") }
  var preEmotion by remember { mutableStateOf(existingTrade?.preEmotion ?: "Disciplined") }
  var duringEmotion by remember { mutableStateOf(existingTrade?.duringEmotion ?: "Patient") }
  var postEmotion by remember { mutableStateOf(existingTrade?.postEmotion ?: "Satisfied") }
  var disciplineScore by remember { mutableIntStateOf(existingTrade?.disciplineScore ?: 5) }
  var notes by remember { mutableStateOf(existingTrade?.notes ?: "") }
  var screenshotPath by remember { mutableStateOf(existingTrade?.screenshotUri) }
  var accountName by remember { mutableStateOf(existingTrade?.accountName ?: "Apex 50k") }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onUploadScreenshot(uri) { path ->
        screenshotPath = path
      }
    }
  }

  // Calculate projected P&L
  val entryPrice = entryPriceStr.toDoubleOrNull() ?: 0.0
  val exitPrice = exitPriceStr.toDoubleOrNull() ?: 0.0
  val qty = quantityStr.toDoubleOrNull() ?: 1.0

  val calculatedPnl = if (manualPnlStr.isNotBlank()) {
    manualPnlStr.toDoubleOrNull() ?: 0.0
  } else {
    if (direction == "LONG") {
      (exitPrice - entryPrice) * qty * (if (asset == "NQ") 20.0 else if (asset == "ES") 50.0 else 1.0)
    } else {
      (entryPrice - exitPrice) * qty * (if (asset == "NQ") 20.0 else if (asset == "ES") 50.0 else 1.0)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.95f)
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
      color = BackgroundDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (existingTrade == null) "Log New Trade" else "Edit Trade",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_trade_editor_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Direction: Long / Short
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (direction == "LONG") ProfitGreen else SurfaceDark)
                .border(
                  1.dp,
                  if (direction == "LONG") ProfitGreen else CardBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable { direction = "LONG" }
                .testTag("direction_long_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "LONG ▲",
                fontWeight = FontWeight.Bold,
                color = if (direction == "LONG") BackgroundDark else ProfitGreen,
                fontSize = 14.sp
              )
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (direction == "SHORT") LossRed else SurfaceDark)
                .border(
                  1.dp,
                  if (direction == "SHORT") LossRed else CardBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable { direction = "SHORT" }
                .testTag("direction_short_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "SHORT ▼",
                fontWeight = FontWeight.Bold,
                color = if (direction == "SHORT") BackgroundDark else LossRed,
                fontSize = 14.sp
              )
            }
          }

          // Asset selector chips
          Text("Symbol / Asset", color = TextSecondary, fontSize = 12.sp)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val assets = listOf("NQ", "ES", "BTC", "ETH", "EURUSD", "AAPL", "NVDA", "TSLA")
            assets.forEach { item ->
              val isSelected = asset == item
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) SurfaceVariantDark else CardBackground)
                  .border(1.dp, if (isSelected) AccentCyan else CardBorder, RoundedCornerShape(8.dp))
                  .clickable { asset = item }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
                  .testTag("asset_chip_$item")
              ) {
                Text(
                  text = item,
                  color = if (isSelected) AccentCyan else TextPrimary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
              }
            }
          }

          // Prices and Quantity
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = entryPriceStr,
              onValueChange = { entryPriceStr = it },
              label = { Text("Entry Price", color = TextSecondary) },
              modifier = Modifier
                .weight(1f)
                .testTag("trade_entry_price_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )

            OutlinedTextField(
              value = exitPriceStr,
              onValueChange = { exitPriceStr = it },
              label = { Text("Exit Price", color = TextSecondary) },
              modifier = Modifier
                .weight(1f)
                .testTag("trade_exit_price_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )

            OutlinedTextField(
              value = quantityStr,
              onValueChange = { quantityStr = it },
              label = { Text("Size", color = TextSecondary) },
              modifier = Modifier
                .width(85.dp)
                .testTag("trade_size_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )
          }

          // Net Profit/Loss Preview
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (calculatedPnl >= 0) ProfitGreenBg else LossRedBg)
              .border(
                1.dp,
                if (calculatedPnl >= 0) ProfitGreen.copy(0.4f) else LossRed.copy(0.4f),
                RoundedCornerShape(10.dp)
              )
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Estimated Net P&L", fontSize = 11.sp, color = TextSecondary)
                Text(
                  text = "${if (calculatedPnl >= 0) "+" else ""}$${String.format(Locale.US, "%.2f", calculatedPnl)}",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (calculatedPnl >= 0) ProfitGreen else LossRed
                )
              }

              OutlinedTextField(
                value = manualPnlStr,
                onValueChange = { manualPnlStr = it },
                label = { Text("Override $", color = TextMuted) },
                placeholder = { Text("e.g. 350.00", color = TextMuted) },
                modifier = Modifier
                  .width(130.dp)
                  .testTag("trade_manual_pnl_input"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = AccentCyan,
                  unfocusedBorderColor = CardBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                )
              )
            }
          }

          // Strategy Setup
          Text("Strategy Setup", color = TextSecondary, fontSize = 12.sp)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val strategies = listOf(
              "Liquidity Sweep",
              "Fair Value Gap",
              "Breakout & Retest",
              "VWAP Bounce",
              "Trend Following",
              "Mean Reversion"
            )
            strategies.forEach { s ->
              val isSelected = strategy == s
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) SurfaceVariantDark else CardBackground)
                  .border(1.dp, if (isSelected) AccentGold else CardBorder, RoundedCornerShape(8.dp))
                  .clickable { strategy = s }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = s,
                  color = if (isSelected) AccentGold else TextSecondary,
                  fontSize = 12.sp
                )
              }
            }
          }

          // Emotional States during trade
          Text("Emotional States (Psychology Tracking)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

          // Pre-Trade Emotion
          Column {
            Text("Pre-Trade Emotion", color = TextMuted, fontSize = 11.sp)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("Disciplined", "Calm", "FOMO", "Confident", "Hesitant", "Anxious", "Greedy").forEach { em ->
                val isSel = preEmotion == em
                val color = if (em == "FOMO" || em == "Greedy") LossRed else if (em == "Disciplined" || em == "Calm") ProfitGreen else AccentCyan
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSel) color.copy(alpha = 0.2f) else CardBackground)
                    .border(1.dp, if (isSel) color else CardBorder, RoundedCornerShape(6.dp))
                    .clickable { preEmotion = em }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(text = em, color = if (isSel) color else TextSecondary, fontSize = 11.sp)
                }
              }
            }
          }

          // During-Trade Emotion
          Column {
            Text("During-Trade Emotion", color = TextMuted, fontSize = 11.sp)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("Patient", "Calm", "Anxious", "Panicked", "Excited").forEach { em ->
                val isSel = duringEmotion == em
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSel) AccentCyan.copy(alpha = 0.2f) else CardBackground)
                    .border(1.dp, if (isSel) AccentCyan else CardBorder, RoundedCornerShape(6.dp))
                    .clickable { duringEmotion = em }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(text = em, color = if (isSel) AccentCyan else TextSecondary, fontSize = 11.sp)
                }
              }
            }
          }

          // Post-Trade Emotion
          Column {
            Text("Post-Trade Emotion", color = TextMuted, fontSize = 11.sp)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("Satisfied", "Disciplined", "Regretful", "Relieved", "Angry").forEach { em ->
                val isSel = postEmotion == em
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSel) AccentGold.copy(alpha = 0.2f) else CardBackground)
                    .border(1.dp, if (isSel) AccentGold else CardBorder, RoundedCornerShape(6.dp))
                    .clickable { postEmotion = em }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(text = em, color = if (isSel) AccentGold else TextSecondary, fontSize = 11.sp)
                }
              }
            }
          }

          // Discipline Rating (1 to 5 Stars)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Discipline & Rule Adherence", color = TextSecondary, fontSize = 12.sp)
            Row {
              for (star in 1..5) {
                IconButton(
                  onClick = { disciplineScore = star },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$star Stars",
                    tint = if (star <= disciplineScore) AccentGold else CardBorder,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }

          // Screenshot Upload
          Text("Trade Chart Screenshot", color = TextSecondary, fontSize = 12.sp)
          if (screenshotPath != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
            ) {
              AsyncImage(
                model = File(screenshotPath!!),
                contentDescription = "Chart Screenshot",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .background(SurfaceDark)
                .clickable {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
                .testTag("upload_chart_screenshot_button"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Attach Chart Screenshot", color = AccentCyan, fontSize = 12.sp)
              }
            }
          }

          // Trade Notes
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Trade Execution Notes & Reasoning", color = TextSecondary) },
            placeholder = { Text("Why did you enter? Did you follow your risk rules?", color = TextMuted) },
            modifier = Modifier
              .fillMaxWidth()
              .height(90.dp)
              .testTag("trade_notes_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AccentCyan,
              unfocusedBorderColor = CardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save Button
        Button(
          onClick = {
            val tradeToSave = (existingTrade ?: Trade(date = dateStr)).copy(
              date = dateStr,
              time = timeStr,
              asset = asset,
              direction = direction,
              entryPrice = entryPrice,
              exitPrice = exitPrice,
              quantity = qty,
              pnl = calculatedPnl,
              pnlPercent = if (entryPrice > 0) ((calculatedPnl / (entryPrice * qty)) * 100.0) else 0.0,
              preEmotion = preEmotion,
              duringEmotion = duringEmotion,
              postEmotion = postEmotion,
              disciplineScore = disciplineScore,
              strategy = strategy,
              notes = notes,
              screenshotUri = screenshotPath,
              accountName = accountName
            )
            onSave(tradeToSave)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen, contentColor = BackgroundDark),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("save_trade_button")
        ) {
          Text(
            text = "Save Trade to Journal",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
