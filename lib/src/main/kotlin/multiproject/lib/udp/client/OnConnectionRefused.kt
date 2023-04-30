package multiproject.lib.udp.client

import multiproject.lib.dto.RequestDto

fun interface OnConnectionRefused {
    fun process(requestDto: RequestDto)
}