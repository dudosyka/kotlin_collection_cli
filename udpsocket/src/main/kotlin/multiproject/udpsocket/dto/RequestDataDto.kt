package multiproject.udpsocket.dto

import kotlinx.serialization.Serializable
import multiproject.udpsocket.dto.command.CommandArgumentDto

@Serializable
data class RequestDataDto (val arguments: Map<String, @Serializable(with=RequestDataSerializer::class) Any?>, val inlineArguments: List<@Serializable(with=RequestDataSerializer::class) Any?>)