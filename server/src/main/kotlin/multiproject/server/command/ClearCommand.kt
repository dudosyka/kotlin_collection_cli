package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Clear command
 *
 * @constructor Create empty Clear command
 */
class ClearCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        collection.clear()
        return CommandResult("Collection is successfully cleared!")
    }
}