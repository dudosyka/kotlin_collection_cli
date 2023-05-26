package multiproject.client.command

import multiproject.client.exceptions.ValidationException
import multiproject.client.io.IOData
import multiproject.client.io.Reader
import multiproject.client.io.Writer
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.Validator
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class ObjectBuilder(private val fieldsMap: Map<String, CommandArgumentDto>) {
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    private val reader: Reader by KoinJavaComponent.inject(Reader::class.java, named("reader"))
    private val logger: Logger by KoinJavaComponent.inject(Logger::class.java, named("logger"))

    private fun getField(map: MutableMap<String, Any?>, key: String, argumentDto: CommandArgumentDto): MutableMap<String, Any?> {
        val isNested = argumentDto.nested
        if (!argumentDto.show)
            return map
        if (isNested != null) {
            if (IOData.current == "console")
                this.writer.write("${key}:\n")
            val builder = ObjectBuilder(argumentDto.nested!!)
            map[key] = builder.getEntityData()
            return map
        } else {
            if (IOData.current == "console")
                this.writer.write("${key}: ")
            if (argumentDto.choisable != null && IOData.current == "console")
                this.writer.write("(${argumentDto.choisable}) ")
            val value = this.reader.readLine()
            val validator = Validator(argumentDto, value)
            return if (validator.validate(value)) {
                map[key] = validator.value
                map
            } else {
                if (IOData.current == "console") {
                    this.writer.writeLine(validator.describe())
                    this.getField(map, key, argumentDto)
                } else
                    throw ValidationException(validator)
            }
        }
    }

    /**
     * Get entity data
     *
     * @return
     */
    fun getEntityData(): MutableMap<String, Any?> {
        if (IOData.current == "console")
            this.writer.writeLine("Write down the fields values: ")
        var map = mutableMapOf<String, Any?>()
        fieldsMap.map {
            map = this.getField(map, it.key, it.value)
        }
        logger(LogLevel.DEBUG, "Built data: $map")
        return map
    }
}