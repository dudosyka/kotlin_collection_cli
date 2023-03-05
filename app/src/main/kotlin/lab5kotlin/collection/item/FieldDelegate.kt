package lab5kotlin.collection.item

import lab5kotlin.collection.exceptions.ValidationFieldException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FieldDelegate<R>(var map: MutableMap<String, Any?>, val validator: Validator): ReadWriteProperty<Any, R?> {
    val key: (KProperty<*>) -> String = KProperty<*>::name
    override fun getValue(thisRef: Any, property: KProperty<*>): R? {
        println("Value of ${key(property)} has set!")
        return (map.getOrDefault(key(property), null) as R?)?: return null
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: R?) {
        println("Value of ${key(property)} has set!")
        if (validator.validate(value)) {
            println("Successfully validated ${key(property)}")
            map[key(property)] = value
        }
        else
            throw ValidationFieldException(property.name, value)
    }
}