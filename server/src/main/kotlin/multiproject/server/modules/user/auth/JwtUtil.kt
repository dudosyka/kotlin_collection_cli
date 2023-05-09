package multiproject.server.modules.user.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JwtUtil {
    private val key: ByteArray = "my_super_secret_key_for_jwt_authorization_which_grater_than_256_bits".toByteArray()
    fun sign(jwtBody: JwtBody): String {
        val jwt = Jwts
            .builder()
            .setSubject(jwtBody.subject.toString())
            .setExpiration(jwtBody.expirationDate)
            .signWith(Keys.hmacShaKeyFor(key))
            .serializeToJsonWith { map ->
                Json.encodeToString(map.map { it.key!! to it.value.toString() }.toMap()).toByteArray()
            }

        jwtBody.data.forEach {
            jwt.claim(it.key, it.value)
        }

        return jwt.compact()
    }

    fun verify(token: String): JwtBody {
        val parsed = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .deserializeJsonWith { array ->
                Json.decodeFromString<Map<String, String>>(String(array))
            }
            .build()
            .parseClaimsJws(token)

        val subject = parsed.body.subject
        val data = parsed.body.map {
            it.key to it.value.toString()
        }.toMap()

        return JwtBody.build(subject, data)
    }
}