package multiproject.lib.udp
object UdpConfig {
    const val serverAddress: String = "127.0.0.1"
    const val serverPort: Int = 7070
    const val timeout: Long = 10 * 1000 //in ms
    const val reconnectTimeout: Long = 10 * 1000 //in ms
    const val pendingRequestCheckTimeout: Long = 5 * 1000 //in ms

    //Time after sending the request when we will try to send it to another server
    const val holdRequestTimeout: Long = 60 //in seconds

    //Time after last response from server when we will mark it as unavailable
    const val unavailableTimeout: Long = 60 //in seconds

    //Time that server have after being marked as temporary unavailable to emit something
    const val removeAfterUnavailableTimeout: Long = 120 //in seconds
}