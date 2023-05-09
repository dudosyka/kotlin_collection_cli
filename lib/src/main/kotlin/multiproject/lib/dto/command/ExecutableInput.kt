package multiproject.lib.dto.command


class ExecutableInput (
    val args: List<Any?> = listOf(),
    val data: MutableMap<String, Any?> = mutableMapOf()
)