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
 * Count less than time to metro by transport command
 *
 * @constructor Create empty Count less than time to metro by transport command
 */
class CountLessThanTimeToMetroByTransportCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(true)

    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "filter" to CommandArgumentDto(
            name = "filter",
            inline = true,
            index = 0,
            required = true,
            type = FieldType.INT,
        )
    )
    override val description: String = "Show number of items which time to metro less than specified"
    override fun execute(input: ExecutableInput): Response {
        val timeToMetro = this.getArgument(input.args, "Time to metro", 0, Validator(
            CommandArgumentDto(name = "time_to_metro", type = FieldType.INT, required = true)
        )
        )
        return Response(ResponseCode.SUCCESS, collection.countLessThanBy(timeToMetro as Int).toString())
    }

}
