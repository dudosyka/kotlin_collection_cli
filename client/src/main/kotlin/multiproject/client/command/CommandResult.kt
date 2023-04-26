package multiproject.client.command

import multiproject.udpsocket.dto.ResponseDto

class CommandResult(val body: String, val success: Boolean = true, val responseDto: ResponseDto? = null)