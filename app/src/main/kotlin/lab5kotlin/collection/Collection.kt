package lab5kotlin.collection

import lab5kotlin.collection.item.CollectionItem

class Collection<T : CollectionItem> {
    val items: MutableCollection<T>

    init {
        this.items = mutableListOf()
    }

    fun getItem(index: Int): T {
        return this.items.elementAt(index)
    }

    fun addItem(collectionItem: T) {
        this.items.add(collectionItem)
    }
}