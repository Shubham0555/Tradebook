package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssetStat
import com.example.model.EconomicNewsItem
import com.example.model.Trade
import com.example.ui.TradeJournalViewModel
import com.example.ui.components.EquityCurveChart
import com.example.ui.components.ProfitFactorBar
import com.example.ui.components.WeeklyPnlStrip
import com.example.ui.components.WinRateGauge
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
fun DashboardScreen(
  viewModel: TradeJournalViewModel,
  onOpenDayModal: (String) -> Unit,
  onOpenTradeEditor: () -> Unit,
  onOpenDriveSync: () -> Unit,
  onNavigateToCalendar: () -> Unit,
  onNavigateToTrades: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedAccount by viewModel.selectedAccount.collectAsState()
  val weeklyStrip by viewModel.weeklyPnlStrip.collectAsState()
  val equityPoints by viewModel.equityPoints.collectAsState()
  val totalTrades by viewModel.totalTradesCount.collectAsState()
  val winningTrades by viewModel.winningTradesCount.collectAsState()
  val breakevenTrades by viewModel.breakevenTradesCount.collectAsState()
  val losingTrades by viewModel.losingTradesCount.collectAsState()
  val winRate by viewModel.winRate.collectAsState()
  val totalProfit by viewModel.totalProfit.collectAsState()
  val totalLoss by viewModel.totalLoss.collectAsState()
  val profitFactor by viewModel.profitFactor.collectAsState()
  val netPnl by viewModel.netPnl.collectAsState()
  val balance by viewModel.currentAccountBalance.collectAsState()
  val mostTraded by viewModel.mostTradedAssets.collectAsState()
  val newsList by viewModel.newsList.collectAsState()
  val allTrades by viewModel.filteredTrades.collectAsState()

  Scaffold(
    containerColor = BackgroundDark,
    floatingActionButton = {
      FloatingActionButton(
        onClick = onOpenTradeEditor,
        containerColor = ProfitGreen,
        contentColor = BackgroundDark,
        shape = CircleShape,
        modifier = Modifier.testTag("fab_add_trade")
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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Navigation Bar matching Screenshot 3
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onNavigateToCalendar,
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.Menu, contentDescription = "Navigation Menu", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$selectedAccount > Dashboard",
              fontSize = 13.sp,
              color = TextSecondary
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Live Trades status pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(ProfitGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Live Trades", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Google Drive Sync Icon Button
            IconButton(
              onClick = onOpenDriveSync,
              modifier = Modifier
                .size(36.dp)
                .testTag("open_drive_sync_button")
            ) {
              Icon(
                Icons.Default.CloudDone,
                contentDescription = "Google Drive Sync",
                tint = ProfitGreen
              )
            }
          }
        }
      }

      // Welcome back Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Welcome back, shubham",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "Sun 18 Oct, 2026",
              fontSize = 13.sp,
              color = TextMuted
            )
          }

          // Week selector pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(CardBackground)
              .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Week", tint = TextSecondary, modifier = Modifier.size(18.dp))
              Text("Current Week", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
              Icon(Icons.Default.ChevronRight, contentDescription = "Next Week", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      // Weekly P&L Strip Cards matching Screenshot 3
      item {
        WeeklyPnlStrip(
          days = weeklyStrip,
          onDayClick = onOpenDayModal
        )
      }

      // Account Balance & Equity Curve Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CardBackground),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Account Balance",
                  fontSize = 13.sp,
                  color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                val balancePrefix = if (netPnl >= 0) "+$" else "-$"
                Text(
                  text = "$${String.format(Locale.US, "%,.0f", balance)}",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = "Last 30 Days (${if (netPnl >= 0) "+" else ""}$${String.format(Locale.US, "%.0f", netPnl)})",
                  fontSize = 11.sp,
                  color = if (netPnl >= 0) ProfitGreen else LossRed
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SurfaceVariantDark)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "${if (netPnl >= 0) "+" else ""}${String.format(Locale.US, "%.1f", (netPnl / 50000.0) * 100)}%",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (netPnl >= 0) ProfitGreen else LossRed
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            EquityCurveChart(
              points = equityPoints,
              isProfit = netPnl >= 0,
              heightDp = 150
            )

            // Timeline labels
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Sep 25", fontSize = 10.sp, color = TextMuted)
              Text("Oct 03", fontSize = 10.sp, color = TextMuted)
              Text("Oct 10", fontSize = 10.sp, color = TextMuted)
              Text("Oct 18", fontSize = 10.sp, color = TextMuted)
            }
          }
        }
      }

      // 2x2 Stats Grid: Most Traded Assets, Total Trades, Winrate, Profit Factor
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Most Traded Assets Card
          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Most Traded Assets", fontSize = 12.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(8.dp))

              if (mostTraded.isEmpty()) {
                Text("N/A", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("-", color = TextMuted, fontSize = 11.sp)
                Text("-", color = TextMuted, fontSize = 11.sp)
              } else {
                val top = mostTraded.first()
                Text(top.asset, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                mostTraded.take(3).forEach { assetStat ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(assetStat.asset, fontSize = 11.sp, color = TextPrimary)
                    Text(
                      "${assetStat.tradeCount} trades (${String.format(Locale.US, "%.0f", assetStat.winRate)}%)",
                      fontSize = 11.sp,
                      color = TextSecondary
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                }
              }
            }
          }

          // Total Trades Card
          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Total Trades", fontSize = 12.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "$totalTrades",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Winning", fontSize = 11.sp, color = TextSecondary)
                Text("$winningTrades", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ProfitGreen)
              }
              Spacer(modifier = Modifier.height(2.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Breakeven", fontSize = 11.sp, color = TextSecondary)
                Text("$breakevenTrades", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
              }
              Spacer(modifier = Modifier.height(2.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Losing", fontSize = 11.sp, color = TextSecondary)
                Text("$losingTrades", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LossRed)
              }
            }
          }
        }
      }

      // Next Row: Winrate Gauge & Profit Factor
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Trade Winrate Card
          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Trade Winrate", fontSize = 12.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(8.dp))
              WinRateGauge(
                winRate = winRate,
                winningTrades = winningTrades,
                losingTrades = losingTrades
              )
            }
          }

          // Profit Factor Card
          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Profit Factor", fontSize = 12.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(4.dp))
              ProfitFactorBar(
                profitFactor = profitFactor,
                totalProfit = totalProfit,
                totalLoss = totalLoss
              )
            }
          }
        }
      }

      // Recent Trades Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Recent Trades",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "Last registered trades",
              fontSize = 11.sp,
              color = TextMuted
            )
          }

          Row(
            modifier = Modifier.clickable { onNavigateToTrades() },
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "See all",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "See all",
              tint = TextSecondary,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Recent trades items
      val recentTrades = allTrades.take(4)
      if (recentTrades.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(SurfaceDark, RoundedCornerShape(10.dp))
              .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No trades recorded yet. Tap + to log your first trade!", color = TextMuted, fontSize = 12.sp)
          }
        }
      } else {
        items(recentTrades) { trade ->
          RecentTradeCard(
            trade = trade,
            onClick = { onOpenDayModal(trade.date) }
          )
        }
      }

      // High Impact News Section matching Screenshot 3
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "High Impact News",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "Today's upcoming high impact news",
              fontSize = 11.sp,
              color = TextMuted
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SurfaceVariantDark)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("Economic Calendar", fontSize = 11.sp, color = TextSecondary)
          }
        }
      }

      items(newsList) { item ->
        EconomicNewsCard(news = item)
      }

      item {
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }
}

