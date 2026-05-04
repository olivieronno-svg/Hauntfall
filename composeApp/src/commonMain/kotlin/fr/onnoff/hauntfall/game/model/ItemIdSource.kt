package fr.onnoff.hauntfall.game.model

import java.util.concurrent.atomic.AtomicLong

/**
 * Générateur d'ids uniques pour les [MergeItem]. Thread-safe.
 *
 * Une seule instance par partie : chaque nouvel item (spawn ou produit
 * d'une fusion) reçoit un id frais.
 */
class ItemIdSource(initial: Long = 1L) {
    private val counter = AtomicLong(initial)
    fun next(): Long = counter.getAndIncrement()
    fun peek(): Long = counter.get()
}
