package multiproject.server.command

import multiproject.lib.dto.command.CommandResult
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.sort.CollectionSortType
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
    override val description: String = "Sort items and show collection"
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        this.collection.sort(CollectionSortType.ASC)
        return CommandResult(collection.toString())
    }
}