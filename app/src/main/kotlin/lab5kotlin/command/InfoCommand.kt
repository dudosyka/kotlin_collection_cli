package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Info command
 *
 * @constructor Create empty Info command
 */
class InfoCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        return CommandResult(collection.getInfo().toString())
    }
}