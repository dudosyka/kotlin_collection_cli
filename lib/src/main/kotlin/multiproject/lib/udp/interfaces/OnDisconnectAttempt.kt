package multiproject.lib.udp.interfaces

fun interface OnDisconnectAttempt {
    fun process(attemptNum: Int)
}