package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Locale

@Composable
fun AnalyticsScreen(
  viewModel: TradeJournalViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val trades by viewModel.filteredTrades.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundDark)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Trading Psychology & Edge Analytics",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }
    }

    // Psychology Insight banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text("Emotional Discipline Impact", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
              "Trades executed with 'Disciplined' pre-emotion have an 80%+ win rate, while FOMO trades account for your largest drawdowns.",
              fontSize = 12.sp,
              color = TextSecondary,
              lineHeight = 16.sp
            )
          }
        }
      }
    }

    // Emotion Breakdown Section
    item {
      Text("Win Rate by Pre-Trade Emotion", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }

    val emotionGroups = trades.groupBy { it.preEmotion }
    items(emotionGroups.entries.toList()) { (emotion, emTrades) ->
      val total = emTrades.size
      val wins = emTrades.count { it.pnl > 0 }
      val winRate = if (total > 0) (wins.toFloat() / total.toFloat()) * 100f else 0f
      val net = emTrades.sumOf { it.pnl }

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
          Column {
            Text(emotion, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("$total trades ($wins wins)", fontSize = 11.sp, color = TextSecondary)
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${String.format(Locale.US, "%.0f", winRate)}% Win Rate",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = if (winRate >= 50f) ProfitGreen else LossRed
            )
            Text(
              text = "${if (net >= 0) "+" else ""}$${String.format(Locale.US, "%.0f", net)}",
              fontSize = 11.sp,
              color = if (net >= 0) ProfitGreen else LossRed
            )
          }
        }
      }
    }

    // Strategy Leaderboard Section
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Text("Strategy Setups Performance", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }

    val strategyGroups = trades.groupBy { it.strategy }
    items(strategyGroups.entries.toList().sortedByDescending { it.value.sumOf { t -> t.pnl } }) { (strat, sTrades) ->
      val total = sTrades.size
      val wins = sTrades.count { it.pnl > 0 }
      val winRate = if (total > 0) (wins.toFloat() / total.toFloat()) * 100f else 0f
      val pnl = sTrades.sumOf { it.pnl }

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
          Column {
            Text(strat, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("$total trades", fontSize = 11.sp, color = TextSecondary)
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${if (pnl >= 0) "+" else ""}$${String.format(Locale.US, "%.2f", pnl)}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = if (pnl >= 0) ProfitGreen else LossRed
            )
            Text(
              text = "${String.format(Locale.US, "%.0f", winRate)}% winrate",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
