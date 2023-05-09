package multiproject.server.modules.auth

import multiproject.server.exceptions.ForbiddenException
import java.time.LocalDateTime
import java.util.*

class Auth(
    private val login: String = "",
    private val password: String = ""
) {
    fun login(): String {
        if (login == "aboba" && password == "pass") {
            val id = 2
            val curDate = LocalDateTime.now().toLocalDate()
            return JwtUtil.sign(JwtBody(subject = id, expirationDate = Date(curDate.year, curDate.monthValue, curDate.dayOfMonth + 1)))
        } else
            throw ForbiddenException()
    }

    fun auth(token: String): JwtBody {
        return JwtUtil.verify(token)
    }
}