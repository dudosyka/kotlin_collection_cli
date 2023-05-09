package multiproject.lib.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    var pathDto: PathDto,
    var headers: MutableMap<String, @Serializable(with=RequestDataSerializer::class) Any?> = mutableMapOf(),
    var data: RequestDataDto = RequestDataDto(),
)