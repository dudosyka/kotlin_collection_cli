package multiproject.udpsocket.dto.command

import kotlinx.serialization.Serializable

@Serializable
data class CommandDto(
    val name: String,
    val arguments: Map<String, CommandArgumentDto>
)