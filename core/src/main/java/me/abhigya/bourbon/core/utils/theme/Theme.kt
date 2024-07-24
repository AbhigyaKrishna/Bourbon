package me.abhigya.bourbon.core.utils.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    primary = ColorPrimary,
    secondary = ColorSecondary,
    tertiary = ColorTertiary,
    background = ColorBackground,
    error = ColorError,
    outlineVariant = ColorBackground.copy(alpha = 0.0f) // divider
)

@Composable
fun BourbonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}