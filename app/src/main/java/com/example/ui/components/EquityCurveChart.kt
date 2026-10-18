package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EquityPoint
import com.example.ui.theme.ChartGridColor
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.TextMuted
import java.util.Locale

@Composable
fun EquityCurveChart(
  points: List<EquityPoint>,
  modifier: Modifier = Modifier,
  isProfit: Boolean = true,
  heightDp: Int = 180
) {
  var selectedIndex by remember { mutableStateOf<Int?>(null) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(heightDp.dp)
  ) {
    if (points.isEmpty()) {
      Text(
        text = "No trades in this period",
        color = TextMuted,
        modifier = Modifier.align(Alignment.Center),
        fontSize = 13.sp
      )
      return@Box
    }

    val lineColor = if (isProfit) ProfitGreen else LossRed
    val gradientBottom = Color(0x00000000)
    val gradientTop = if (isProfit) Color(0x3300E676) else Color(0x33FF3D57)

    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(points) {
          detectTapGestures { offset ->
            val w = size.width
            val step = w / (points.size - 1).coerceAtLeast(1)
            val idx = (offset.x / step).toInt().coerceIn(0, points.size - 1)
            selectedIndex = if (selectedIndex == idx) null else idx
          }
        }
    ) {
      val w = size.width
      val h = size.height
      val padTop = 20f
      val padBottom = 26f
      val chartH = h - padTop - padBottom

      val minVal = points.minOfOrNull { it.pnl } ?: 0.0
      val maxVal = points.maxOfOrNull { it.pnl } ?: 100.0
      val range = if (maxVal == minVal) 100.0 else (maxVal - minVal)

      // Draw subtle horizontal grid lines
      val gridSteps = 3
      for (i in 0..gridSteps) {
        val y = padTop + (chartH / gridSteps) * i
        drawLine(
          color = ChartGridColor,
          start = Offset(0f, y),
          end = Offset(w, y),
          strokeWidth = 1f,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
      }

      // Zero line if in range
      if (minVal < 0 && maxVal > 0) {
        val zeroY = padTop + chartH * (1f - ((0.0 - minVal) / range).toFloat())
        drawLine(
          color = Color(0xFF475569),
          start = Offset(0f, zeroY),
          end = Offset(w, zeroY),
          strokeWidth = 1.5f
        )
      }

      val stepX = w / (points.size - 1).coerceAtLeast(1)
      val path = Path()
      val fillPath = Path()

      points.forEachIndexed { i, pt ->
        val x = i * stepX
        val normY = ((pt.pnl - minVal) / range).toFloat()
        val y = padTop + chartH * (1f - normY)

        if (i == 0) {
          path.moveTo(x, y)
          fillPath.moveTo(x, h - padBottom)
          fillPath.lineTo(x, y)
        } else {
          val prevX = (i - 1) * stepX
          val prevNormY = ((points[i - 1].pnl - minVal) / range).toFloat()
          val prevY = padTop + chartH * (1f - prevNormY)
          val controlX1 = prevX + (x - prevX) / 2f
          val controlX2 = prevX + (x - prevX) / 2f

          path.cubicTo(controlX1, prevY, controlX2, y, x, y)
          fillPath.cubicTo(controlX1, prevY, controlX2, y, x, y)
        }
      }

      // Complete fill path
      val lastX = (points.size - 1) * stepX
      fillPath.lineTo(lastX, h - padBottom)
      fillPath.close()

      // Draw area fill
      drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
          colors = listOf(gradientTop, gradientBottom),
          startY = padTop,
          endY = h - padBottom
        )
      )

      // Draw line
      drawPath(
        path = path,
        color = lineColor,
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
      )

      // Draw dot on the last point or selected point
      val highlightIdx = selectedIndex ?: (points.size - 1)
      if (highlightIdx in points.indices) {
        val pt = points[highlightIdx]
        val x = highlightIdx * stepX
        val normY = ((pt.pnl - minVal) / range).toFloat()
        val y = padTop + chartH * (1f - normY)

        // Outer glow
        drawCircle(
          color = lineColor.copy(alpha = 0.35f),
          radius = 12f,
          center = Offset(x, y)
        )
        // Solid dot
        drawCircle(
          color = lineColor,
          radius = 5.5f,
          center = Offset(x, y)
        )
      }
    }

    // Tooltip overlay if tapped
    selectedIndex?.let { idx ->
      if (idx in points.indices) {
        val pt = points[idx]
        val prefix = if (pt.pnl >= 0) "+$" else "-$"
        val absPnl = Math.abs(pt.pnl)
        Box(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .background(Color(0xE61E293B), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${pt.label}: $prefix${String.format(Locale.US, "%.2f", absPnl)}",
            color = if (pt.pnl >= 0) ProfitGreen else LossRed,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}
