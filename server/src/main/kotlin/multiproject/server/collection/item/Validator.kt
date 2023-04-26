package multiproject.server.collection.item

import multiproject.udpsocket.dto.command.CommandArgumentDto
import multiproject.udpsocket.dto.command.FieldType

class Validator (val argumentDto: CommandArgumentDto, var value: Any? = null) {

    private fun validateString(value: String): Boolean {
        if (argumentDto.max != null && value.length > argumentDto.max!!)
            return false
        return !(argumentDto.min != null && value.length < argumentDto.min!!)
    }

    private fun validateNumber(v: String): Boolean {
        val value = v.toDoubleOrNull() ?: return false

        if (argumentDto.type == FieldType.LONG)
            this.value = v.toLong()
        if (argumentDto.type == FieldType.INT)
            this.value = v.toInt()
        if (argumentDto.type == FieldType.FLOAT)
            this.value = v.toFloat()

        if (argumentDto.max != null)
            return value < argumentDto.max!!

        if (argumentDto.min != null)
            return value > argumentDto.min!!

        return true
    }

    private fun validateBoolean(value: String): Boolean {
        if (value.equals("true", ignoreCase = true)){
            this.value = true
            return true
        }
        if (value.equals("false", ignoreCase = true)){
            this.value = false
            return false
        }
        return false
    }

    private fun validateEnum(value: String): Boolean {
        return (argumentDto.choisable!!.contains(value))
    }

    fun validate(onValidate: Any?): Boolean {
        val value = onValidate?.toString()

        if (argumentDto.required && value.isNullOrEmpty())
            return false
        if (!argumentDto.required && value.isNullOrEmpty())
            return true



        this.value = value!!

        if (argumentDto.type == FieldType.STRING)
            return this.validateString(value)

        if (argumentDto.type == FieldType.NUMBER ||
            argumentDto.type == FieldType.FLOAT ||
            argumentDto.type == FieldType.LONG ||
            argumentDto.type == FieldType.INT)
            return this.validateNumber(value)

        if (argumentDto.type == FieldType.BOOLEAN)
            return this.validateBoolean(value)

        if (argumentDto.type == FieldType.ENUM)
            return this.validateEnum(value)

        return true
    }

    fun describe(): String {
        var output = argumentDto.name
        if (argumentDto.inline)
            output = "Inline argument ${argumentDto.name} (index=${argumentDto.index})"
        if (argumentDto.required)
            output += "\nmust be not null"
        output += if (argumentDto.type == FieldType.ENUM)
            "\n must be one of ${argumentDto.choisable}"
        else
            "\nmust be ${argumentDto.type}"
        if (argumentDto.max != null)
            output += "\nmust be lower than ${argumentDto.max}"
        if (argumentDto.min !== null)
            output += "\nmust be grater than ${argumentDto.min}"

        return output!!
    }
}