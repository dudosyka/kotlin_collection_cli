package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.CollectionPrinter
import lab5kotlin.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Show command
 *
 * @constructor Create empty Show command
 */
open class ShowCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val collectionPrinter = CollectionPrinter()
    override fun execute(args: List<String>): Boolean {
        collectionPrinter.print(collection.getAll())
        return true
    }
}