package multiproject.lib.dto.command

import multiproject.lib.dto.ResponseDto

class CommandResult(val body: String, val success: Boolean = true, val responseDto: ResponseDto? = null)