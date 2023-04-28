package multiproject.lib.dto

import kotlinx.serialization.*
import kotlinx.serialization.json.*

class Serializer {
    companion object {
        fun serializeResponse(data: ResponseDto): String {
            return Json.encodeToString(data)
        }

        fun serializeRequest(data: RequestDto): String {
            return Json.encodeToString(data)
        }

        fun deserializeResponse(data: String): ResponseDto {
            return try {
                Json.decodeFromString<ResponseDto>(data)
            } catch (e: Exception) {
                println(data)
                ResponseDto(ResponseCode.BAD_REQUEST, "serialization error")
            }
        }

        fun deserializeRequest(data: String): RequestDto {
            return Json.decodeFromString<RequestDto>(data)
        }
    }
}