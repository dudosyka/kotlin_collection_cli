package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.lib.dto.command.CommandArgumentDto
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
    override val fields: Map<String, CommandArgumentDto> = entityBuilder.fields
    override val description = "Adds new item to collection."

    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult? {
        val entity = this.entityBuilder.build(data)
        collection.addItem(entity)
        return CommandResult("Item successfully created.")
    }

}