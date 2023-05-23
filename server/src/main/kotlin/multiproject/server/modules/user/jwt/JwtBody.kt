package multiproject.server.modules.user.jwt

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.server.exceptions.BadJwtSubject
import java.util.*

@Serializable
class JwtBody (
    val subject: Int,
    val data: Map<String, String> = mapOf(),
    @Transient val expirationDate: Date = Date()
) {
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