package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Remove at command
 *
 * @constructor Create empty Remove at command
 */
class RemoveAtCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        val index = this.getArgument(args, "index", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))
        collection.removeAt(index as Int)
        return CommandResult("Item with index $index successfully removed!")
    }
}