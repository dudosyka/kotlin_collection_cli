package lab5kotlin.command

import lab5kotlin.collection.Collection
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
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        this.collection.sort(CollectionSortType.ASC)
        return CommandResult(collection.toString())
    }
}