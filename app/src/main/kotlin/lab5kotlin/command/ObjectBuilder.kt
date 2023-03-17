package lab5kotlin.command

import lab5kotlin.collection.item.Validator
import lab5kotlin.exceptions.ValidationFieldException
import lab5kotlin.io.IOData
import lab5kotlin.io.Reader
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class ObjectBuilder(private val fieldsMap: Map<String, Validator>) {
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    private val reader: Reader by KoinJavaComponent.inject(Reader::class.java, named("reader"))

    private fun getField(map: MutableMap<String, Any?>, key: String, validator: Validator): MutableMap<String, Any?> {
        val isNested = validator.isNested()
        if (isNested != null) {
            if (IOData.current == "console")
                this.writer.write("${key}:\n")
            val builder = ObjectBuilder(isNested.fields);
            map.put(key, builder.getEntityData())
            return map
        }
        else {
            if (IOData.current == "console")
                this.writer.write("${key}: ")
            if (validator.isChoisable() && IOData.current == "console")
                this.writer.write("(${validator.variants()}) ")
            val value = this.reader.readLine()
            if (validator.validate(value)) {
                map.put(key, validator.value)
                return map
            }
            else {
                if (IOData.current == "console") {
                    this.writer.writeLine(validator.describe(key))
                    return this.getField(map, key, validator)
                }
                else
                    throw ValidationFieldException(key, validator)
            }
        }
    }

    /**
     * Get entity data
     *
     * @param fieldsMap
     * @return
     */
    fun getEntityData(): MutableMap<String, Any?> {
        if (IOData.current == "console")
            this.writer.writeLine("Write down the fields values: ")
        var map = mutableMapOf<String, Any?>()
        fieldsMap.map {
            map = this.getField(map, it.key, it.value)
        }
        return map
    }
}