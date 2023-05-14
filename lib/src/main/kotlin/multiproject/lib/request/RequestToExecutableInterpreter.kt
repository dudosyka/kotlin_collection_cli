package multiproject.lib.request

import multiproject.lib.dto.command.ExecutableInput

class RequestToExecutableInterpreter {
    fun interpret(request: Request): ExecutableInput {
        return ExecutableInput(
            request.data.inlineArguments,
            request.data.arguments,
            request
        )
    }
}