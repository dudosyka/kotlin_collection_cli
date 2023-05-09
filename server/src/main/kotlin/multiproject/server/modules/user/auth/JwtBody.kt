package multiproject.server.modules.user.auth

import multiproject.server.exceptions.BadJwtSubject
import java.util.Date

class JwtBody (val subject: Int, val data: Map<String, String> = mapOf(), val expirationDate: Date = Date()) {
    companion object {
        fun build(
            subject: String, data: Map<String, String> = mapOf(), expirationDate: Date = Date()
        ): JwtBody {
            val intSubject = subject.toIntOrNull() ?: throw BadJwtSubject()
            return JwtBody(intSubject, data, expirationDate)
        }
    }

    override fun toString(): String {
        return "JwtBody - id=$subject, data=$data, expirationAt=$expirationDate"
    }
}