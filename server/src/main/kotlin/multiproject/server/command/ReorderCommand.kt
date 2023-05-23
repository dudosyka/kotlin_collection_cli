package multiproject.server.command

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.sort.CollectionSortType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Reorder command
 *
 * @constructor Create empty Reorder command
 */
class ReorderCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(
        Collection::class.java,
        named("collection")
    )
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(true)

    override val description: String = "Sort items and show collection"
    override suspend fun execute(input: ExecutableInput): Response {
        return Response(ResponseCode.SUCCESS, this.collection.sort(CollectionSortType.ASC))
    }
}