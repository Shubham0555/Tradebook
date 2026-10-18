package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.Trade
import com.example.ui.TradeJournalViewModel
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
import java.util.Locale

@Composable
fun TradesScreen(
  viewModel: TradeJournalViewModel,
  onBack: () -> Unit,
  onOpenTradeEditor: (Trade?) -> Unit,
  modifier: Modifier = Modifier
) {
  val trades by viewModel.filteredTrades.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedDirectionFilter by remember { mutableStateOf("ALL") }
  var selectedEmotionFilter by remember { mutableStateOf("ALL") }
  var previewScreenshotPath by remember { mutableStateOf<String?>(null) }

  val filteredList = trades.filter { trade ->
    val matchesSearch = searchQuery.isBlank() ||
      trade.asset.contains(searchQuery, ignoreCase = true) ||
      trade.strategy.contains(searchQuery, ignoreCase = true) ||
      trade.notes.contains(searchQuery, ignoreCase = true)
    val matchesDir = selectedDirectionFilter == "ALL" || trade.direction == selectedDirectionFilter
    val matchesEmotion = selectedEmotionFilter == "ALL" || trade.preEmotion == selectedEmotionFilter
    matchesSearch && matchesDir && matchesEmotion
  }

  // Fullscreen Screenshot Preview Dialog
  if (previewScreenshotPath != null) {
    Dialog(onDismissRequest = { previewScreenshotPath = null }) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(BackgroundDark)
          .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = File(previewScreenshotPath!!),
          contentDescription = "Full Screenshot Preview",
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }

  Scaffold(
    containerColor = BackgroundDark,
    floatingActionButton = {
      FloatingActionButton(
        onClick = { onOpenTradeEditor(null) },
        containerColor = ProfitGreen,
        contentColor = BackgroundDark,
        shape = CircleShape,
        modifier = Modifier.testTag("fab_add_trade_trades_screen")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Log Trade", modifier = Modifier.size(28.dp))
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Trade Journal Log", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          }

          Text("${filteredList.size} trades", fontSize = 12.sp, color = TextSecondary)
        }
      }

      // Search Field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by asset, strategy, or notes...", color = TextMuted) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_trades_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = CardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          shape = RoundedCornerShape(10.dp)
        )
      }

      // Filter Chips: Direction & Emotion
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("ALL", "LONG", "SHORT").forEach { dir ->
            val isSel = selectedDirectionFilter == dir
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) SurfaceVariantDark else CardBackground)
                .border(1.dp, if (isSel) AccentCyan else CardBorder, RoundedCornerShape(8.dp))
                .clickable { selectedDirectionFilter = dir }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = dir,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                color = if (isSel) AccentCyan else TextSecondary
              )
            }
          }

          listOf("ALL", "Disciplined", "FOMO", "Confident", "Hesitant").forEach { em ->
            val isSel = selectedEmotionFilter == em
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) SurfaceVariantDark else CardBackground)
                .border(1.dp, if (isSel) AccentGold else CardBorder, RoundedCornerShape(8.dp))
                .clickable { selectedEmotionFilter = em }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = em,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                color = if (isSel) AccentGold else TextSecondary
              )
            }
          }
        }
      }

      // Trades List
      if (filteredList.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(SurfaceDark, RoundedCornerShape(10.dp))
              .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No trades match your search filters.", color = TextMuted, fontSize = 13.sp)
          }
        }
      } else {
        items(filteredList) { trade ->
          FullTradeCard(
            trade = trade,
            onEdit = { onOpenTradeEditor(trade) },
            onDelete = { viewModel.deleteTrade(trade.id) },
            onImageClick = {
              if (trade.screenshotUri != null) {
                previewScreenshotPath = trade.screenshotUri
              }
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }
}

@Composable
fun FullTradeCard(
  trade: Trade,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onImageClick: () -> Unit
) {
  val isPositive = trade.pnl >= 0
  val pnlColor = if (isPositive) ProfitGreen else LossRed
  val pnlBg = if (isPositive) ProfitGreenBg else LossRedBg
  val sign = if (isPositive) "+" else ""

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = CardBackground),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Direction, Asset, Date, P&L
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (trade.direction == "LONG") ProfitGreenBg else LossRedBg)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = trade.direction,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (trade.direction == "LONG") ProfitGreen else LossRed
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Text(trade.asset, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("${trade.date} ${trade.time}", fontSize = 11.sp, color = TextMuted)
        }

        Text(
          text = "$sign$${String.format(Locale.US, "%.2f", trade.pnl)}",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = pnlColor
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Prices & Size
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Entry: ${trade.entryPrice}  |  Exit: ${trade.exitPrice}  |  Size: ${trade.quantity}",
          fontSize = 11.sp,
          color = TextSecondary
        )

        // Discipline Stars
        Row {
          for (i in 1..5) {
            Icon(
              Icons.Default.Star,
              contentDescription = null,
              tint = if (i <= trade.disciplineScore) AccentGold else CardBorder,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Psychology chips: Pre, During, Post
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text("Pre: ${trade.preEmotion}", fontSize = 10.sp, color = AccentCyan)
        Text("•", fontSize = 10.sp, color = TextMuted)
        Text("During: ${trade.duringEmotion}", fontSize = 10.sp, color = AccentCyan)
        Text("•", fontSize = 10.sp, color = TextMuted)
        Text("Post: ${trade.postEmotion}", fontSize = 10.sp, color = AccentGold)
      }

      if (trade.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = trade.notes,
          fontSize = 12.sp,
          color = TextPrimary,
          maxLines = 2
        )
      }

      // Screenshot Thumbnail if present
      if (trade.screenshotUri != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceDark)
            .clickable { onImageClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("View Attached Chart Screenshot", fontSize = 11.sp, color = AccentCyan)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = LossRed, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
