import SwiftUI
import UIKit
import ComposeApp

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Pont vers le Composable App() défini dans commonMain
        // (fr.onnoff.hauntfall.MainViewController dans iosMain).
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
