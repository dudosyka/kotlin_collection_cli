package multiproject.lib.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.request.resolver.ResolveError

class NoAvailableServers: ResolveError(ResponseCode.CONNECTION_REFUSED) {
    override val message: String
        get() = "No available servers. Try to reconnect later."
}