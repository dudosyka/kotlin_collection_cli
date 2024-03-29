package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Count by number of rooms command
 *
 * @constructor Create empty Count by number of rooms command
 */
class CountByNumberOfRoomsCommand : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        val numberOfRooms = this.getArgument(args, "Number of rooms", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))

        return CommandResult(collection.countBy(numberOfRooms as Int).toString())
}

}
