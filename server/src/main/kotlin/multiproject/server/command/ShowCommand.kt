package multiproject.server.command

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Show command
 *
 * @constructor Create empty Show command
 */
open class ShowCommand(controller: Controller) : Command(controller) {
    override val description: String = "Show items in collection"
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(true)
    override suspend fun execute(input: ExecutableInput): Response {
        return Response(ResponseCode.SUCCESS, collection.toString())
    }
}