package multiproject.client.command

import multiproject.client.io.Writer
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class InlineArgumentsValidator(private val arguments: List<String>, private val argumentDtoList: Map<String, CommandArgumentDto>) {
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    fun getArguments(): List<Any>? {
        if (arguments.size != argumentDtoList.size)
            return null

        return argumentDtoList.map {
            val argInput: String = arguments[it.value.index!!]
            val validator = Validator(it.value)
            val validatorRes = validator.validate(argInput)
            if (validatorRes) {
                return@map validator.value!!
            } else {
                this.writer.writeLine(validator.describe())
                return null
            }
        }
    }
}