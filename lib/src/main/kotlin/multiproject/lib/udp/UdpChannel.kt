package multiproject.lib.udp

import multiproject.lib.dto.RequestDto
import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.Serializer
import multiproject.lib.udp.interfaces.OnConnectionRefused
import multiproject.lib.udp.interfaces.OnConnectionRestored
import multiproject.lib.udp.disconnect.CloseOnDisconnectStrategy
import multiproject.lib.udp.disconnect.DisconnectStrategy
import multiproject.lib.udp.interfaces.OnDisconnectAttempt
import multiproject.lib.udp.interfaces.OnConnect
import multiproject.lib.udp.interfaces.OnReceive
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

abstract class UdpChannel {
    var onConnectionRefusedCallback: OnConnectionRefused = OnConnectionRefused {
            _ ->
    }
    var onConnectionRestoredCallback: OnConnectionRestored = OnConnectionRestored {
            _ ->
    }
    var receiveCallback: OnReceive = OnReceive {
            _, _, _ ->
    }
    var firstConnectCallback: OnConnect = OnConnect {
            _, _ ->
    }
    var onDisconnectAttempt: OnDisconnectAttempt = OnDisconnectAttempt {
            _ ->
    }
    protected val channel: DatagramChannel = DatagramChannel.open()
    var disconnectStrategy: DisconnectStrategy = CloseOnDisconnectStrategy()
    var wasDisconnected: Boolean = false
    private var connections: MutableList<SocketAddress> = mutableListOf()
    protected var servers: MutableList<InetSocketAddress> = mutableListOf()
    fun bindOn(address: InetSocketAddress) {
        channel.bind(address)
    }
    fun addServer(address: InetSocketAddress) {
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
        channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)
    }
    fun emit(address: SocketAddress, data: ResponseDto) {
        println()
        print("Response to $address "); print(data)
        val dataString = Serializer.serializeResponse(data)
        channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)
    }
    open fun send(address: InetSocketAddress, data: RequestDto): ResponseDto {
        val dataString = Serializer.serializeRequest(data)
        return try {
            channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)
            return if (wasDisconnected) {
                disconnectStrategy.attemptNum = 0
                wasDisconnected = false
                onConnectionRestoredCallback.process(Serializer.deserializeResponse(this.getMessage()))
                ResponseDto(ResponseCode.SUCCESS, "")
            } else {
                Serializer.deserializeResponse(this.getMessage())
            }
        } catch (e: PortUnreachableException) {
            if (this.disconnectStrategy.attemptNum == 0) {
                wasDisconnected = true
                onConnectionRefusedCallback.process(data)
            }
            disconnectStrategy.onDisconnect(this, address)
        }
    }
    private fun onMessage(address: SocketAddress, data: String) {
        this.receiveCallback.process(channel, address, Serializer.deserializeRequest(data))
    }
    private fun onNewConnection(address: SocketAddress) {
        connections.add(address)
        this.firstConnectCallback.process(channel, address)
    }
    private fun receive() {
        while (true) {
            val buffer = ByteBuffer.allocate(65535)
            val address: SocketAddress = channel.receive(buffer)

            if (!connections.contains(address)) {
                this.onNewConnection(address)
            } else {
                this.onMessage(address, this.processBuffer(buffer))
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
        this.receive()
    }
}