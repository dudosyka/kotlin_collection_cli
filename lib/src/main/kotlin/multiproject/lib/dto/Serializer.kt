package multiproject.lib.dto

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseDto

object Serializer {

    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResponse(data: ResponseDto): String = json.encodeToString(data)

    fun serializeRequest(data: RequestDto): String = json.encodeToString(data)

    fun deserializeResponse(data: String): ResponseDto = json.decodeFromString<ResponseDto>(data)

    fun deserializeRequest(data: String): RequestDto = json.decodeFromString<RequestDto>(data)
}