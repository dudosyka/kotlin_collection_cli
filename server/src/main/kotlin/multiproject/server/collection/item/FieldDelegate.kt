package multiproject.server.collection.item

import multiproject.lib.exceptions.ValidationFieldException
import multiproject.lib.dto.command.CommandArgumentDto
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import multiproject.lib.dto.command.Validator

/**
 * Field delegate
 *
 * @param R
 * @property map
 * @constructor Create empty Field delegate
 */
class FieldDelegate<R>(var map: MutableMap<String, Any?>, val argumentDto: CommandArgumentDto): ReadWriteProperty<Nothing?, R?> {
    val key: (KProperty<*>) -> String = KProperty<*>::name
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): R? {
        return (map.getOrDefault(key(property), null) as R?)?: return null
    }

    override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: R?) {
        val validator = Validator(argumentDto, value)
        if (validator.validate(value)) {
            map[key(property)] = value
        }
        else
            throw ValidationFieldException(key(property), validator)
    }
}