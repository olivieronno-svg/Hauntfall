package fr.onnoff.hauntfall.game.model

/**
 * Coordonnée d'une case sur la grille. (0, 0) = coin haut-gauche.
 */
data class GridPos(val row: Int, val col: Int) {
    override fun toString(): String = "($row,$col)"
}
