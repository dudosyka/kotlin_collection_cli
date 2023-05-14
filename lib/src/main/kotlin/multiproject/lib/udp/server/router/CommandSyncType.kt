package multiproject.lib.udp.server.router

import kotlinx.serialization.Serializable

@Serializable
data class CommandSyncType (
    val sync: Boolean,
    val blocking: Boolean = false,
    val blockByArgument: Int? = null,
    val blockByDataValue: String? = null
)