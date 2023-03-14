package lab5kotlin.dump

import lab5kotlin.collection.item.Entity

abstract class DumpManager <T: Entity> {
    abstract fun loadDump(): MutableList<T>

    abstract fun dump(items: MutableList<T>)
}