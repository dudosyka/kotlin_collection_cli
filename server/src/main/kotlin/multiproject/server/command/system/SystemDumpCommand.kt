package multiproject.server.command.system

import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.FileDumpException
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class SystemDumpCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    override val hideFromClient: Boolean = true
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        return try {
            this.collection.dump()
            Response(ResponseDto(ResponseCode.SUCCESS, "Collection is successfully dumped!"))
        } catch (e: FileDumpException) {
            Response(ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, e.message))
        }
    }
}