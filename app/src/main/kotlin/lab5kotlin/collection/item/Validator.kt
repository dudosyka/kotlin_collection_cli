package lab5kotlin.collection.item

class Validator(validationOptions: Map<String, Any?>) {
    private val withDefault = validationOptions.withDefault { null }

    private val max: Int? by withDefault
    private val min: Int? by withDefault
    private val type: FieldType? by withDefault
    private val required: Boolean? by withDefault
    private val childEntity: EntityBuilder<Entity>? by withDefault
    private val childEnumVariants: List<String>? by withDefault

    var value: Any? = null

    private fun validateNumber(type: FieldType, v: String): Boolean {
        val value = v.toLongOrNull() ?: return false

        if (type == FieldType.LONG)
            this.value = v.toLong()
        if (type == FieldType.INT)
            this.value = v.toInt()

        if (this.max != null)
            return value < this.max!!

        if (this.min != null)
            return value > this.min!!

        return true
    }

    private fun validateString(value: Any): Boolean {
        if (value is String) {
            if (this.max != null && value.length > this.max!!)
                return false
            if (this.min != null && value.length < this.min!!)
                return false
            return true
        }
        return false
    }

    private fun validateEnum(value: Any?): Boolean {
        if (this.childEnumVariants?.contains(value)!!) {
            this.value = value
            return true
        }
        return false
    }

    fun isNested(): EntityBuilder<Entity>? {
        return if (this.type != null && this.type == FieldType.ENTITY) this.childEntity else null
    }

    fun validate(value: Any?): Boolean {
        this.value = value

        if (this.required != null && this.required!! && (value == "" || value == null))
            return false

        if (this.required != null && !this.required!! && (value == "" || value == null)) {
            this.value = null
            return true
        }

        if (type == FieldType.ENUM)
            return this.validateEnum(value)

        if (type == FieldType.INT || type == FieldType.LONG)
            return this.validateNumber(type!!, value!!.toString())

        if (type == FieldType.STRING)
            return  this.validateString(value!!)

        return true
    }

    fun describe(fieldName: String): String {
        var output = fieldName
        if (required != null)
            output += "\nmust be not null"
        if (type != null)
            if (type == FieldType.ENUM)
                output += "\n must be one of $childEnumVariants"
            else
                output += "\nmust be $type"
        if (max != null)
            output += "\nmust be lower than $max"
        if (min !== null)
            output += "\nmust be grater than $min"

        return output
    }

    fun isChoisable(): Boolean {
        return type == FieldType.ENUM
    }

    fun variants(): String {
        return childEnumVariants.toString()
    }
}