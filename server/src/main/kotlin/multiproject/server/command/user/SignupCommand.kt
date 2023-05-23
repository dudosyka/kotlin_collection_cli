package multiproject.server.command.user

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.server.exceptions.NotUniqueException
import multiproject.server.modules.user.User
import multiproject.server.modules.user.UserBuilder
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class SignupCommand(controller: Controller) : Command(controller) {
    val server: ServerUdpChannel by KoinJavaComponent.inject(ServerUdpChannel::class.java, named("server"))
    override val fields: Map<String, CommandArgumentDto> = UserBuilder().fields
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override suspend fun execute(input: ExecutableInput): Response {
        val checkLogin = User.getByLogin(input.data["login"]?.toString() ?: throw NotUniqueException("login"))
        return if (checkLogin == null) {
            input.data["password"] = User.hash(input.data["password"].toString())
            User.create(input.data)
            Response(ResponseCode.SUCCESS, "Account successfully created!")
        } else {
            throw NotUniqueException("login")
        }
    }
}
