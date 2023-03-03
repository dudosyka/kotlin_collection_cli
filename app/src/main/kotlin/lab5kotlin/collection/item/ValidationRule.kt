package lab5kotlin.collection.item

class ValidationRule(max: Long?, min: Long?, required: Boolean = false, checkChildEntity: String? = null, checkChildEnum: String? = null) {
    val max: Long?
    val min: Long?
    val required: Boolean
    val checkChildEntity: String?;
    val checkChildEnum: String?;

    init {
        this.max = max
        this.min = min
        this.required = required
        this.checkChildEntity = checkChildEntity;
        this.checkChildEnum = checkChildEnum;
    }

    private fun validateNumber(type: FieldType, value: Any): Boolean {
        if (type == FieldType.INT && value !is Int)
            return false

        if (type == FieldType.LONG && value !is Long)
            return false

        if (this.max != null) {
            when (value) {
                is Long -> return value < this.max
                is Int -> return value < this.max
            }
            return false
        }
        if (this.min != null) {
            when (value) {
                is Long -> return value > this.min
                is Int -> return value > this.min
            }
            return false
        }
        return true
    }

    private fun validateString(value: Any): Boolean {
        if (value is String) {
            if (this.max != null && value.length > this.max)
                return false
            if (this.min != null && value.length < this.min)
                return false
            return true
        }
        return false
    }

    fun validate(field: Field, value: Any?): Boolean {
        if (this.required && value == null)
            return false

        if (!this.required && value == null)
            return true

        if (field.type == FieldType.ENUM && checkChildEnum != null)
            return (value is Enum<*> && value::class.simpleName == checkChildEnum)

        if (field.type == FieldType.ENTITY && checkChildEntity != null)
            return (value is Entity && value::class.simpleName == checkChildEntity)

        if (field.type == FieldType.INT || field.type == FieldType.LONG)
            return this.validateNumber(field.type, value!!)

        if (field.type == FieldType.STRING)
            return  this.validateString(value!!)

        return true
    }
}
