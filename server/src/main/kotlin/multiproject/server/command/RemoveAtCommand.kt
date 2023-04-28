package multiproject.server.command

import multiproject.lib.dto.command.Validator
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
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
            inline = true,
            required = true,
            index = 0,
            type = FieldType.INT,
        )
    )
    override val description: String = "Remove element on specified index"
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val index = this.getArgument(args, "index", 0, Validator(
            CommandArgumentDto(name = "index", type = FieldType.INT, required = true)
        )
        )

        collection.removeAt(index as Int)
        return CommandResult("Item with index $index successfully removed!")
    }
}