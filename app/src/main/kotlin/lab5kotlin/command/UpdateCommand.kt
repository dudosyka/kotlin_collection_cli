package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Update command
 *
 * @constructor Create empty Update command
 */
open class UpdateCommand : AddCommand() {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: Map<String, Validator> = entityBuilder.fields
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        val id = this.getArgument(args, "id", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))
        collection.checkIdExists(id as Int)
        val entity = this.entityBuilder.build(data)
        collection.update(id, entity)

        return CommandResult("Item successfully updated.")
    }
}