package multiproject.lib.udp

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.dto.Serializer
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.request.resolver.RequestResolver
import multiproject.lib.udp.interfaces.OnConnectionRefused
import multiproject.lib.udp.interfaces.OnConnectionRestored
import multiproject.lib.udp.disconnect.CloseOnDisconnectStrategy
import multiproject.lib.udp.disconnect.DisconnectStrategy
import multiproject.lib.udp.interfaces.OnDisconnectAttempt
import multiproject.lib.udp.interfaces.OnReceive
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.PortUnreachableException
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
    fun bindOn(address: InetSocketAddress?) {
        if (address == null) {
            val socket = ServerSocket(0)
            val port = socket.localPort
            channel.bind(InetSocketAddress(InetAddress.getLocalHost(), port))
            return
        }
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
    fun emit(address: InetSocketAddress, data: RequestDto) {
        val dataString = Serializer.serializeRequest(data)
        println("Emit to $address with data: $data")
        channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)
    }
    fun emit(address: SocketAddress, data: ResponseDto) {
        println("Emit to $address with data: $data")
        val dataString = Serializer.serializeResponse(data)
        channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)
    }
    open fun send(address: InetSocketAddress, data: RequestDto): ResponseDto {
        println("Send to $address with data: $data")
        val dataString = Serializer.serializeRequest(data)
        val response = try {
            channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)

            val msg = this.getMessage()
            val returnedFromServer = Serializer.deserializeResponse(msg)

            if (returnedFromServer.code.toString() == ResponseCode.CONNECTION_REFUSED.toString())
                throw PortUnreachableException()

            if (wasDisconnected) {
                disconnectStrategy.attemptNum = 0
                wasDisconnected = false
                onConnectionRestoredCallback.process(returnedFromServer)
            }

            returnedFromServer

        } catch (e: PortUnreachableException) {
            if (this.disconnectStrategy.attemptNum == 0) {
                wasDisconnected = true
                onConnectionRefusedCallback.process()
            }
            val response = disconnectStrategy.onDisconnect(this, address, RequestDirection.FROM_CLIENT)
            response
        }
        println("Returned response from $address with data $response")
        return response
    }
    protected fun onMessage(address: SocketAddress, data: String) {
        val dto = Serializer.deserializeRequest(data)
        println("Received request. from $address with data $dto")
        this.receiveCallback.process(address, dto)
    }
    protected open fun onNewConnection(address: SocketAddress, data: String) {
        val dto = Serializer.deserializeRequest(data)
        println("Received first request. from $address with data $dto")
        connections.add(address)
        this.firstConnectCallback.process(address, dto)
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
    fun disconnect(address: SocketAddress) {
        connections.remove(address)
    }

    open fun stop() {
        channel.close()
    }
    open fun run() {
        println("Bind on: ${this.channel.localAddress}")
        this.receive()
    }
}