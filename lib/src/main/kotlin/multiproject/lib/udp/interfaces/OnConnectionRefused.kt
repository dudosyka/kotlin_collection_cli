package multiproject.lib.udp.interfaces

import multiproject.lib.dto.RequestDto

fun interface OnConnectionRefused {
    fun process(requestDto: RequestDto)
}