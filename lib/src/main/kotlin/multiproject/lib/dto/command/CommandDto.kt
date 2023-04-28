package multiproject.lib.dto.command

import kotlinx.serialization.Serializable

@Serializable
data class CommandDto(
    val name: String,
    val fileReaderSource: Boolean = false,
    val arguments: Map<String, CommandArgumentDto>
)