package lab5kotlin.collection

import kotlinx.serialization.Serializable
import lab5kotlin.collection.item.Entity

@Serializable
class Collection<T : Entity>(var items: MutableCollection<T>) {
    fun getItem(index: Int): T {
        return this.items.elementAt(index)
    }

    fun addItem(collectionItem: T) {
        this.items.add(collectionItem)
    }
}