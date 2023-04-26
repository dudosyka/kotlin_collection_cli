package multiproject.udpsocket.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    val command: String,
    val data: RequestDataDto? = null,
)