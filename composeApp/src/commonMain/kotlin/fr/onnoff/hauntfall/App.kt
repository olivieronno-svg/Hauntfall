package fr.onnoff.hauntfall

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.onnoff.hauntfall.ui.game.GameScreen
import fr.onnoff.hauntfall.ui.theme.HauntfallTheme

/**
 * Point d'entrée Compose commun aux deux plateformes.
 * Android : appelé par MainActivity via setContent { App() }.
 * iOS    : appelé par MainViewController via ComposeUIViewController { App() }.
 */
@Composable
fun App() {
    HauntfallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GameScreen()
        }
    }
}
