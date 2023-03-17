package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Remove by id command
 *
 * @constructor Create empty Remove by id command
 */
class RemoveByIdCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        val id = this.getArgument(args, "id", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))
        collection.removeById(id as Int)
        return CommandResult("Item with id = $id successfully removed!")
    }
}