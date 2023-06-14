package multiproject.server.dump

import multiproject.server.collection.item.Entity

/**
 * Dump manager
 *
 * @param T
 * @constructor Create empty Dump manager
 */
abstract class DumpManager <T: Entity> {
    /**
     * Load dump
     *
     * @return
     */
    abstract suspend fun loadDump(): MutableList<T>

    /**
     * Dump
     *
     * @param items
     */
    abstract suspend fun dump(items: MutableList<T>): Boolean

    abstract suspend fun dumpOnly(removedItems: MutableList<Int> = mutableListOf(), updatedItems: MutableList<T> = mutableListOf()): MutableList<T>
}