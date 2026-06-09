package com.example.zerosekai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val ZeroSekaiColorScheme = darkColorScheme(
    primary = ZPrimary,
    primaryContainer = ZPrimaryDeep,
    secondary = ZSecondary,
    secondaryContainer = ZSurfaceElevated,
    tertiary = ZAccent,

    background = ZBackground,
    surface = ZSurface,
    surfaceVariant = ZSurfaceElevated,
    surfaceContainer = ZSurface,
    surfaceContainerHigh = ZSurfaceElevated,

    onPrimary = ZText,
    onSecondary = ZText,
    onTertiary = ZText,
    onBackground = ZText,
    onSurface = ZText,
    onSurfaceVariant = ZTextMuted,

    outline = ZBorder,
    outlineVariant = ZBorderSoft,
    error = ZError,
    onError = ZText
)

private val ZeroSekaiShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ZeroSekaiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZeroSekaiColorScheme,
        typography = Typography,
        shapes = ZeroSekaiShapes,
        content = content
    )
}
