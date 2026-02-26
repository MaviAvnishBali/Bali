package com.bali.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VibrantMint,
    secondary = DeepTeal,
    tertiary = SoftSage
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantMint,
    secondary = DeepTeal,
    tertiary = SoftSage,

    background = PureWhite,
    surface = PureWhite,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = DarkCharcoal,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal,
    outline = LightGray,
)

@Composable
fun BaliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
