package multiproject.lib.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ReadOnlyInitializer<T> : ReadWriteProperty<Any, T> {

    private enum class Val { EMPTY }

    private var value: Any? = Val.EMPTY

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value != Val.EMPTY)
            return value as T

        throw IllegalStateException("Value must be initialized")
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (this.value == Val.EMPTY)
            throw IllegalStateException("Value has already initialized!")

        this.value = value
    }
}