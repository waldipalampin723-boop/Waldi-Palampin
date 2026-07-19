package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Indigo500,
    onPrimary = Color.White,
    secondary = Teal500,
    onSecondary = Color.White,
    tertiary = Rose500,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate400
  )

private val LightColorScheme =
  darkColorScheme( // Atmospheric theme is immersive dark by design
    primary = Indigo500,
    onPrimary = Color.White,
    secondary = Teal500,
    onSecondary = Color.White,
    tertiary = Rose500,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate400
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark/immersive theme for this layout
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve custom theme branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
