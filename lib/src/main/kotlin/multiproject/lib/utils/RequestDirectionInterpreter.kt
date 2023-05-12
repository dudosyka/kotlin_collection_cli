package multiproject.lib.utils

import multiproject.lib.dto.request.RequestDirection

object RequestDirectionInterpreter {
    fun interpret(input: Long?): RequestDirection {
        return when (input) {
            1L -> {
                RequestDirection.FROM_CLIENT
            }
            2L -> {
                RequestDirection.FROM_SERVER
            }
            else -> {
                RequestDirection.UNKNOWN
            }
        }
    }
    fun interpret(input: RequestDirection): Long? {
        return when (input) {
            RequestDirection.FROM_CLIENT -> {
                1L
            }
            RequestDirection.FROM_SERVER -> {
                2L
            }
            else -> {
                null
            }
        }
    }
}
