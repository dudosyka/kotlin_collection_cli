package multiproject.server.command

import multiproject.lib.dto.command.*
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

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

        val removedId = collection.removeAt(index as Int, input.request.getHeader("authorizedUserId").toString().toLong())
        input.request.apply {
            this.setSyncHelper(this.getSyncHelper().apply {
                this.removedInstances.add(removedId)
            })
        }

        return if (removedId.toInt() == -1)
            Response(ResponseCode.ITEM_NOT_FOUND, "Item not found!")
        else if (removedId.toInt() == 0)
            Response(ResponseCode.FORBIDDEN, "Forbidden! You can't delete this item!")
        else
            Response(ResponseCode.SUCCESS,"Item with index $index successfully removed!", commits = listOf(
                CommitDto(
                    id = removedId,
                    data = null,
                    timestamp = ZonedDateTime.now().toEpochSecond()
                )
            ))

    }
}