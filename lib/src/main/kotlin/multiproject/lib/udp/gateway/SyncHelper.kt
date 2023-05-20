package multiproject.lib.udp.gateway

import kotlinx.serialization.Serializable
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.utils.UltimateSerializer
import java.net.InetSocketAddress

@Serializable
data class SyncHelper (
    val removedInstances: MutableList<Long> = mutableListOf(),
    var synchronizationEnded: Boolean = false,
    var commits: MutableList<CommitDto> = mutableListOf(),
    var servers: MutableList<@Serializable(with = UltimateSerializer::class) InetSocketAddress?> = mutableListOf()
)