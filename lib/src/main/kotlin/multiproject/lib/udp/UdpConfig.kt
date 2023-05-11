package multiproject.lib.udp
object UdpConfig {
    const val serverAddress: String = "127.0.0.1"
    const val serverPort: Int = 7070
    const val timeout: Long = 10 * 1000 //in ms
    const val reconnectTimeout: Long = 10 * 1000 //in ms
    const val pendingRequestCheckTimeout: Long = 5 * 1000 //in ms
    const val unavailableTimeout: Long = 10 //in ms
    const val removeAfterUnavailableTimeout: Long = 30 //in ms
}