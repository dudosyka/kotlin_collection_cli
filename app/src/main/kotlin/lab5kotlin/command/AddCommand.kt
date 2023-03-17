package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Add command
 *
 * @constructor Create empty Add command
 */
open class AddCommand : Command() {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: Map<String, Validator> = entityBuilder.fields

    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult? {
        val entity = this.entityBuilder.build(data)
        collection.addItem(entity)
        return CommandResult("Item successfully created.")
    }
}