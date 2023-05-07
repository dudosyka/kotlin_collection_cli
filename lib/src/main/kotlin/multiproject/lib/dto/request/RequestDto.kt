package multiproject.lib.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    val command: String,
    val headers: MutableMap<String, @Serializable(with=RequestDataSerializer::class) Any?> = mutableMapOf(),
    val data: RequestDataDto? = null,
)