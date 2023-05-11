package multiproject.lib.dto

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import multiproject.lib.request.Request

object Serializer {

    private val json = Json { ignoreUnknownKeys = true }

    fun serializeRequest(data: Request): String = json.encodeToString(data)

    fun deserializeRequest(data: String): Request = json.decodeFromString<Request>(data)

}