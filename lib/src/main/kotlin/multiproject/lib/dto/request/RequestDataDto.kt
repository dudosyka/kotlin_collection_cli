package multiproject.lib.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestDataDto (
    val arguments: Map<String, @Serializable(with= RequestDataSerializer::class) Any?>,
    val inlineArguments: List<@Serializable(with= RequestDataSerializer::class) Any?>
)