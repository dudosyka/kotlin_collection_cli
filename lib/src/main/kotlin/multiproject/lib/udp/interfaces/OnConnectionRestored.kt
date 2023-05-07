package multiproject.lib.udp.interfaces

import multiproject.lib.dto.ResponseDto

fun interface OnConnectionRestored {
    fun process(responseDto: ResponseDto)
}