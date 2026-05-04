package fr.onnoff.hauntfall.game.model

/**
 * Un objet posé sur la grille.
 *
 * L'[id] unique permet de tracker un item à travers les déplacements et fusions
 * pour les animations Compose (sinon deux items de même type seraient
 * indistinguables et casseraient les transitions).
 */
data class MergeItem(
    val id: Long,
    val type: ItemType
) {
    val tier: Int get() = type.tier
    val displayName: String get() = type.displayName
    val baseValue: Int get() = type.baseValue
}
