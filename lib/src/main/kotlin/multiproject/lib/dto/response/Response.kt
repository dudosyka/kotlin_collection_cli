package multiproject.lib.dto.response

import kotlinx.serialization.Serializable
import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.utils.UltimateSerializer

class Response(var dto: ResponseDto?, val commits: List<CommitDto> = listOf()) {
    constructor(
        code: ResponseCode,
        result: String,
        headers: MutableMap<String, @Serializable(with = UltimateSerializer::class) Any?> = mutableMapOf(),
        commands: Map<String, List<CommandDto>> = mapOf(),
        commits: List<CommitDto> = listOf()
    ) : this(ResponseDto(code, result, headers, commands), commits)
}