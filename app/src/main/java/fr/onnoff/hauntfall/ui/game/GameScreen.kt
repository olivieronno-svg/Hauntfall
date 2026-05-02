package fr.onnoff.hauntfall.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NuitProfonde, MauveNuit, NuitProfonde)
                )
            )
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

        // Légende des paliers
        TierLegend()

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
}

@Composable
private fun TierLegend() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        fr.onnoff.hauntfall.game.model.ItemType.entries.forEach { type ->
            val v = ItemVisual.of(type)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = v.emoji,
                    fontSize = 14.sp
                )
                Text(
                    text = v.tierLabel,
                    color = v.borderColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp, end = 4.dp)
                )
            }
        }
    }
}
