package fr.onnoff.hauntfall.game.engine

import fr.onnoff.hauntfall.game.model.Grid
import fr.onnoff.hauntfall.game.model.ItemIdSource
import fr.onnoff.hauntfall.game.model.ItemType
import fr.onnoff.hauntfall.game.model.MergeItem
import kotlin.random.Random

/**
 * Spawn d'items aléatoires dans les cases vides.
 *
 * Règles de pondération (cf. [ItemType.SPAWNABLE]) : on ne spawn jamais
 * de palier ≥ 3, ces items doivent toujours résulter d'une fusion.
 * La distribution actuelle (3/4 Bougie, 1/4 Chandelier) maintient un flux
 * de bas paliers qui force le joueur à fusionner pour libérer de la place.
 */
object SpawnEngine {

    /**
     * Spawn [count] items dans des cases vides choisies au hasard.
     * S'arrête tôt si la grille n'a plus de place.
     */
    fun spawnRandom(
        grid: Grid,
        ids: ItemIdSource,
        count: Int = 1,
        random: Random = Random.Default
    ): Grid {
        var current = grid
        repeat(count) {
            val empties = current.emptyPositions()
            if (empties.isEmpty()) return current
            val pos = empties[random.nextInt(empties.size)]
            val type = ItemType.SPAWNABLE[random.nextInt(ItemType.SPAWNABLE.size)]
            current = current.set(pos, MergeItem(ids.next(), type))
        }
        return current
    }
}
