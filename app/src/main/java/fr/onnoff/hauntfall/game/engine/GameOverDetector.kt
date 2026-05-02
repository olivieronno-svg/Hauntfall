package fr.onnoff.hauntfall.game.engine

import fr.onnoff.hauntfall.game.model.Grid
import fr.onnoff.hauntfall.game.model.GridPos

/**
 * Détecte les conditions de fin de partie.
 *
 * Le jeu est perdu quand :
 *  1. La grille est pleine (donc plus aucune place pour drag-to-empty), ET
 *  2. Aucun swap (échange entre 2 cases de types différents) ne peut produire
 *     un groupe connexe de 3+ items du même palier.
 *
 * Tant qu'un seul swap productif existe, la partie continue.
 *
 * Algorithmique : O(N⁴) au pire (N² paires × O(N²) BFS) mais N=36 sur grille
 * 6x6 → ≈ 1,7M opérations, donc < 50 ms sur n'importe quel téléphone moderne.
 */
object GameOverDetector {

    /** True ssi la partie est perdue (cf. règle ci-dessus). */
    fun isGameOver(grid: Grid): Boolean {
        if (!grid.isFull()) return false

        for (ra in 0 until grid.rows) {
            for (ca in 0 until grid.cols) {
                val pa = GridPos(ra, ca)
                val ta = grid[pa]?.type ?: continue
                for (rb in 0 until grid.rows) {
                    for (cb in 0 until grid.cols) {
                        val pb = GridPos(rb, cb)
                        if (pa == pb) continue
                        val tb = grid[pb]?.type ?: continue
                        if (tb == ta) continue  // swap de même type = identité

                        val swapped = grid.swap(pa, pb)

                        // Fusion possible à pa (qui contient maintenant tb) ?
                        if (!tb.isFinalTier) {
                            val group = FusionEngine.connectedSameType(swapped, pa, tb)
                            if (group.size >= 3) return false
                        }
                        // Fusion possible à pb (qui contient maintenant ta) ?
                        if (!ta.isFinalTier) {
                            val group = FusionEngine.connectedSameType(swapped, pb, ta)
                            if (group.size >= 3) return false
                        }
                    }
                }
            }
        }
        return true
    }
}
