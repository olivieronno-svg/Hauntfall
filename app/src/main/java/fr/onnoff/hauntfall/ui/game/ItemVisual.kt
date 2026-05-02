package fr.onnoff.hauntfall.ui.game

import androidx.compose.ui.graphics.Color
import fr.onnoff.hauntfall.game.model.ItemType

/**
 * Mapping de présentation : un [ItemType] vers son rendu visuel.
 *
 * Cette couche est isolée du modèle (ui/ ne dépend de game/model/ que dans
 * un sens). Si on ajoute des skins ou un thème saisonnier, on n'aura qu'à
 * remplacer ce mapping.
 */
data class ItemVisual(
    val emoji: String,
    val tierLabel: String,
    val bgColor: Color,
    val borderColor: Color,
    val labelColor: Color
) {
    companion object {
        fun of(type: ItemType): ItemVisual = when (type) {
            ItemType.BOUGIE -> ItemVisual(
                emoji = "🕯",
                tierLabel = "I",
                bgColor = Color(0xFFFFE082),
                borderColor = Color(0xFFFFCA28),
                labelColor = Color(0xFF5D4037)
            )
            ItemType.CHANDELIER -> ItemVisual(
                emoji = "🏮",
                tierLabel = "II",
                bgColor = Color(0xFFFFB74D),
                borderColor = Color(0xFFFB8C00),
                labelColor = Color(0xFF4E342E)
            )
            ItemType.LUSTRE_FANTOME -> ItemVisual(
                emoji = "👻",
                tierLabel = "III",
                bgColor = Color(0xFF6BD9A8),
                borderColor = Color(0xFF26A69A),
                labelColor = Color(0xFF1A1325)
            )
            ItemType.LUSTRE_MAUDIT -> ItemVisual(
                emoji = "🔮",
                tierLabel = "IV",
                bgColor = Color(0xFFCE93D8),
                borderColor = Color(0xFF8E24AA),
                labelColor = Color(0xFF1A1325)
            )
            ItemType.RELIQUE -> ItemVisual(
                emoji = "💎",
                tierLabel = "V",
                bgColor = Color(0xFFD4AF37),
                borderColor = Color(0xFFFFD54F),
                labelColor = Color(0xFF1A1325)
            )
        }
    }
}
