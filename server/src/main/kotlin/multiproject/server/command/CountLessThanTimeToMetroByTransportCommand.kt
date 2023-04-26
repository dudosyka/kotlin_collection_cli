package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.FieldType
import multiproject.server.collection.item.Validator
import multiproject.udpsocket.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Count less than time to metro by transport command
 *
 * @constructor Create empty Count less than time to metro by transport command
 */
class CountLessThanTimeToMetroByTransportCommand : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val timeToMetro = this.getArgument(args, "Time to metro", 0, Validator(
            CommandArgumentDto(name = "time_to_metro", type = multiproject.udpsocket.dto.command.FieldType.INT, required = true)
        ))
        return CommandResult(collection.countLessThanBy(timeToMetro as Int).toString())
    }

}
