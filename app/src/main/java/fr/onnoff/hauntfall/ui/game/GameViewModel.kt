package fr.onnoff.hauntfall.ui.game

import androidx.lifecycle.ViewModel
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
 * Pour J3 : la grille est juste éditable par drag & drop (déplacement /
 * échange). La logique de fusion arrivera en J4.
 */
class GameViewModel : ViewModel() {

    private val ids = ItemIdSource()

    private val _grid = MutableStateFlow(initialGrid())
    val grid = _grid.asStateFlow()

    /**
     * Déplace l'item de [from] vers [to].
     * - Si [to] est vide → déplacement simple
     * - Si [to] est occupé → swap (J4 transformera ceci en fusion si types
     *   identiques et conditions remplies)
     */
    fun onMove(from: GridPos, to: GridPos) {
        val current = _grid.value
        val target = current[to]
        _grid.value = if (target == null) current.move(from, to) else current.swap(from, to)
    }

    /** Réinitialise la grille avec une population de démo. */
    fun reset() {
        _grid.value = initialGrid()
    }

    /**
     * Population de démo J3 : un item de chaque palier pour valider visuellement
     * que les 5 types s'affichent correctement, puis remplissage de bougies et
     * chandeliers pour avoir de quoi tester le drag.
     */
    private fun initialGrid(): Grid {
        var grid = Grid.empty()
        val population = listOf(
            ItemType.RELIQUE to 1,
            ItemType.LUSTRE_MAUDIT to 2,
            ItemType.LUSTRE_FANTOME to 3,
            ItemType.CHANDELIER to 4,
            ItemType.BOUGIE to 6
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
