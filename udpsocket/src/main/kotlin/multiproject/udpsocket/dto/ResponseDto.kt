package multiproject.udpsocket.dto

import kotlinx.serialization.Serializable
import multiproject.udpsocket.dto.command.CommandDto

@Serializable
open class ResponseDto(
    val code: ResponseCode,
    val result: String,
    val commands: List<CommandDto> = listOf()
)