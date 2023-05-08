package multiproject.server.command

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
import org.koin.java.KoinJavaComponent

class LoadCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val description: String = "Restore collection from the file"

    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        return try {
            this.collection.loadDump()
            Response(ResponseDto(ResponseCode.SUCCESS, "Collection successfully restored!"))
        } catch (e: FileDumpException) {
            Response(ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, e.message))
        }
    }
}