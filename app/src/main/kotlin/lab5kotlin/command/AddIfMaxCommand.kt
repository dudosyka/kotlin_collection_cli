package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.Validator
import lab5kotlin.flat.Flat
import lab5kotlin.flat.RoomsComparator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Add if max command
 *
 * @constructor Create empty Add if max command
 */
open class AddIfMaxCommand : AddCommand() {
    private val entityBuilder: EntityBuilder<Flat> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: Map<String, Validator> = entityBuilder.fields
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult? {
        val entity = this.entityBuilder.build(data)
        val result = collection.addIfMax(RoomsComparator(), entity)

        return if (!result) {
            CommandResult("Failed! number of rooms is lower than max in collection", false)
        } else {
            CommandResult("Item successfully created")
        }
    }
}