@Composable
fun RecentTradeCard(
  trade: Trade,
  onClick: () -> Unit
) {
  val isPositive = trade.pnl >= 0
  val pnlColor = if (isPositive) ProfitGreen else LossRed
  val pnlBg = if (isPositive) ProfitGreenBg else LossRedBg
  val sign = if (isPositive) "+" else ""

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
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
            Text(trade.asset, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(trade.date, fontSize = 11.sp, color = TextMuted)
          }
          Text("Setup: ${trade.strategy}", fontSize = 11.sp, color = TextSecondary)
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "$sign$${String.format(Locale.US, "%.2f", trade.pnl)}",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = pnlColor
        )
        Text(
          text = "State: ${trade.preEmotion}",
          fontSize = 10.sp,
          color = AccentGold
        )
      }
    }
  }
}

@Composable
fun EconomicNewsCard(news: EconomicNewsItem) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = CardBackground),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    shape = RoundedCornerShape(8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (news.impact == "HIGH") LossRedBg else SurfaceVariantDark)
            .border(
              1.dp,
              if (news.impact == "HIGH") LossRed.copy(alpha = 0.5f) else CardBorder,
              RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = news.impact,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (news.impact == "HIGH") LossRed else AccentGold
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
          Text(news.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
          Text("${news.currency} • ${news.time}", fontSize = 10.sp, color = TextMuted)
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text("F: ${news.forecast}", fontSize = 10.sp, color = TextSecondary)
        Text("P: ${news.previous}", fontSize = 10.sp, color = TextMuted)
      }
    }
  }
}
