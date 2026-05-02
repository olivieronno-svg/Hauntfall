package fr.onnoff.hauntfall.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.onnoff.hauntfall.game.model.MergeItem
import fr.onnoff.hauntfall.ui.theme.MauveProfond
import fr.onnoff.hauntfall.ui.theme.NuitProfonde
import fr.onnoff.hauntfall.ui.theme.PoussiereSpectrale

/**
 * Une case de la grille. Affiche un [item] s'il y en a un, sinon une case
 * vide stylisée. La case "fantôme" pendant un drag est gérée en passant
 * `item = null` et `isDraggedSource = true`.
 */
@Composable
fun MergeCell(
    item: MergeItem?,
    modifier: Modifier = Modifier,
    isDraggedSource: Boolean = false,
    isElevated: Boolean = false
) {
    val shape = RoundedCornerShape(10.dp)

    if (item == null) {
        // Case vide (ou case source pendant un drag)
        Box(
            modifier = modifier
                .clip(shape)
                .background(
                    if (isDraggedSource) MauveProfond.copy(alpha = 0.15f)
                    else MauveProfond.copy(alpha = 0.35f)
                )
                .border(
                    1.dp,
                    PoussiereSpectrale.copy(alpha = 0.25f),
                    shape
                )
        )
        return
    }

    val visual = ItemVisual.of(item.type)
    val borderWidth = if (isElevated) 2.dp else 1.dp

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        visual.bgColor,
                        visual.bgColor.copy(alpha = 0.85f)
                    )
                )
            )
            .border(borderWidth, visual.borderColor, shape),
        contentAlignment = Alignment.Center
    ) {
        // Emoji central (visuel principal)
        Text(
            text = visual.emoji,
            fontSize = if (isElevated) 36.sp else 32.sp
        )

        // Badge palier en haut à droite
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(3.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(NuitProfonde.copy(alpha = 0.85f))
                .border(0.5.dp, visual.borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = visual.tierLabel,
                color = visual.borderColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
