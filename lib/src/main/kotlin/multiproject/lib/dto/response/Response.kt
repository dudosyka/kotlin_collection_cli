package multiproject.lib.dto.response

import kotlinx.serialization.Serializable
import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.request.RequestDataSerializer

class Response(val dto: ResponseDto?) {
    constructor(
        code: ResponseCode,
        result: String,
        headers: MutableMap<String, @Serializable(with = RequestDataSerializer::class) Any?> = mutableMapOf(),
        commands: Map<String, List<CommandDto>> = mapOf()
    ) : this(ResponseDto(code, result, headers, commands))
}