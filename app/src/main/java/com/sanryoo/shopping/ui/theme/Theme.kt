package com.sanryoo.shopping.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Primary,
    onPrimary = Color.White,

    secondary = Secondary,
    onSecondary = Color.Black,

    background = Color.Black,
    onBackground = Color.White,

    surface = DarkSurface,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Primary,
    onPrimary = Color.White,

    secondary = Secondary,
    onSecondary = Color.Black,

    background = Color.White,
    onBackground = Color.Black,

    surface = LightSurface,
    onSurface = Color.Black,
)

@Composable
fun ShoppingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}