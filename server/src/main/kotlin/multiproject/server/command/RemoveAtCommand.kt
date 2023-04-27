package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.Validator
import multiproject.udpsocket.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Remove at command
 *
 * @constructor Create empty Remove at command
 */
class RemoveAtCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "index" to CommandArgumentDto(
            name = "index",
            required = true,
            index = 0,
            type = multiproject.udpsocket.dto.command.FieldType.INT,
        )
    )
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val index = this.getArgument(args, "index", 0, Validator(
            CommandArgumentDto(name = "index", type = multiproject.udpsocket.dto.command.FieldType.INT, required = true)
        ))

        collection.removeAt(index as Int)
        return CommandResult("Item with index $index successfully removed!")
    }
}