package multiproject.lib.udp.client

import multiproject.lib.dto.ResponseDto

fun interface OnConnectionRestored {
    fun process(responseDto: ResponseDto)
}