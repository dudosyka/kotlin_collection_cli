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
    abstract fun loadDump(): MutableList<T>

    /**
     * Dump
     *
     * @param items
     */
    abstract fun dump(items: MutableList<T>)
}