package multiproject.client.command

import multiproject.client.exceptions.ValidationFieldException
import multiproject.client.io.IOData
import multiproject.client.io.Reader
import multiproject.client.io.Writer
import multiproject.udpsocket.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class ObjectBuilder(private val fieldsMap: Map<String, CommandArgumentDto>) {
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    private val reader: Reader by KoinJavaComponent.inject(Reader::class.java, named("reader"))

    private fun getField(map: MutableMap<String, Any?>, key: String, argumentDto: CommandArgumentDto): MutableMap<String, Any?> {
        val isNested = argumentDto.nested
        if (isNested != null) {
            if (IOData.current == "console")
                this.writer.write("${key}:\n")
            val builder = ObjectBuilder(argumentDto.nested!!)
            map.put(key, builder.getEntityData())
            return map
        } else {
            if (IOData.current == "console")
                this.writer.write("${key}: ")
            if (argumentDto.choisable != null && IOData.current == "console")
                this.writer.write("(${argumentDto.choisable}) ")
            val value = this.reader.readLine()
            val validator = Validator(argumentDto, value)
            if (validator.validate(value)) {
                map.put(key, validator.value)
                return map
            }
            else {
                if (IOData.current == "console") {
                    this.writer.writeLine(validator.describe())
                    return this.getField(map, key, argumentDto)
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