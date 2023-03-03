package lab5kotlin.console

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity

class CollectionPrinter {

    fun <T : Collection<M>, M : Entity> print(collection: T) {
        if (collection.items.isNotEmpty())
            collection.items.map {
                this.printItem(it)
            }
        else
            println("Collection is empty")
    }

    fun <T : Entity> printItem(collectionItem: T) {
        print(collectionItem.toString());
    }
}