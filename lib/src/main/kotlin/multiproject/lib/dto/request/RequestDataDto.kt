package multiproject.lib.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestDataDto (
    var arguments: MutableMap<String, @Serializable(with= RequestDataSerializer::class) Any?> = mutableMapOf(),
    var inlineArguments: List<@Serializable(with= RequestDataSerializer::class) Any?> = listOf()
)