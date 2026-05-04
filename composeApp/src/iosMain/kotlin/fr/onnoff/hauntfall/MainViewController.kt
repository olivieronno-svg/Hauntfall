package fr.onnoff.hauntfall

import androidx.compose.ui.window.ComposeUIViewController

/**
 * Point d'entrée iOS : retourne un UIViewController qui héberge le Composable [App].
 * Appelé depuis Swift dans iosApp/iosApp/ContentView.swift.
 */
fun MainViewController() = ComposeUIViewController { App() }
