package multiproject.server.command.system

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.server.command.Command
import multiproject.lib.dto.command.CommandResult
import multiproject.lib.exceptions.FileDumpException
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.command.CommandResolver
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class SystemDumpCommand: Command() {
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    private val server: ServerUdpChannel by inject(ServerUdpChannel::class.java, named("server"))
    override val hideFromClient: Boolean = true
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        server.disconnect(CommandResolver.author!!)
        return try {
            this.collection.dump()
            CommandResult("Collection is successfully dumped!", true, ResponseDto(ResponseCode.SUCCESS, ""))
        } catch (e: FileDumpException) {
            CommandResult(e.message, false, ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, e.message))
        }
    }
}