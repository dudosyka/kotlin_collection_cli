package multiproject.lib.dto.command

import kotlinx.serialization.Serializable

@Serializable
data class CommandDto(
    val name: String,
    val needAuth: Boolean = true,
    val fileReaderSource: Boolean = false,
    val authorizedEndpoint: Boolean = false,
    val hideFromClient: Boolean = false,
    val arguments: Map<String, CommandArgumentDto>
)