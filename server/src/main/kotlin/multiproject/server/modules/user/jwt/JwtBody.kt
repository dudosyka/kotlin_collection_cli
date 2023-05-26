package multiproject.server.modules.user.jwt

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
            val intSubject = subject.toInt()
            return JwtBody(intSubject, data, expirationDate)
        }
    }

    override fun toString(): String {
        return "JwtBody - id=$subject, data=$data, expirationAt=$expirationDate"
    }
}