package multiproject.lib.dto.response

import kotlinx.serialization.Serializable
import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.request.UltimateSerializer

@Serializable
data class ResponseDto(
    val code: ResponseCode,
    val result: String = "",
    val headers: MutableMap<String, @Serializable(with = UltimateSerializer::class) Any?> = mutableMapOf(),
    val commands: Map<String, List<CommandDto>> = mapOf()
)