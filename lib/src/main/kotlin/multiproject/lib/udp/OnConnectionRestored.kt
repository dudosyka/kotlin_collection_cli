package multiproject.lib.udp

import multiproject.lib.dto.ResponseDto

fun interface OnConnectionRestored {
    fun process(responseDto: ResponseDto)
}