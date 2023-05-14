package multiproject.lib.dto.command

import multiproject.lib.request.Request


class ExecutableInput (
    val args: List<Any?> = listOf(),
    val data: MutableMap<String, Any?> = mutableMapOf(),
    val request: Request
)