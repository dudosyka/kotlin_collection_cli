package multiproject.lib.dto.command

import kotlinx.serialization.Serializable

@Serializable
data class CommandArgumentDto(
    val name: String?,
    val index: Int? = null,
    val type: FieldType = FieldType.STRING,
    val min: Int? = null,
    val max: Int? = null,
    val required: Boolean = false,
    val show: Boolean = true,
    val inline: Boolean = false,
    val nested: Map<String, CommandArgumentDto>? = null,
    val nestedTable: String? = null,
    val nestedJoinOn: Pair<String, String>? = null,
    val choisable: List<String>? = null,
)