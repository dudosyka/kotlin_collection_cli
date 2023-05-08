package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Count by number of rooms command
 *
 * @constructor Create empty Count by number of rooms command
 */
class CountByNumberOfRoomsCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "filter" to CommandArgumentDto(
            name = "filter",
            inline = true,
            required = true,
            index = 0,
            type = FieldType.INT,
        )
    )
    override val description: String = "Show number of items which have that number of rooms"
    override fun execute(input: ExecutableInput): Response {
        val numberOfRooms = this.getArgument(input.args, "Number of rooms", 0, Validator(
                CommandArgumentDto(name = "number_of_rooms", type = FieldType.INT, required = true)
        )
        )

        return Response(ResponseDto(ResponseCode.SUCCESS, collection.countBy(numberOfRooms as Int).toString()))
}

}
