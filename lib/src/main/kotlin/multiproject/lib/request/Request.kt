package multiproject.lib.request

import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.request.resolvers.RequestResolver

class Request(val dto: RequestDto?) {
    val requestDirection: Long? by RequestHeaderDelegate(request = dto);
    fun acceptResolver(resolver: RequestResolver) {
        resolver.resolve(dto);
    }
}