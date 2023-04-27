package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.Validator
import multiproject.udpsocket.dto.command.CommandArgumentDto
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
            required = true,
            index = 0,
            type = multiproject.udpsocket.dto.command.FieldType.INT,
        )
    )

    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val id = this.getArgument(args, "id", 0, Validator(
            CommandArgumentDto(name = "id", type = multiproject.udpsocket.dto.command.FieldType.INT, required = true)
        ))
        collection.removeById(id as Int)
        return CommandResult("Item with id = $id successfully removed!")
    }
}