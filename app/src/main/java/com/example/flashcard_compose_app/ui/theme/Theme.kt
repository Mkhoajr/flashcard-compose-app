package com.example.flashcard_compose_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    secondary = CyanLight,
    tertiary = AmberLight,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    background = Grey900,
    surface = Grey800,
    onBackground = Grey100,
    onSurface = Grey100
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    secondary = Cyan,
    tertiary = Amber,
    onPrimary = White,
    onSecondary = White,
    onTertiary = Black,
    background = Grey50,
    surface = White,
    onBackground = Grey900,
    onSurface = Grey900
)

@Composable
fun FlashcardcomposeappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}