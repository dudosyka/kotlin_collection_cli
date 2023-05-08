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
 * Clear command
 *
 * @constructor Create empty Clear command
 */
class ClearCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val description: String = "Clear the collection"
    override fun execute(input: ExecutableInput): Response {
        collection.clear()
        return Response(ResponseDto(ResponseCode.SUCCESS, "Collection is successfully cleared!"))
    }
}