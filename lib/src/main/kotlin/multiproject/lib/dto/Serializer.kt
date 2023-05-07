package multiproject.lib.dto

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto

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