package fr.onnoff.hauntfall.game.model

/**
 * État immuable de la grille de jeu. Une cellule null = case vide.
 *
 * Toutes les opérations de modification retournent une nouvelle [Grid] :
 * la grille courante est conservée comme état précédent (utile pour annuler,
 * animer les transitions, et garantir des recompositions Compose stables).
 */
data class Grid(
    val rows: Int,
    val cols: Int,
    val cells: List<MergeItem?>
) {
    init {
        require(rows > 0 && cols > 0) { "Dimensions de grille invalides : $rows x $cols" }
        require(cells.size == rows * cols) {
            "Taille cells=${cells.size} incohérente avec $rows x $cols=${rows * cols}"
        }
    }

    val size: Int get() = rows * cols

    fun indexOf(pos: GridPos): Int = pos.row * cols + pos.col

    operator fun get(pos: GridPos): MergeItem? = cells[indexOf(pos)]
    operator fun get(row: Int, col: Int): MergeItem? = cells[row * cols + col]

    /** Renvoie une nouvelle grille avec [item] (peut être null) à la position [pos]. */
    fun set(pos: GridPos, item: MergeItem?): Grid {
        require(isInBounds(pos)) { "Position hors grille : $pos" }
        val newCells = cells.toMutableList()
        newCells[indexOf(pos)] = item
        return copy(cells = newCells)
    }

    /** Déplace l'item de [from] vers [to]. Si [to] est occupé, son contenu est écrasé. */
    fun move(from: GridPos, to: GridPos): Grid {
        if (from == to) return this
        val item = this[from] ?: return this
        return set(from, null).set(to, item)
    }

    /** Échange le contenu de deux cases. */
    fun swap(a: GridPos, b: GridPos): Grid {
        if (a == b) return this
        val itemA = this[a]
        val itemB = this[b]
        return set(a, itemB).set(b, itemA)
    }

    fun isFull(): Boolean = cells.all { it != null }
    fun isEmpty(): Boolean = cells.all { it == null }
    fun count(): Int = cells.count { it != null }

    fun emptyPositions(): List<GridPos> = buildList {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (cells[r * cols + c] == null) add(GridPos(r, c))
            }
        }
    }

    fun positionsOf(type: ItemType): List<GridPos> = buildList {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (cells[r * cols + c]?.type == type) add(GridPos(r, c))
            }
        }
    }

    fun isInBounds(pos: GridPos): Boolean =
        pos.row in 0 until rows && pos.col in 0 until cols

    /** Voisins orthogonaux (haut, bas, gauche, droite) dans les bornes de la grille. */
    fun neighbors(pos: GridPos): List<GridPos> {
        val candidates = listOf(
            GridPos(pos.row - 1, pos.col),
            GridPos(pos.row + 1, pos.col),
            GridPos(pos.row, pos.col - 1),
            GridPos(pos.row, pos.col + 1)
        )
        return candidates.filter { isInBounds(it) }
    }

    /**
     * Itère sur toutes les positions de la grille dans l'ordre row-major.
     * Utile pour le rendu et les algorithmes de fusion.
     */
    fun forEachPosition(action: (GridPos, MergeItem?) -> Unit) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val pos = GridPos(r, c)
                action(pos, cells[r * cols + c])
            }
        }
    }

    companion object {
        const val DEFAULT_ROWS = 6
        const val DEFAULT_COLS = 6

        /** Grille vide aux dimensions par défaut (6x6). */
        fun empty(rows: Int = DEFAULT_ROWS, cols: Int = DEFAULT_COLS): Grid =
            Grid(rows, cols, List(rows * cols) { null })
    }
}
