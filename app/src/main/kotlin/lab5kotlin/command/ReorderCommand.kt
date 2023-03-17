package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.CollectionPrinter
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.sort.CollectionSortType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Reorder command
 *
 * @constructor Create empty Reorder command
 */
class ReorderCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(
        Collection::class.java,
        named("collection")
    )
    private val collectionPrinter = CollectionPrinter()
    override fun execute(args: List<String>): Boolean {
        this.collection.sort(CollectionSortType.ASC)
        collectionPrinter.print(collection.getAll())
        return true
    }
}