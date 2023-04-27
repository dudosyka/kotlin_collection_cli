package multiproject.udpsocket

import multiproject.udpsocket.dto.RequestDto

fun interface OnConnectionRefused {
    fun process(requestDto: RequestDto)
}