package multiproject.lib.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestDataDto (
    var arguments: MutableMap<String, @Serializable(with= UltimateSerializer::class) Any?> = mutableMapOf(),
    var inlineArguments: List<@Serializable(with= UltimateSerializer::class) Any?> = listOf()
)