package multiproject.lib.udp

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.utils.Serializer
import multiproject.lib.request.Request
import multiproject.lib.request.resolver.RequestResolver
import multiproject.lib.udp.interfaces.OnConnectionRefused
import multiproject.lib.udp.interfaces.OnConnectionRestored
import multiproject.lib.udp.disconnect.CloseOnDisconnectStrategy
import multiproject.lib.udp.disconnect.DisconnectStrategy
import multiproject.lib.udp.interfaces.OnDisconnectAttempt
import multiproject.lib.udp.interfaces.OnReceive
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

abstract class UdpChannel {
    var onConnectionRefusedCallback: OnConnectionRefused = OnConnectionRefused { }
    var onConnectionRestoredCallback: OnConnectionRestored = OnConnectionRestored { }
    var receiveCallback: OnReceive = OnReceive { _, _ -> }
    var firstConnectCallback: OnReceive = OnReceive { _, _ -> }
    var onDisconnectAttempt: OnDisconnectAttempt = OnDisconnectAttempt { _ -> }
    protected val channel: DatagramChannel = DatagramChannel.open()
    var disconnectStrategy: DisconnectStrategy = CloseOnDisconnectStrategy()
    var wasDisconnected: Boolean = false
    private var connections: MutableList<SocketAddress> = mutableListOf()
    var servers: MutableList<ConnectedServer> = mutableListOf()
    lateinit var requestResolver: RequestResolver
    var logger: Logger = Logger()

    fun getChannelAddress(): SocketAddress = channel.localAddress
    fun bindOn(address: InetSocketAddress?) {
        if (address == null) {
            val socket = ServerSocket(0)
            val port = socket.localPort
            channel.bind(InetSocketAddress(InetAddress.getLocalHost(), port))
            return
        }
        logger(LogLevel.INFO, "Socket bind on $address")
        channel.bind(address)
    }
    fun addServer(address: ConnectedServer) {
        this.servers.add(address)
    }
    private fun processBuffer(buffer: ByteBuffer): String {
        buffer.flip()
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return String(bytes)
    }
    protected fun getMessage(): String {
        var data: String? = null
        while (data.isNullOrEmpty()) {
            val buffer: ByteBuffer = ByteBuffer.allocate(65535)
            channel.receive(buffer)
            data = processBuffer(buffer)
        }
        return data
    }
    fun emit(address: InetSocketAddress, data: Request) {
        val dataString = Serializer.serializeRequest(data)
        data setSender channel.localAddress
        logger(LogLevel.INFO, "Emit to $address with data: $data")
        channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)
    }
    open fun send(address: InetSocketAddress, data: Request): Request = TODO("not yet implemented")

    protected fun onMessage(address: SocketAddress, data: String) {
        val request = Serializer.deserializeRequest(data)
        logger(LogLevel.INFO, "Received request. from $address with data $request")
        this.receiveCallback.process(address, request)
    }
    protected open fun onNewConnection(address: SocketAddress, data: String) {
        val request = Serializer.deserializeRequest(data)
        logger(LogLevel.INFO, "Received first request. from $address with data $request")
        connections.add(address)
        this.firstConnectCallback.process(address, request)
    }
    private fun receive() {
        while (true) {
            val buffer = ByteBuffer.allocate(65535)
            val address: SocketAddress = channel.receive(buffer)
            val data = this.processBuffer(buffer)

            if (!connections.contains(address)) {
                this.onNewConnection(address, data)
            } else {
                this.onMessage(address, data)
            }
        }
    }

    open fun stop() {
        channel.close()
    }
    open fun run() {
        this.receive()
    }
}