package fr.onnoff.hauntfall.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import fr.onnoff.hauntfall.game.model.Grid
import fr.onnoff.hauntfall.game.model.GridPos
import fr.onnoff.hauntfall.ui.theme.MauveNuit
import fr.onnoff.hauntfall.ui.theme.OrManoir

/**
 * Grille 6×6 (par défaut) avec drag & drop. Le drag déplace l'item d'une
 * case vers une autre. Si la cible est vide → déplacement, sinon → swap.
 *
 * Détails techniques :
 * - Le pointer input est posé sur le Box englobant : un seul handler
 *   gère tout le drag (plus simple que par cellule).
 * - On suit la taille du Box (`onSizeChanged`) pour convertir un
 *   offset pixel en `GridPos` (division par cellSize).
 * - Pendant le drag, la case source est rendue vide et un overlay
 *   flottant (le même `MergeCell`) suit le doigt avec une légère
 *   élévation visuelle.
 * - `rememberUpdatedState` + lecture fraîche de `gridSizePx` à l'intérieur
 *   du coroutine `pointerInput` évitent le bug de "stale closure" : sans ça,
 *   la lambda capture la taille initiale (zéro) et le drag ne démarre jamais.
 */
@Composable
fun MergeGrid(
    grid: Grid,
    onMove: (from: GridPos, to: GridPos) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var gridSizePx by remember { mutableStateOf(IntSize.Zero) }
    var dragFrom by remember { mutableStateOf<GridPos?>(null) }
    var dragPosPx by remember { mutableStateOf(Offset.Zero) }

    // Évitent les captures stale dans le coroutine pointerInput.
    val currentGrid by rememberUpdatedState(grid)
    val currentOnMove by rememberUpdatedState(onMove)

    // Pour le rendu (recompose à chaque changement) : OK d'utiliser des locals.
    val cellWidthPx = if (gridSizePx.width > 0) gridSizePx.width.toFloat() / grid.cols else 0f
    val cellHeightPx = if (gridSizePx.height > 0) gridSizePx.height.toFloat() / grid.rows else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MauveNuit.copy(alpha = 0.6f))
            .border(1.5.dp, OrManoir.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(4.dp)
            .onSizeChanged { gridSizePx = it }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val sz = gridSizePx
                        if (sz.width <= 0 || sz.height <= 0) return@detectDragGestures
                        val cellW = sz.width.toFloat() / currentGrid.cols
                        val cellH = sz.height.toFloat() / currentGrid.rows
                        if (offset.x < 0f || offset.y < 0f) return@detectDragGestures
                        if (offset.x >= sz.width || offset.y >= sz.height) return@detectDragGestures
                        val pos = GridPos(
                            (offset.y / cellH).toInt(),
                            (offset.x / cellW).toInt()
                        )
                        if (currentGrid.isInBounds(pos) && currentGrid[pos] != null) {
                            dragFrom = pos
                            dragPosPx = offset
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        if (dragFrom != null) {
                            dragPosPx = change.position
                        }
                    },
                    onDragEnd = {
                        val from = dragFrom
                        if (from != null) {
                            val sz = gridSizePx
                            if (sz.width > 0 && sz.height > 0) {
                                val cellW = sz.width.toFloat() / currentGrid.cols
                                val cellH = sz.height.toFloat() / currentGrid.rows
                                val o = dragPosPx
                                if (o.x >= 0f && o.x < sz.width.toFloat() &&
                                    o.y >= 0f && o.y < sz.height.toFloat()
                                ) {
                                    val to = GridPos(
                                        (o.y / cellH).toInt(),
                                        (o.x / cellW).toInt()
                                    )
                                    if (currentGrid.isInBounds(to) && to != from) {
                                        currentOnMove(from, to)
                                    }
                                }
                            }
                        }
                        dragFrom = null
                    },
                    onDragCancel = {
                        dragFrom = null
                    }
                )
            }
    ) {
        // Grille de cellules
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until grid.rows) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                ) {
                    for (c in 0 until grid.cols) {
                        val pos = GridPos(r, c)
                        val item = grid[pos]
                        val isSource = pos == dragFrom
                        MergeCell(
                            item = if (isSource) null else item,
                            isDraggedSource = isSource,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }

        // Overlay : item "fantôme" qui suit le doigt pendant le drag
        val draggedItem = dragFrom?.let { grid[it] }
        if (draggedItem != null && cellWidthPx > 0f) {
            val sizeDp = with(density) { cellWidthPx.toDp() }
            val offsetXDp = with(density) { (dragPosPx.x - cellWidthPx / 2f).toDp() }
            val offsetYDp = with(density) { (dragPosPx.y - cellHeightPx / 2f).toDp() }
            Box(
                modifier = Modifier
                    .offset(offsetXDp, offsetYDp)
                    .size(sizeDp)
                    .padding(2.dp)
            ) {
                MergeCell(
                    item = draggedItem,
                    isElevated = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
