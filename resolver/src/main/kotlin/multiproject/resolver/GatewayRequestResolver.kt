package multiproject.resolver

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.gateway.ResolveError
import multiproject.lib.request.Request
import multiproject.lib.request.resolver.RequestResolver
import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.time.ZonedDateTime

class GatewayRequestResolver: RequestResolver() {
    private val gateway: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
    private val logger: Logger by inject(Logger::class.java, named("logger"))
    override fun resolveFirst(request: Request) {
        if (request directionIs RequestDirection.FROM_CLIENT)
            if (gateway isPendingClient request) throw ResolveError(ResponseCode.CONNECTION_REFUSED)
            else {
                gateway.sendThrough(request) {
                    path = PathDto("system", "_load")
                }
            }
        if (request directionIs RequestDirection.FROM_SERVER)
            gateway.addServer(ConnectedServer(0, ZonedDateTime.now().toEpochSecond(), request.getFrom()))
        else
            logger(LogLevel.WARN, "Unknown request have come $request")
    }

    override fun resolve(request: Request) {
        if (request directionIs RequestDirection.FROM_CLIENT)
            gateway.sendThrough(request) {}
        else if (request directionIs RequestDirection.FROM_SERVER) {
            println("We here too")
            if (!(gateway clearPending request)) {
                println("And here")
                return
            }
            gateway.servers.find { it.address == request.getSender() }?.apply {
                temporaryUnavailable = Pair(0, false)
                lastRequest = ZonedDateTime.now().toEpochSecond()
                pendingRequest--
            }
            val syncHelper = request.getSyncHelper()

            if (gateway.blockInput && request.getFrom() == gateway.syncInitiator?.getFrom()) {
                gateway.blockInput = false
                if (syncHelper.synchronizationEnded)
                    gateway.runBlockedRequests()
            }
            if (syncHelper.commits.size > 0) {
                gateway.commits.addAll(syncHelper.commits)
            }
            gateway.logger(LogLevel.INFO, "Unpushed changes ${gateway.commits}")
            val from = request.getFrom()
            request.removeSystemHeaders()
            gateway.emit(from, request)
        }
        else
            logger(LogLevel.WARN, "Unknown request have come $request")
    }

    override fun resolveError(request: Request, e: ResolveError) {
        if (e.code == ResponseCode.CONNECTION_REFUSED) {
            request.apply {
                response = ResponseDto(e.code, result = "Server unavailable. Connection refused.")
            }
            gateway.emit(request.getFrom(), request)
        }
    }

}