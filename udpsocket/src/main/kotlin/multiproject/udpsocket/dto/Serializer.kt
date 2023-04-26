package multiproject.udpsocket.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
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
            try {
                return Json.decodeFromString<ResponseDto>(data)
            } catch (e: Exception) {
                println(data)
                return ResponseDto(ResponseCode.BAD_REQUEST, "serialization error")
            }
        }

        fun deserializeRequest(data: String): RequestDto {
            return Json.decodeFromString<RequestDto>(data)
        }
    }
}