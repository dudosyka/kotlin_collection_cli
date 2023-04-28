package multiproject.lib.dto

import kotlinx.serialization.Serializable
import multiproject.lib.dto.command.CommandDto

@Serializable
open class ResponseDto(
    val code: ResponseCode,
    val result: String,
    val commands: List<CommandDto> = listOf()
)