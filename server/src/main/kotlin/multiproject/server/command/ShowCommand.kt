package multiproject.server.command

import multiproject.lib.dto.command.CommandResult
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Show command
 *
 * @constructor Create empty Show command
 */
open class ShowCommand: Command() {
    override val description: String = "Show items in collection"
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        return CommandResult(collection.toString())
    }
}