package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Remove by id command
 *
 * @constructor Create empty Remove by id command
 */
class RemoveByIdCommand(controller: Controller) : Command(controller) {
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
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(
            sync = false,
            blocking = true,
            blockByArgument = 0
        )

    override fun execute(input: ExecutableInput): Response {
        val id = this.getArgument(input.args, "id", 0,
            Validator(
                CommandArgumentDto(name = "id", type = FieldType.INT, required = true)
            )
        )
        collection.removeById(id as Int)
        return Response( ResponseCode.SUCCESS,"Item with id = $id successfully removed!")
    }
}