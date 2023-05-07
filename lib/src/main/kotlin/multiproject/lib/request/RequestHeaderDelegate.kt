package multiproject.lib.request

import multiproject.lib.dto.request.RequestDto
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RequestHeaderDelegate<R>(val request: RequestDto?):
    ReadWriteProperty<Request?, R?> {
    val key: (KProperty<*>) -> String = KProperty<*>::name
    override fun getValue(thisRef: Request?, property: KProperty<*>): R? {
        if (request == null)
            return null;
        return (request.headers.getOrDefault(key(property), null) as R?);
    }

    /**
     * Sets the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @param value the value to set.
     */
    override fun setValue(thisRef: Request?, property: KProperty<*>, value: R?) {}
}