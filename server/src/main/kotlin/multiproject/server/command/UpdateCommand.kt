package multiproject.server.command

import multiproject.lib.dto.command.*
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.time.ZonedDateTime

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

    override suspend fun execute(input: ExecutableInput): Response {
        val id = this.getArgument(input.args, "id", 0, Validator(
            CommandArgumentDto(name = "id", type = FieldType.INT, required = true)
        )
        ).toString().toInt()

        val sync = input.request.getSyncHelper()
        if (sync.removedInstances.contains(id.toLong()))
            return Response(ResponseCode.ITEM_NOT_FOUND, "Item not found!")

        val entity = this.entityBuilder.build(input.data)
        val result = collection.update(id, entity)

        return if (result)
            Response(ResponseCode.SUCCESS, "Item successfully updated.", commits = listOf(
                CommitDto(
                    id = id.toLong(),
                    timestamp = ZonedDateTime.now().toEpochSecond(),
                    data = input.data
                )
            ))
        else
            Response(ResponseCode.ITEM_NOT_FOUND, "Item can`t be updated")
    }
}