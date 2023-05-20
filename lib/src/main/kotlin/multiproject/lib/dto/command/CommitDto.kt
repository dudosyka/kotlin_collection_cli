package multiproject.lib.dto.command

import kotlinx.serialization.Serializable
import multiproject.lib.utils.UltimateSerializer

@Serializable
data class CommitDto (
    val id: Long,
    val timestamp: Long,
    val data: Map<String, @Serializable(with=UltimateSerializer::class) Any?>? = null,
)