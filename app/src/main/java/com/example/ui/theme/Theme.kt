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

private val DarkColorScheme = darkColorScheme(
    primary = LabTealContainer,
    onPrimary = LabTealOnContainer,
    primaryContainer = LabTealPrimary,
    onPrimaryContainer = LabTealContainer,
    secondary = LabSlateContainer,
    onSecondary = LabSlateOnContainer,
    background = Color(0xFF0F1415),
    surface = Color(0xFF191C1D),
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF3F484A),
    onSurfaceVariant = Color(0xFFBFC8CA)
)

private val LightColorScheme = lightColorScheme(
    primary = LabTealPrimary,
    onPrimary = LabTealOnPrimary,
    primaryContainer = LabTealContainer,
    onPrimaryContainer = LabTealOnContainer,
    secondary = LabSlateSecondary,
    onSecondary = LabSlateOnSecondary,
    secondaryContainer = LabSlateContainer,
    onSecondaryContainer = LabSlateOnContainer,
    background = LabSurfaceLight,
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C1D),
    surfaceVariant = LabSurfaceVariant,
    onSurfaceVariant = Color(0xFF3F484A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our crisp clinical theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
