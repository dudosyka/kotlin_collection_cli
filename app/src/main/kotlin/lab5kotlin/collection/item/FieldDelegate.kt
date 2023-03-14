package lab5kotlin.collection.item

import lab5kotlin.exceptions.ValidationFieldException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Field delegate
 *
 * @param R
 * @property map
 * @property validator
 * @constructor Create empty Field delegate
 */
class FieldDelegate<R>(var map: MutableMap<String, Any?>, val validator: Validator): ReadWriteProperty<Nothing?, R?> {
    val key: (KProperty<*>) -> String = KProperty<*>::name
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): R? {
        return (map.getOrDefault(key(property), null) as R?)?: return null
    }

    override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: R?) {
        if (validator.validate(value)) {
            map[key(property)] = value
        }
        else
            throw ValidationFieldException(key(property), validator)
    }
}