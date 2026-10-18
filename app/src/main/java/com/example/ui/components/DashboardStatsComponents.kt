package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun WinRateGauge(
  winRate: Float,
  modifier: Modifier = Modifier,
  winningTrades: Int = 0,
  losingTrades: Int = 0
) {
  val animatedWinRate by animateFloatAsState(
    targetValue = winRate,
    animationSpec = tween(durationMillis = 800),
    label = "winRateAnim"
  )

  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Text(
        text = "${String.format(Locale.US, "%.0f", animatedWinRate)}%",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Losing trades  $losingTrades",
        fontSize = 11.sp,
        color = TextSecondary
      )
      Text(
        text = "Winning trades  $winningTrades",
        fontSize = 11.sp,
        color = TextSecondary
      )
    }

    Box(
      modifier = Modifier.size(76.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(68.dp)) {
        val strokeWidth = 8.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)

        // Background track (dark loss track)
        drawArc(
          color = Color(0xFF262E3D),
          startAngle = -90f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Winning arc
        val sweep = (animatedWinRate / 100f) * 360f
        if (sweep > 0f) {
          drawArc(
            color = ProfitGreen,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }
      }
    }
  }
}

@Composable
fun ProfitFactorBar(
  profitFactor: Double,
  totalProfit: Double,
  totalLoss: Double,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = String.format(Locale.US, "%.2f", profitFactor),
      fontSize = 26.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Segmented indicator bars matching Screenshot 3
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(18.dp),
      horizontalArrangement = Arrangement.spacedBy(2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val totalSum = (totalProfit + totalLoss).coerceAtLeast(1.0)
      val profitRatio = (totalProfit / totalSum).toFloat().coerceIn(0.05f, 0.95f)
      val totalBars = 32
      val greenBarsCount = (profitRatio * totalBars).toInt().coerceIn(1, totalBars - 1)

      for (i in 0 until totalBars) {
        val isGreen = i < greenBarsCount
        Box(
          modifier = Modifier
            .weight(1f)
            .height(16.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(if (isGreen) ProfitGreen else LossRed)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Total profit",
        fontSize = 12.sp,
        color = TextSecondary
      )
      Text(
        text = "$${String.format(Locale.US, "%.0f", totalProfit)}",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = ProfitGreen
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Total loss",
        fontSize = 12.sp,
        color = TextSecondary
      )
      Text(
        text = "$${String.format(Locale.US, "%.0f", totalLoss)}",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = LossRed
      )
    }
  }
}
