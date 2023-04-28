package multiproject.server.command

import multiproject.lib.dto.command.Validator
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Count less than time to metro by transport command
 *
 * @constructor Create empty Count less than time to metro by transport command
 */
class CountLessThanTimeToMetroByTransportCommand : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

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
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val timeToMetro = this.getArgument(args, "Time to metro", 0, Validator(
            CommandArgumentDto(name = "time_to_metro", type = FieldType.INT, required = true)
        )
        )
        return CommandResult(collection.countLessThanBy(timeToMetro as Int).toString())
    }

}
