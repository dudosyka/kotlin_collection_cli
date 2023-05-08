package multiproject.server.command

import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Info command
 *
 * @constructor Create empty Info command
 */
class InfoCommand(controller: Controller) : Command(controller) {
    override val description: String = "Show information about collection"
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(input: ExecutableInput): Response {
        return Response(ResponseDto(ResponseCode.SUCCESS, collection.getInfo().toString()))
    }
}