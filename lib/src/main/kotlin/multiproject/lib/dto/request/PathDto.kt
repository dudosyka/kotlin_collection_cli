package multiproject.lib.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PathDto (
    val controller: String,
    val route: String
)