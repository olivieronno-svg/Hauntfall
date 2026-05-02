package fr.onnoff.hauntfall.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.onnoff.hauntfall.ui.theme.Ectoplasme
import fr.onnoff.hauntfall.ui.theme.MauveNuit
import fr.onnoff.hauntfall.ui.theme.MauveProfond
import fr.onnoff.hauntfall.ui.theme.NuitProfonde
import fr.onnoff.hauntfall.ui.theme.OrManoir
import fr.onnoff.hauntfall.ui.theme.OrPale
import fr.onnoff.hauntfall.ui.theme.PoussiereSpectrale

/**
 * Overlay plein écran affiché à la fin de partie.
 * Avale les clics pour ne pas laisser le joueur interagir avec la grille
 * en dessous tant qu'il n'a pas cliqué "Rejouer".
 */
@Composable
fun GameOverOverlay(
    score: Int,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noopInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NuitProfonde.copy(alpha = 0.88f))
            .clickable(
                interactionSource = noopInteraction,
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MauveProfond, MauveNuit)
                    )
                )
                .border(1.5.dp, OrManoir.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Le Manoir s'est figé",
                color = OrManoir,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Plus aucune fusion possible",
                color = PoussiereSpectrale,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Score final",
                color = OrPale.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "$score",
                color = Ectoplasme,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onReplay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrManoir,
                    contentColor = NuitProfonde
                )
            ) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Rejouer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
