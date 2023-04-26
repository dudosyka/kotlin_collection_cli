package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.Validator
import multiproject.udpsocket.dto.command.CommandArgumentDto
import multiproject.udpsocket.dto.command.FieldType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Count by number of rooms command
 *
 * @constructor Create empty Count by number of rooms command
 */
class CountByNumberOfRoomsCommand : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val numberOfRooms = this.getArgument(args, "Number of rooms", 0, Validator(
                CommandArgumentDto(name = "number_of_rooms", type = FieldType.INT, required = true)
        ))

        return CommandResult(collection.countBy(numberOfRooms as Int).toString())
}

}
