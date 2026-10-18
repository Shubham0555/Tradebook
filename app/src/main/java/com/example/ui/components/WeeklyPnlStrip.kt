package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeekDayPnl
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun WeeklyPnlStrip(
  days: List<WeekDayPnl>,
  onDayClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    days.forEach { day ->
      val isPositive = day.pnl > 0
      val isNegative = day.pnl < 0
      val pnlColor = when {
        isPositive -> ProfitGreen
        isNegative -> LossRed
        else -> TextPrimary
      }
      val pnlText = when {
        isPositive -> "+$${String.format(Locale.US, "%.0f", day.pnl)}"
        isNegative -> "-$${String.format(Locale.US, "%.0f", Math.abs(day.pnl))}"
        else -> "$0"
      }

      val borderModifier = if (day.isCurrent) {
        Modifier.border(1.dp, ProfitGreen.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
      } else {
        Modifier.border(1.dp, CardBorder, RoundedCornerShape(10.dp))
      }

      Column(
        modifier = Modifier
          .width(105.dp)
          .then(borderModifier)
          .clip(RoundedCornerShape(10.dp))
          .background(CardBackground)
          .clickable { onDayClick(day.date) }
          .padding(horizontal = 10.dp, vertical = 10.dp)
          .testTag("weekly_day_${day.date}")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = day.dayLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
          )
          Text(
            text = pnlText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = pnlColor
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${day.tradeCount} trades",
          fontSize = 11.sp,
          color = TextMuted
        )
      }
    }
  }
}
