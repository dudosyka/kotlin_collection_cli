package multiproject.server.command

import multiproject.lib.dto.command.Validator
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Update command
 *
 * @constructor Create empty Update command
 */
class UpdateCommand : AddCommand() {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: MutableMap<String, CommandArgumentDto> = entityBuilder.fields.toMutableMap()
    override val description: String = "Update element with specified id"
    init {
        fields["id"] = CommandArgumentDto(name = "id", index = 0, type = FieldType.INT, inline = true)
    }

    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val id = this.getArgument(args, "id", 0, Validator(
            CommandArgumentDto(name = "id", type = FieldType.INT, required = true)
        )
        )
        collection.checkIdExists(id as Int)
        val entity = this.entityBuilder.build(data)
        collection.update(id, entity)

        return CommandResult("Item successfully updated.")
    }
}