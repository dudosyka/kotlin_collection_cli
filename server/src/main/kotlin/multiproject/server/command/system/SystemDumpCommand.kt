package multiproject.server.command.system

import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.server.command.Command
import multiproject.lib.dto.command.CommandResult
import multiproject.lib.exceptions.FileDumpException
import multiproject.lib.udp.ServerUdpChannel
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.command.CommandResolver
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class SystemDumpCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val hideFromClient: Boolean = true
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        ServerUdpChannel.removeDisconnected(CommandResolver.author!!)
        return try {
            this.collection.dump()
            CommandResult("Collection is successfully dumped!", true, ResponseDto(ResponseCode.SUCCESS, ""))
        } catch (e: FileDumpException) {
            CommandResult(e.message, false, ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, e.message))
        }
    }
}