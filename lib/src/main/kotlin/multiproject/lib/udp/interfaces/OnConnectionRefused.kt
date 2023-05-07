package multiproject.lib.udp.interfaces

import multiproject.lib.dto.request.RequestDto

fun interface OnConnectionRefused {
    fun process(requestDto: RequestDto)
}