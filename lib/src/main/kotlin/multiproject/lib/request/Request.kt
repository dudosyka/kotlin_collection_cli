package multiproject.lib.request

import kotlinx.serialization.Serializable
import multiproject.lib.dto.request.*
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.utils.RequestDirectionInterpreter
import multiproject.lib.utils.SocketAddressInterpreter
import java.net.InetSocketAddress
import java.net.SocketAddress

@Serializable
open class Request(
    var path: PathDto,
    private var headers: MutableMap<String, @Serializable(with=UltimateSerializer::class) Any?> = mutableMapOf(),
    var data: RequestDataDto = RequestDataDto(),
    var response: ResponseDto = ResponseDto(ResponseCode.SUCCESS)
) {

    fun applyMiddleware(middleware: Request.() -> Unit): Request = this.apply(middleware)


    // ------------- Headers managing ---------------- //
    fun isEmptyPath(): Boolean {
        return path.route == "" && path.controller == ""
    }
    infix fun setHeader(value: Pair<String, Any?>) {
        this.headers[value.first] = value.second
    }
    fun getHeader(key: String): Any? {
        return this.headers[key]
    }
    infix fun directionIs(requestDirection: RequestDirection): Boolean {
        val direction = this.getHeader("requestDirection") ?: return false

        return (RequestDirectionInterpreter.interpret(direction.toString().toLongOrNull() ?: 3L) == requestDirection)
    }
    infix fun setDirection(requestDirection: RequestDirection) = this setHeader Pair("requestDirection", RequestDirectionInterpreter.interpret(requestDirection))
    infix fun setFrom(address: InetSocketAddress) = this setHeader Pair("from", address)
    infix fun setFrom(address: SocketAddress) = this setFrom SocketAddressInterpreter.interpret(address)
    infix fun setFrom(address: String) = this setFrom SocketAddressInterpreter.interpret(address)
    fun getFrom(): InetSocketAddress = SocketAddressInterpreter.interpret(this.getHeader("from").toString())
    infix fun setSender(address: SocketAddress) = this setHeader Pair("sender", SocketAddressInterpreter.interpret(address))
    fun getSender(): InetSocketAddress = SocketAddressInterpreter.interpret(this.getHeader("sender").toString())

    // ------------- Response managing ---------------- //
    infix fun checkCode(code: ResponseCode): Boolean {
        return code == response.code
    }

    override fun toString(): String {
        return "Request (path=$path, headers=$headers, data=$data, response=$response)"
    }
}