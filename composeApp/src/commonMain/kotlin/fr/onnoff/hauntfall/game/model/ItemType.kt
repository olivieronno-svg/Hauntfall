package fr.onnoff.hauntfall.game.model

/**
 * Les 5 paliers de fusion de Hauntfall.
 * Trois items contigus de même palier fusionnent en un item du palier supérieur.
 *
 * Progression de valeur : x4 par palier (1 → 4 → 16 → 64 → 256).
 */
enum class ItemType(
    val displayName: String,
    val baseValue: Int
) {
    BOUGIE("Bougie", 1),
    CHANDELIER("Chandelier", 4),
    LUSTRE_FANTOME("Lustre fantôme", 16),
    LUSTRE_MAUDIT("Lustre maudit", 64),
    RELIQUE("Relique", 256);

    val tier: Int get() = ordinal + 1

    val isFinalTier: Boolean get() = ordinal == entries.lastIndex

    /** Le palier suivant dans la chaîne, ou null si déjà au palier final. */
    fun next(): ItemType? = entries.getOrNull(ordinal + 1)

    companion object {
        const val TIER_COUNT = 5

        /** Items autorisés au spawn aléatoire (J5). On ne fait spawner que les bas paliers. */
        val SPAWNABLE: List<ItemType> = listOf(BOUGIE, BOUGIE, BOUGIE, CHANDELIER)
    }
}
