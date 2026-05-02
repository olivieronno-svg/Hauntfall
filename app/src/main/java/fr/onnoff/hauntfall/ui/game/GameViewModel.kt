package fr.onnoff.hauntfall.ui.game

import androidx.lifecycle.ViewModel
import fr.onnoff.hauntfall.game.engine.FusionEngine
import fr.onnoff.hauntfall.game.engine.GameOverDetector
import fr.onnoff.hauntfall.game.engine.SpawnEngine
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
 * Boucle de jeu (J5) :
 *  1. Le joueur drag → move ou swap
 *  2. Cascade de fusions sur les positions affectées (FusionEngine)
 *  3. Spawn d'1 nouvel item dans une case vide aléatoire (SpawnEngine)
 *  4. Détection game over (GameOverDetector) si la grille est saturée
 */
class GameViewModel : ViewModel() {

    private val ids = ItemIdSource()

    private val _grid = MutableStateFlow(initialGrid())
    val grid = _grid.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _gameOver = MutableStateFlow(false)
    val gameOver = _gameOver.asStateFlow()

    fun onMove(from: GridPos, to: GridPos) {
        if (_gameOver.value) return
        if (from == to) return
        val current = _grid.value
        val source = current[from] ?: return
        val target = current[to]

        // 1. Move ou swap
        val afterMove = if (target == null) current.move(from, to) else current.swap(from, to)

        // 2. Fusion à la position de chute, puis à la source (en cas de swap qui crée une fusion là aussi)
        val secondary = if (target != null) from else null
        val fusionResult = FusionEngine.applyAfterMove(
            afterMove,
            primary = to,
            secondary = secondary,
            ids = ids
        )

        // 3. Spawn de 2 nouveaux items pour densifier la grille (rend le game
        //    over atteignable : fusion 3-group + spawn 2 = 0 net, mais sans
        //    fusion +2 net force le joueur à fusionner activement)
        val withSpawn = SpawnEngine.spawnRandom(fusionResult.grid, ids, count = 2)

        // 4. Stabilisation : un spawn peut créer un groupe de 3+ s'il atterrit
        //    près de doublons existants. Sans ce passage, les groupes s'accumulent
        //    sur la grille et le détecteur de game over est faussé.
        val stabilized = FusionEngine.stabilize(withSpawn, ids)

        // 5. Mise à jour de l'état + check game over
        _grid.value = stabilized.grid
        _score.value = _score.value + fusionResult.scoreGained + stabilized.scoreGained
        _gameOver.value = GameOverDetector.isGameOver(stabilized.grid)
    }

    fun reset() {
        _grid.value = initialGrid()
        _score.value = 0
        _gameOver.value = false
    }

    /**
     * Population de démarrage : ~10 items, laissant 26 cases libres.
     * Le spawn-après-move densifie la grille progressivement → la pression
     * monte naturellement et force le joueur à fusionner pour survivre.
     */
    private fun initialGrid(): Grid {
        var grid = Grid.empty()
        val population = listOf(
            ItemType.LUSTRE_FANTOME to 1,
            ItemType.CHANDELIER to 3,
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
        // Stabilisation : si la randomisation a créé un groupe de 3+ (rare mais
        // possible), on fusionne avant de présenter la grille au joueur.
        return FusionEngine.stabilize(grid, ids).grid
    }
}
