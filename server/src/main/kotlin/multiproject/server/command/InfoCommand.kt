package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Info command
 *
 * @constructor Create empty Info command
 */
class InfoCommand: Command() {
    override val description: String = "Show information about collection"
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        return CommandResult(collection.getInfo().toString())
    }
}