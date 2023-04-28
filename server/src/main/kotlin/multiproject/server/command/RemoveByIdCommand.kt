package multiproject.server.command

import multiproject.lib.dto.command.Validator
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Remove by id command
 *
 * @constructor Create empty Remove by id command
 */
class RemoveByIdCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "id" to CommandArgumentDto(
            name = "id",
            inline = true,
            required = true,
            index = 0,
            type = FieldType.INT,
        )
    )
    override val description: String = "Remove element with specified id"

    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val id = this.getArgument(args, "id", 0, Validator(
            CommandArgumentDto(name = "id", type = FieldType.INT, required = true)
        )
        )
        collection.removeById(id as Int)
        return CommandResult("Item with id = $id successfully removed!")
    }
}