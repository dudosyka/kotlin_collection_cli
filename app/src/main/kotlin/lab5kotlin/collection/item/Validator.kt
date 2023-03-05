package lab5kotlin.collection.item

class Validator(validationOptions: Map<String, Any?>) {
    private val max: Int? by validationOptions
    private val min: Int? by validationOptions
    private val type: FieldType? by validationOptions
    private val required: Boolean? by validationOptions
    private val checkChildEntity: String? by validationOptions
    private val checkChildEnum: String? by validationOptions

    private fun validateNumber(type: FieldType, value: Any): Boolean {
        if (type == FieldType.INT && value !is Int)
            return false

        if (type == FieldType.LONG && value !is Long)
            return false

        if (this.max != null) {
            when (value) {
                is Long -> return value < this.max!!
                is Int -> return value < this.max!!
            }
            return false
        }
        if (this.min != null) {
            when (value) {
                is Long -> return value > this.min!!
                is Int -> return value > this.min!!
            }
            return false
        }
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

    fun validate(value: Any?): Boolean {
        if (this.required != null && this.required!! && value == null)
            return false

        if (this.required != null && !this.required!! && value == null)
            return true

        if (type == FieldType.ENUM)
            return (value is Enum<*> && value::class.simpleName == checkChildEnum)

        if (type == FieldType.ENTITY)
            return (value is Entity && value::class.simpleName == checkChildEntity)

        if (type == FieldType.INT || type == FieldType.LONG)
            return this.validateNumber(type!!, value!!)

        if (type == FieldType.STRING)
            return  this.validateString(value!!)

        return true
    }
}