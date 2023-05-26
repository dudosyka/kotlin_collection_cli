package multiproject.server.command.system

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.exceptions.execution.FileDumpException
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class SystemDumpCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    override val hideFromClient: Boolean = true
    private val logger: Logger by inject(Logger::class.java, named("logger"))
    /**
     * Execute
     *
     * @param input
     * @return
     */
    override suspend fun execute(input: ExecutableInput): Response {
        return try {
            this.collection.dump()
            Response(ResponseCode.SUCCESS, "Collection is successfully dumped!")
        } catch (e: FileDumpException) {
            logger(LogLevel.FATAL, error = e)
            Response(ResponseCode.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}