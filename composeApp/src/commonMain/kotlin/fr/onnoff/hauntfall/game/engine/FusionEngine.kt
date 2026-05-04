package fr.onnoff.hauntfall.game.engine

import fr.onnoff.hauntfall.game.model.Grid
import fr.onnoff.hauntfall.game.model.GridPos
import fr.onnoff.hauntfall.game.model.ItemIdSource
import fr.onnoff.hauntfall.game.model.ItemType
import fr.onnoff.hauntfall.game.model.MergeItem

/**
 * Résultat d'une opération de fusion : la grille modifiée + le score gagné +
 * la liste des fusions appliquées (utile pour les animations / particules).
 */
data class FusionResult(
    val grid: Grid,
    val scoreGained: Int,
    val fusions: List<FusionEvent>
) {
    val hadFusion: Boolean get() = fusions.isNotEmpty()

    companion object {
        fun unchanged(grid: Grid) = FusionResult(grid, 0, emptyList())
    }
}

/**
 * Une fusion individuelle : N items du palier [consumedType] présents aux positions
 * [consumed] ont été fusionnés en un item du palier [producedType] à la position [pivot].
 */
data class FusionEvent(
    val consumed: Set<GridPos>,
    val pivot: GridPos,
    val consumedType: ItemType,
    val producedType: ItemType
)

/**
 * Moteur de fusion 3-contigu de Hauntfall.
 *
 * Règle : 3 items ou plus de même type, formant un groupe orthogonalement connexe,
 * fusionnent en 1 item du palier supérieur, placé à la position [pivot]
 * (= la cellule où l'utilisateur vient d'agir).
 *
 * Cascade automatique : si l'item produit forme à son tour un groupe de 3+, on
 * fusionne à nouveau, ad infinitum. C'est le combo dopaminergique qu'on cherche.
 *
 * Le palier final ([ItemType.RELIQUE]) ne peut pas fusionner (pas de palier
 * suivant), donc 3 reliques côte à côte restent en place.
 *
 * Pure : aucune dépendance Compose ni Android, testable isolément.
 */
object FusionEngine {

    /**
     * Tente une fusion à la position [trigger]. Cascade tant que possible.
     * Si aucune fusion n'est possible (item null, palier final, groupe < 3),
     * renvoie la grille inchangée.
     */
    fun applyAt(grid: Grid, trigger: GridPos, ids: ItemIdSource): FusionResult {
        if (!grid.isInBounds(trigger)) return FusionResult.unchanged(grid)

        var current = grid
        var totalScore = 0
        val events = mutableListOf<FusionEvent>()

        while (true) {
            val item = current[trigger] ?: break
            if (item.type.isFinalTier) break

            val group = connectedSameType(current, trigger, item.type)
            if (group.size < 3) break

            val nextType = item.type.next() ?: break

            // Vide toutes les cellules du groupe sauf le pivot, puis y place l'item supérieur.
            var newGrid = current
            for (pos in group) {
                newGrid = newGrid.set(pos, null)
            }
            newGrid = newGrid.set(trigger, MergeItem(ids.next(), nextType))

            events += FusionEvent(
                consumed = group,
                pivot = trigger,
                consumedType = item.type,
                producedType = nextType
            )
            totalScore += nextType.baseValue
            current = newGrid
            // pivot reste identique : on retente une fusion (cascade) avec le nouvel item.
        }

        return FusionResult(current, totalScore, events)
    }

    /**
     * Applique d'abord une fusion à [primary], puis à [secondary] sur la grille
     * résultante. Utile après un swap : on vérifie les deux extrémités du déplacement.
     */
    fun applyAfterMove(
        grid: Grid,
        primary: GridPos,
        secondary: GridPos?,
        ids: ItemIdSource
    ): FusionResult {
        val first = applyAt(grid, primary, ids)
        if (secondary == null) return first
        val second = applyAt(first.grid, secondary, ids)
        return FusionResult(
            grid = second.grid,
            scoreGained = first.scoreGained + second.scoreGained,
            fusions = first.fusions + second.fusions
        )
    }

    /**
     * Stabilise la grille : scanne TOUTES les positions et déclenche une fusion
     * sur tout groupe connexe de 3+ items du même type non-final, jusqu'à ce
     * que plus aucune fusion ne soit possible.
     *
     * Utilisé après les spawns aléatoires : un nouvel item peut atterrir au
     * milieu d'un cluster existant et créer un groupe de 3+. Sans stabilisation
     * post-spawn, ces groupes s'accumulent et faussent le détecteur de game over.
     */
    fun stabilize(grid: Grid, ids: ItemIdSource): FusionResult {
        var current = grid
        var totalScore = 0
        val events = mutableListOf<FusionEvent>()

        while (true) {
            var pivot: GridPos? = null
            outer@ for (r in 0 until current.rows) {
                for (c in 0 until current.cols) {
                    val pos = GridPos(r, c)
                    val item = current[pos] ?: continue
                    if (item.type.isFinalTier) continue
                    if (connectedSameType(current, pos, item.type).size >= 3) {
                        pivot = pos
                        break@outer
                    }
                }
            }
            val p = pivot ?: break
            val result = applyAt(current, p, ids)
            current = result.grid
            totalScore += result.scoreGained
            events += result.fusions
        }

        return FusionResult(current, totalScore, events)
    }

    /**
     * Composante connexe orthogonale d'items de type [type] contenant [start].
     * Si [start] n'est pas du bon type, renvoie un ensemble vide.
     */
    fun connectedSameType(grid: Grid, start: GridPos, type: ItemType): Set<GridPos> {
        if (grid[start]?.type != type) return emptySet()
        val visited = HashSet<GridPos>()
        val queue = ArrayDeque<GridPos>()
        queue.addLast(start)
        while (queue.isNotEmpty()) {
            val pos = queue.removeFirst()
            if (!visited.add(pos)) continue
            for (n in grid.neighbors(pos)) {
                if (n !in visited && grid[n]?.type == type) {
                    queue.addLast(n)
                }
            }
        }
        return visited
    }
}
