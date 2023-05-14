package multiproject.lib.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import multiproject.lib.request.Request
import multiproject.lib.udp.gateway.SyncHelper
import multiproject.lib.udp.server.router.CommandSyncType

object Serializer {

    private val json = Json { ignoreUnknownKeys = true }

    fun serializeRequest(data: Request): String = json.encodeToString(data)

    fun deserializeRequest(data: String): Request = json.decodeFromString<Request>(data)

    fun serializeSyncHelper(data: SyncHelper): String = json.encodeToString(data)
    fun serializeSyncType(data: CommandSyncType): String = json.encodeToString(data)
    fun deserializeSyncHelper(data: String): SyncHelper = json.decodeFromString<SyncHelper>(data)
    fun deserializeSyncType(data: String): CommandSyncType = json.decodeFromString<CommandSyncType>(data)
}