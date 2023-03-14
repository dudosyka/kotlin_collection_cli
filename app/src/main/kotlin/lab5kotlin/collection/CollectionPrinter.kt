package lab5kotlin.collection

import lab5kotlin.collection.item.Entity
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Collection printer
 *
 * @constructor Create empty Collection printer
 */
class CollectionPrinter {
    private val writer: Writer by inject(Writer::class.java, named("writer"))

    /**
     * Print
     *
     * @param M
     * @param items
     */
    fun <M : Entity> print(items: MutableList<M>) {
        if (items.isNotEmpty())
            items.withIndex().map {
                this.printItem(it.index, it.value)
            }
        else
            this.writer.writeLine("Collection is empty")
    }

    /**
     * Print item
     *
     * @param T
     * @param index
     * @param collectionItem
     */
    fun <T : Entity> printItem(index: Int, collectionItem: T) {
        this.writer.writeLine("[Index: ${index}] $collectionItem")
    }
}