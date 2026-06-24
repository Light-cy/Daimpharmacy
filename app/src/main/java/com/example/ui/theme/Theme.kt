package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  lightColorScheme(
    primary = PharmacyGreenPrimary,
    secondary = PharmacyGreenSecondary,
    tertiary = PharmacyMintAccent,
    background = PharmacyWarmWhite,
    surface = PharmacySurfaceLight,
    surfaceVariant = Color(0xFFE8F5E9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PharmacyLightText,
    onSurface = PharmacyLightText,
    onSurfaceVariant = PharmacySecondaryText,
    error = PharmacyErrorRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PharmacyGreenPrimary,
    secondary = PharmacyGreenSecondary,
    tertiary = PharmacyMintAccent,
    background = PharmacyWarmWhite,
    surface = PharmacySurfaceLight,
    surfaceVariant = Color(0xFFE8F5E9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PharmacyLightText,
    onSurface = PharmacyLightText,
    onSurfaceVariant = PharmacySecondaryText,
    error = PharmacyErrorRed
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force false to ensure all screens reflect the beautiful light green theme
  dynamicColor: Boolean = false, // Set to false to force our beautiful brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
