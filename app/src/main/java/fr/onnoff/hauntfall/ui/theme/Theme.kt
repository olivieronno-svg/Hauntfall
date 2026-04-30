package fr.onnoff.hauntfall.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = OrManoir,
    onPrimary = NuitProfonde,
    primaryContainer = MauveProfond,
    onPrimaryContainer = OrPale,
    secondary = Ectoplasme,
    onSecondary = NuitProfonde,
    secondaryContainer = MauveNuit,
    onSecondaryContainer = EctoplasmePale,
    tertiary = MauveClair,
    background = NuitProfonde,
    onBackground = Parchemin,
    surface = MauveNuit,
    onSurface = Parchemin,
    surfaceVariant = MauveProfond,
    onSurfaceVariant = PoussiereSpectrale
)

private val LightColors = lightColorScheme(
    primary = MauveProfond,
    onPrimary = Parchemin,
    primaryContainer = OrPale,
    onPrimaryContainer = NuitProfonde,
    secondary = Ectoplasme,
    onSecondary = NuitProfonde,
    tertiary = OrManoir,
    background = Parchemin,
    onBackground = NuitProfonde,
    surface = Parchemin,
    onSurface = NuitProfonde
)

@Composable
fun HauntfallTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = HauntfallTypography,
        content = content
    )
}
