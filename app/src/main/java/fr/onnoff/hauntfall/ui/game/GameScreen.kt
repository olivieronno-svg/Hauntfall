package fr.onnoff.hauntfall.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.onnoff.hauntfall.R
import fr.onnoff.hauntfall.ui.theme.Ectoplasme
import fr.onnoff.hauntfall.ui.theme.MauveNuit
import fr.onnoff.hauntfall.ui.theme.NuitProfonde
import fr.onnoff.hauntfall.ui.theme.OrManoir
import fr.onnoff.hauntfall.ui.theme.PoussiereSpectrale

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val grid by viewModel.grid.collectAsState()
    val score by viewModel.score.collectAsState()
    val gameOver by viewModel.gameOver.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Photo de fond : manoir hanté
        Image(
            painter = painterResource(id = R.drawable.bg_manor),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // 2. Voile mauve nuit assombri pour garder la lisibilité de la grille
        //    et du texte. Plus dense en haut/bas, plus transparent au milieu
        //    où la photo dialogue avec la grille.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NuitProfonde.copy(alpha = 0.85f),
                            NuitProfonde.copy(alpha = 0.55f),
                            MauveNuit.copy(alpha = 0.45f),
                            NuitProfonde.copy(alpha = 0.75f),
                            NuitProfonde.copy(alpha = 0.92f)
                        )
                    )
                )
        )
        // 3. Contenu de jeu par-dessus
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hauntfall",
                    color = OrManoir,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score : $score",
                    color = Ectoplasme,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Glisse les items pour les déplacer",
                color = PoussiereSpectrale,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Grille de jeu
            MergeGrid(
                grid = grid,
                onMove = viewModel::onMove
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Légende ladder : la chaîne de fusion visible
            TierLadder()

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = viewModel::reset
            ) {
                Text(
                    text = "Nouvelle partie",
                    color = OrManoir
                )
            }
        }

        // Overlay fin de partie (par-dessus tout)
        if (gameOver) {
            GameOverOverlay(
                score = score,
                onReplay = viewModel::reset
            )
        }
    }
}

/**
 * Affiche la chaîne de fusion sous la grille : 5 cartes (une par palier)
 * séparées par des flèches → pour montrer clairement la progression
 * 🕯 → 🏮 → 👻 → 🔮 → 💎.
 */
@Composable
private fun TierLadder() {
    val types = fr.onnoff.hauntfall.game.model.ItemType.entries
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEachIndexed { i, type ->
            TierBadge(type)
            if (i < types.size - 1) {
                Text(
                    text = "›",
                    color = OrManoir.copy(alpha = 0.7f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TierBadge(type: fr.onnoff.hauntfall.game.model.ItemType) {
    val v = ItemVisual.of(type)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(v.bgColor, v.bgColor.copy(alpha = 0.85f))
                )
            )
            .border(1.dp, v.borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = v.emoji,
            fontSize = 24.sp
        )
        Text(
            text = v.tierLabel,
            color = v.labelColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
