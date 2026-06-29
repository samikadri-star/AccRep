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

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = PurpleOnPrimary,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = PurpleOnContainer,
    secondary = PurplePrimary,
    onSecondary = PurpleOnPrimary,
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = PurpleOnContainer,
    background = ElegantDarkBg,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkCard,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkHeader,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantBorder,
    outlineVariant = ElegantBorder,
    error = ElegantError,
    errorContainer = ElegantErrorContainer,
    onErrorContainer = ElegantOnErrorContainer
)

private val LightColorScheme = DarkColorScheme // Force elegant dark mode throughout the application


@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color support on Android 12+ (disable by default for cohesive branding)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
