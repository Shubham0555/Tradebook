package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = ProfitGreen,
  onPrimary = BackgroundDark,
  primaryContainer = SurfaceVariantDark,
  onPrimaryContainer = ProfitGreen,
  secondary = AccentCyan,
  onSecondary = BackgroundDark,
  secondaryContainer = SurfaceVariantDark,
  onSecondaryContainer = AccentCyan,
  tertiary = AccentGold,
  onTertiary = BackgroundDark,
  background = BackgroundDark,
  onBackground = TextPrimary,
  surface = SurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = CardBackground,
  onSurfaceVariant = TextSecondary,
  outline = CardBorder,
  outlineVariant = CardBorderActive,
  error = LossRed,
  onError = TextPrimary,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek trading terminal dark theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = BackgroundDark.toArgb()
        window.navigationBarColor = BackgroundDark.toArgb()
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
