package multiproject.udpsocket.dto.command

import kotlinx.serialization.Serializable

@Serializable
data class CommandArgumentDto(
    val name: String?,
    val index: Int? = null,
    val type: FieldType = FieldType.STRING,
    val min: Int? = null,
    val max: Int? = null,
    val required: Boolean = false,
    val inline: Boolean = false,
    val nested: Map<String, CommandArgumentDto>? = null,
    val choisable: List<String>? = null
)