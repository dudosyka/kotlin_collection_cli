package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.exceptions.FileDumpException
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Save command
 *
 * @constructor Create empty Save command
 */
class SaveCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        return try {
            this.collection.dump()
            CommandResult("Collection is successfully dumped!")
        } catch (e: FileDumpException) {
            CommandResult(e.message, false)
        }
    }
}