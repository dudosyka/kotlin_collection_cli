package multiproject.lib.request

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.exceptions.EmptyRequestProvided
import java.net.InetSocketAddress

open class Request(dto: RequestDto?, val from: InetSocketAddress) {
    var dto: RequestDto
    init {
        if (dto == null)
            throw EmptyRequestProvided()
        this.dto = dto
    }

    val requestDirection: Long? by RequestHeaderDelegate(request = dto)
    fun acceptResolver(resolver: RequestResolver) {
        resolver.resolve(this)
    }
    fun applyMiddleware(middleware: Request.() -> Unit): Request = this.apply(middleware)
}