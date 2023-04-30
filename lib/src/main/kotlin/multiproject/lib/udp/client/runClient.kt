package multiproject.lib.udp.client

object runClient {
    operator fun invoke(init: ClientUdpChannel.() -> Unit): ClientUdpChannel = ClientUdpChannel().apply(init)
}