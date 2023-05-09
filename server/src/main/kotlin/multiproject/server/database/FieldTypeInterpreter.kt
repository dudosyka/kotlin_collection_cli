package multiproject.server.database

import multiproject.lib.dto.command.FieldType

object FieldTypeInterpreter {
    fun interpret(type: FieldType): String {
        return when (type) {
            FieldType.STRING -> "varchar"
            FieldType.LONG -> "bigint"
            FieldType.INT -> "int"
            FieldType.FLOAT -> "float"
            FieldType.BOOLEAN -> "boolean"
            else -> "varchar"
        }
    }
}