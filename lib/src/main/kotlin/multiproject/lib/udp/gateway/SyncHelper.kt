package multiproject.lib.udp.gateway

import kotlinx.serialization.Serializable

@Serializable
data class SyncHelper (
    val removedInstances: MutableList<Long> = mutableListOf()
)