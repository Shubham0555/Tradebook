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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthPerformance
import com.example.ui.TradeJournalViewModel
import com.example.ui.components.EquityCurveChart
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
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
  viewModel: TradeJournalViewModel,
  onOpenDayModal: (String) -> Unit,
  onOpenDriveSync: () -> Unit,
  onNavigateToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedAccount by viewModel.selectedAccount.collectAsState()
  val calendarYear by viewModel.calendarYear.collectAsState()
  val calendarMonth by viewModel.calendarMonth.collectAsState()
  val selectedDate by viewModel.selectedDate.collectAsState()
  val yearlyPerformance by viewModel.yearlyPerformance.collectAsState()
  val equityPoints by viewModel.equityPoints.collectAsState()
  val calendarStats by viewModel.calendarDateStats.collectAsState()
  val netPnl by viewModel.netPnl.collectAsState()
  val balance by viewModel.currentAccountBalance.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundDark)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Navigation matching Screenshot 2 & 4
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateToDashboard,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(Icons.Default.Menu, contentDescription = "Dashboard", tint = TextPrimary)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$selectedAccount > Calendar",
            fontSize = 13.sp,
            color = TextSecondary
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Balance Icon
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(CardBackground)
              .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Text("$", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Live Trades indicator pill
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

          // Google Drive Cloud Sync Icon Button
          IconButton(
            onClick = onOpenDriveSync,
            modifier = Modifier.size(36.dp)
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

    // Yearly Performance Card matching Screenshot 2
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
                text = "Yearly Performance",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "Select a month to view its performance",
                fontSize = 11.sp,
                color = TextMuted
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariantDark)
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Year $calendarYear",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Month Matrix row matching Screenshot 2
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Year tag
            Box(
              modifier = Modifier
                .width(65.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$calendarYear",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            }

            yearlyPerformance.forEach { month ->
              val isSelectedMonth = month.monthNum == calendarMonth
              val isPositive = month.pnl > 0
              val isNegative = month.pnl < 0
              val pnlColor = if (isPositive) ProfitGreen else if (isNegative) LossRed else TextMuted
              val pnlText = if (month.hasData) {
                "${if (isPositive) "+" else ""}$${String.format(Locale.US, "%.0f", month.pnl)}"
              } else "-"

              Column(
                modifier = Modifier
                  .width(60.dp)
                  .height(60.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelectedMonth) SurfaceVariantDark else SurfaceDark)
                  .border(
                    1.dp,
                    if (isSelectedMonth) AccentCyan else CardBorder,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { viewModel.setCalendarMonth(month.monthNum) }
                  .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Text(
                  text = month.monthName,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (isSelectedMonth) AccentCyan else TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = pnlText,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = pnlColor
                )
              }
            }

            // YTD pill
            val ytdPnl = yearlyPerformance.sumOf { it.pnl }
            val ytdTrades = yearlyPerformance.sumOf { it.tradeCount }
            Column(
              modifier = Modifier
                .width(72.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text("YTD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${if (ytdPnl >= 0) "+" else ""}$${String.format(Locale.US, "%.0f", ytdPnl)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (ytdPnl >= 0) ProfitGreen else LossRed
              )
              Text(
                text = "$ytdTrades trades",
                fontSize = 9.sp,
                color = TextMuted
              )
            }
          }
        }
      }
    }

    // Account Balance & Equity Curve Card (Screenshot 2)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Account Balance",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "Equity curve of selected time period",
            fontSize = 11.sp,
            color = TextMuted
          )

          Spacer(modifier = Modifier.height(10.dp))

          EquityCurveChart(
            points = equityPoints,
            isProfit = netPnl >= 0,
            heightDp = 130
          )
        }
      }
    }

    // Monthly Calendar Section matching Screenshot 4
    item {
      val monthNameStr = when (calendarMonth) {
        1 -> "JAN"
        2 -> "FEB"
        3 -> "MAR"
        4 -> "APR"
        5 -> "MAY"
        6 -> "JUN"
        7 -> "JUL"
        8 -> "AUG"
        9 -> "SEP"
        10 -> "OCT"
        11 -> "NOV"
        12 -> "DEC"
        else -> "OCT"
      }

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Calendar Month Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = { viewModel.changeCalendarMonth(-1) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TextPrimary)
              }

              Text(
                text = "$monthNameStr $calendarYear",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
              )

              IconButton(
                onClick = { viewModel.changeCalendarMonth(1) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = TextPrimary)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceVariantDark)
                .clickable {
                  viewModel.calendarYear.value = 2026
                  viewModel.calendarMonth.value = 10
                  viewModel.selectedDate.value = "2026-10-18"
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Today, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today (18)", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Days of Week Header: M, T, W, T, F, S, S
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            val weekDayNames = listOf("M", "T", "W", "T", "F", "S", "S")
            weekDayNames.forEachIndexed { idx, name ->
              val isSunday = idx == 6
              Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSunday) LossRed else TextSecondary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Calculate calendar grid days
          val cal = Calendar.getInstance()
          cal.set(Calendar.YEAR, calendarYear)
          cal.set(Calendar.MONTH, calendarMonth - 1)
          cal.set(Calendar.DAY_OF_MONTH, 1)

          // 1 = Sunday, 2 = Monday, ...
          val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
          // Shift to Monday = 0
          val leadingBlanks = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
          val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

          val totalCells = ((leadingBlanks + maxDaysInMonth + 6) / 7) * 7

          // Calendar Grid
          for (week in 0 until (totalCells / 7)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              for (dayCol in 0 until 7) {
                val cellIndex = week * 7 + dayCol
                val dayNumber = cellIndex - leadingBlanks + 1

                if (dayNumber in 1..maxDaysInMonth) {
                  val dateKey = String.format(Locale.US, "%04d-%02d-%02d", calendarYear, calendarMonth, dayNumber)
                  val dayData = calendarStats[dateKey]
                  val pnl = dayData?.first ?: 0.0
                  val tradeCount = dayData?.second ?: 0
                  val isCurrentSelected = dateKey == selectedDate

                  // Cell Styling
                  val isProfit = pnl > 0
                  val isLoss = pnl < 0

                  val cellBg = when {
                    isProfit -> ProfitGreen.copy(alpha = 0.25f)
                    isLoss -> LossRed.copy(alpha = 0.25f)
                    else -> SurfaceDark
                  }

                  val borderModifier = if (isCurrentSelected) {
                    Modifier.border(1.5.dp, AccentGold, RoundedCornerShape(6.dp))
                  } else if (isProfit) {
                    Modifier.border(1.dp, ProfitGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                  } else if (isLoss) {
                    Modifier.border(1.dp, LossRed.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                  } else {
                    Modifier.border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                  }

                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .height(58.dp)
                      .then(borderModifier)
                      .clip(RoundedCornerShape(6.dp))
                      .background(cellBg)
                      .clickable {
                        viewModel.selectDate(dateKey)
                        onOpenDayModal(dateKey)
                      }
                      .padding(4.dp)
                      .testTag("calendar_day_$dateKey"),
                    contentAlignment = Alignment.TopStart
                  ) {
                    Column(
                      modifier = Modifier.fillMaxSize(),
                      verticalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = "$dayNumber",
                          fontSize = 11.sp,
                          fontWeight = if (isCurrentSelected) FontWeight.Bold else FontWeight.Medium,
                          color = if (dayCol == 6) LossRed else TextPrimary
                        )

                        if (tradeCount > 0) {
                          Box(
                            modifier = Modifier
                              .size(5.dp)
                              .clip(CircleShape)
                              .background(if (isProfit) ProfitGreen else LossRed)
                          )
                        }
                      }

                      if (tradeCount > 0 || pnl != 0.0) {
                        Text(
                          text = "${if (isProfit) "+" else ""}$${String.format(Locale.US, "%.0f", pnl)}",
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isProfit) ProfitGreen else if (isLoss) LossRed else TextPrimary,
                          maxLines = 1
                        )
                      } else {
                        Spacer(modifier = Modifier.height(2.dp))
                      }
                    }
                  }
                } else {
                  // Empty cell before/after month
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .height(58.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color.Transparent)
                  )
                }
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
