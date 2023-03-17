package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Count less than time to metro by transport command
 *
 * @constructor Create empty Count less than time to metro by transport command
 */
class CountLessThanTimeToMetroByTransportCommand : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        val timeToMetro = this.getArgument(args, "Time to metro", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))

        return CommandResult(collection.countLessThanBy(timeToMetro as Int).toString())
}

}
