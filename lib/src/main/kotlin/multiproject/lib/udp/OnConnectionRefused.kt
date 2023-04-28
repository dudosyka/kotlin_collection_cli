package multiproject.lib.udp

import multiproject.lib.dto.RequestDto

fun interface OnConnectionRefused {
    fun process(requestDto: RequestDto)
}