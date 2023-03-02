package lab5kotlin.console

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.CollectionItem

class CollectionPrinter {

    fun <T : Collection<M>, M : CollectionItem> print(collection: T) {
        if (collection.items.isNotEmpty())
            collection.items.map {
                this.printItem(it)
            }
        else
            println("Collection is empty")
    }

    fun <T : CollectionItem> printItem(collectionItem: T) {
        println("Item <${collectionItem::class.simpleName}> {")
        for (field in collectionItem.fields) {
            println("\t${field.name}: ${collectionItem.values[field.name]}")
        }
        println("}")
    }
}