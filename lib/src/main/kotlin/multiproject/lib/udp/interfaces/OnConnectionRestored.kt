package multiproject.lib.udp.interfaces

import multiproject.lib.dto.response.ResponseDto

fun interface OnConnectionRestored {
    fun process(responseDto: ResponseDto)
}