package multiproject.server.modules.user

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldDelegate

class UserBuilder : EntityBuilder<User>() {
    override val tableName: String
        get() = "users"
    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "id" to CommandArgumentDto(
            name = "id",
            type = FieldType.INT,
            show = false,
            autoIncrement = true
        ),
        "login" to CommandArgumentDto(
            name = "login",
            required = true,
            type = FieldType.STRING
        ),
        "password" to CommandArgumentDto(
            name = "password",
            required = true,
            type = FieldType.STRING
        ),
    )

    /**
     * Build
     *
     * @param map
     * @return
     */
    override fun build(map: MutableMap<String, Any?>): User {
        val id: Long? by FieldDelegate(map = map, fields["id"]!!)
        val login: String? by FieldDelegate(map = map, fields["login"]!!)
        val password: String? by FieldDelegate(map = map, fields["password"]!!)
        return User(id?.toInt()!!, login!!, password!!).apply {
            pureData = map
            fieldsSchema = fields
        }
    }
}