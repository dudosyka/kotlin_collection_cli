package multiproject.lib.udp.server

object runServer {
    operator fun invoke(init: ServerUdpChannel.() -> Unit): ServerUdpChannel = ServerUdpChannel().apply(init)
}