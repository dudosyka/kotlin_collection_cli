package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Remove at command
 *
 * @constructor Create empty Remove at command
 */
class RemoveAtCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "index" to CommandArgumentDto(
            name = "index",
            inline = true,
            required = true,
            index = 0,
            type = FieldType.INT,
        )
    )
    override val description: String = "Remove element on specified index"
    override suspend fun execute(input: ExecutableInput): Response {
        val index = this.getArgument(input.args, "index", 0, Validator(
            CommandArgumentDto(name = "index", type = FieldType.INT, required = true)
        )
        )

        val deletedId = collection.removeAt(index as Int, input.request.getHeader("authorizedUserId").toString().toLong())
        input.request.apply {
            this.setSyncHelper(this.getSyncHelper().apply {
                this.removedInstances.add(deletedId)
            })
        }

        return if (deletedId.toInt() != 0)
            Response( ResponseCode.SUCCESS,"Item with index $index successfully removed!")
        else
            Response(ResponseCode.FORBIDDEN, "Item was not removed!")

    }
}