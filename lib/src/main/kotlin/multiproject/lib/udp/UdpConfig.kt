package multiproject.lib.udp

class UdpConfig {
    companion object {
        const val serverAddress: String = "127.0.0.1"
        const val serverPort: Int = 3032
        const val timeout: Long = 5 * 1000 //in ms
        const val reconnectTimeout: Long = 5 * 1000 //in ms
    }
}