package multiproject.lib.dto.request

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
                RequestDirection.UNKNOW
            }
        }
    }
    fun interpret(input: RequestDirection): Long? {
        return when (input) {
            RequestDirection.FROM_CLIENT -> {
                1
            }
            RequestDirection.FROM_SERVER -> {
                2
            }
            else -> {
                null
            }
        }
    }
}
enum class RequestDirection {
    FROM_CLIENT,
    FROM_SERVER,
    UNKNOW
}