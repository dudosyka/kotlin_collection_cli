package multiproject.lib.utils

import multiproject.lib.request.Request

class RequestToExecutableInterpreter {
    fun interpret(request: Request): ExecutableInput {
        return ExecutableInput(
            request.dto.data?.inlineArguments ?: listOf(),
            request.dto.data?.arguments?.toMutableMap() ?: mutableMapOf()
        )
    }
}