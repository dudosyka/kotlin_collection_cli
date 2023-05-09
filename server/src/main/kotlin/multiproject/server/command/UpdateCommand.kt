package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Update command
 *
 * @constructor Create empty Update command
 */
class UpdateCommand(controller: Controller) : AddCommand(controller) {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: MutableMap<String, CommandArgumentDto> = entityBuilder.fields.toMutableMap()
    override val description: String = "Update element with specified id"
    init {
        fields["id"] = CommandArgumentDto(name = "id", index = 0, type = FieldType.INT, inline = true)
    }

    override fun execute(input: ExecutableInput): Response {
        val id = this.getArgument(input.args, "id", 0, Validator(
            CommandArgumentDto(name = "id", type = FieldType.INT, required = true)
        )
        )
        collection.checkIdExists(id as Int)
        val entity = this.entityBuilder.build(input.data)
        collection.update(id, entity)

        return Response(ResponseCode.SUCCESS, "Item successfully updated.")
    }
}