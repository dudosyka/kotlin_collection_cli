package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.entities.flat.Flat
import multiproject.server.entities.flat.RoomsComparator
import multiproject.lib.dto.command.CommandArgumentDto
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
    override val fields: Map<String, CommandArgumentDto> = entityBuilder.fields
    override val description: String = "Adds new element if number of rooms grater than max of current items"
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult? {
        val entity = this.entityBuilder.build(data)
        val result = collection.addIfMax(RoomsComparator(), entity)

        return if (!result) {
            CommandResult("Failed! number of rooms is lower than max in collection", false)
        } else {
            CommandResult("Item successfully created")
        }
    }
}