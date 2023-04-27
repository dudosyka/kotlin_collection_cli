package multiproject.udpsocket

import multiproject.udpsocket.dto.ResponseDto

fun interface OnConnectionRestored {
    fun process(responseDto: ResponseDto)
}