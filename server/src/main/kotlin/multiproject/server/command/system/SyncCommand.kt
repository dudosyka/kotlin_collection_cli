package multiproject.server.command.system

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

class SyncCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(sync = true)
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        collection.loadDump()
        return Response(ResponseCode.SUCCESS, "Successfully synchronized")
    }
}