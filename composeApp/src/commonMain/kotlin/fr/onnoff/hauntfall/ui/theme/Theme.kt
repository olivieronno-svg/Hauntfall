package fr.onnoff.hauntfall.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

/**
 * Theme commun Android + iOS. La gestion du status bar est faite côté Android
 * dans [fr.onnoff.hauntfall.MainActivity] (et n'est pas pertinente sur iOS).
 */
@Composable
fun HauntfallTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = HauntfallTypography,
        content = content
    )
}
