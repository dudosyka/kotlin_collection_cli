package multiproject.server.modules.user

import kotlinx.serialization.Serializable
import multiproject.server.collection.item.Entity
import multiproject.server.database.DatabaseManager
import multiproject.server.database.DatabasePredicate
import multiproject.server.exceptions.ForbiddenException
import multiproject.server.modules.user.jwt.JwtBody
import multiproject.server.modules.user.jwt.JwtUtil
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

@Serializable
class User(
    override var id: Int,
    var login: String,
    var password: String,
) : Entity() {
    override val tableName: String
        get() = "users"
    companion object {
        private val databaseManager: DatabaseManager by inject(DatabaseManager::class.java, named("dbManager"))
        fun getByLogin(login: String): User? {
            return databaseManager.findOne(UserBuilder(), mapOf( "users" to DatabasePredicate("login", "=", "'$login'") ))
        }
        fun hash(input: String): String {
            return MessageDigest
                .getInstance("SHA-256")
                .digest(input.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }
        }

        private fun compareHash(input: String, hash: String): Boolean {
            return hash(input) == hash
        }
        fun create(data: MutableMap<String, Any?>) {
            val user = UserBuilder().build(data)
            databaseManager.insert(mutableListOf(user))
        }
        fun login(login: String, password: String): String {
            val user = getByLogin(login)
            if (user != null) {
                if (!compareHash(password, user.password))
                    throw ForbiddenException()
                val curDate = LocalDateTime.now().toLocalDate()
                return JwtUtil.sign(
                    JwtBody(
                        subject = user.id,
                        data = mapOf(
                            "id" to user.id.toString(),
                            "login" to user.login,
                            "password" to user.password
                        ),
                        expirationDate = Date(curDate.year, curDate.monthValue, curDate.dayOfMonth + 1)
                    )
                )
            } else
                throw ForbiddenException()
        }
        fun checkToken(token: String): JwtBody {
            return JwtUtil.verify(token)
        }
    }

    override fun toString(): String {
        return "User { id=$id, login=$login }"
    }
}