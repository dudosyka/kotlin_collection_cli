package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldType
import multiproject.server.collection.item.Validator
import multiproject.udpsocket.dto.command.CommandArgumentDto
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
    override val fields: Map<String, CommandArgumentDto> = entityBuilder.fields
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val id = this.getArgument(args, "id", 0, Validator(
            CommandArgumentDto(name = "id", type = multiproject.udpsocket.dto.command.FieldType.INT, required = true)
        ))
        collection.checkIdExists(id as Int)
        val entity = this.entityBuilder.build(data)
        collection.update(id, entity)

        return CommandResult("Item successfully updated.")
    }
}