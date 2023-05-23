package multiproject.server.command.user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class LongCommand(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    val scope: CoroutineScope = CoroutineScope(Job())
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override suspend fun execute(input: ExecutableInput): Response {
        println("Processing...")

        val data = scope.async { collection.getInfo() }

        println("Second step started!")

        delay(15_000)

        println(data.isActive)
        println(data.isCompleted)
        println(data.isCancelled)
//        println(data.size)

        println("Processed!")

        return Response(ResponseCode.SUCCESS, "success processed!")
    }
}