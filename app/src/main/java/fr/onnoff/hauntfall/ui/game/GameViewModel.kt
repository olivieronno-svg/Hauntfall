package fr.onnoff.hauntfall.ui.game

import androidx.lifecycle.ViewModel
import fr.onnoff.hauntfall.game.engine.FusionEngine
import fr.onnoff.hauntfall.game.model.Grid
import fr.onnoff.hauntfall.game.model.GridPos
import fr.onnoff.hauntfall.game.model.ItemIdSource
import fr.onnoff.hauntfall.game.model.ItemType
import fr.onnoff.hauntfall.game.model.MergeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel de la partie courante.
 *
 * J4 : la grille répond aux drag & drop comme avant (move ou swap selon
 * que la cible est vide ou occupée), puis le moteur de fusion est invoqué
 * sur les positions affectées. Cascade automatique gérée par [FusionEngine].
 */
class GameViewModel : ViewModel() {

    private val ids = ItemIdSource()

    private val _grid = MutableStateFlow(initialGrid())
    val grid = _grid.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    /**
     * Déplace ou échange un item, puis applique les fusions résultantes.
     */
    fun onMove(from: GridPos, to: GridPos) {
        if (from == to) return
        val current = _grid.value
        val source = current[from] ?: return
        val target = current[to]

        // 1. Move ou swap.
        val afterMove = if (target == null) current.move(from, to) else current.swap(from, to)

        // 2. Fusion : on vérifie d'abord la position de chute (intention du joueur),
        //    puis la source si un swap a placé un item là.
        val secondary = if (target != null) from else null
        val result = FusionEngine.applyAfterMove(afterMove, primary = to, secondary = secondary, ids = ids)

        _grid.value = result.grid
        _score.value = _score.value + result.scoreGained
    }

    /** Réinitialise la grille avec une population de démo + remet le score à zéro. */
    fun reset() {
        _grid.value = initialGrid()
        _score.value = 0
    }

    /**
     * Population de démo J4 : items pondérés vers les bas paliers pour favoriser
     * la création de groupes de 3+ et tester la fusion / cascade.
     */
    private fun initialGrid(): Grid {
        var grid = Grid.empty()
        val population = listOf(
            ItemType.RELIQUE to 1,
            ItemType.LUSTRE_MAUDIT to 1,
            ItemType.LUSTRE_FANTOME to 2,
            ItemType.CHANDELIER to 5,
            ItemType.BOUGIE to 9
        )
        val empties = grid.emptyPositions().shuffled()
        var idx = 0
        for ((type, count) in population) {
            repeat(count) {
                if (idx < empties.size) {
                    grid = grid.set(empties[idx], MergeItem(ids.next(), type))
                    idx++
                }
            }
        }
        return grid
    }
}
