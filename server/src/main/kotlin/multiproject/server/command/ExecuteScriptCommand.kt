package multiproject.server.command

import multiproject.server.collection.item.Validator
import multiproject.server.io.IOData
import multiproject.udpsocket.dto.command.CommandArgumentDto
import multiproject.udpsocket.dto.command.FieldType
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Execute script command
 *
 * @constructor Create empty Execute script command
 */
class ExecuteScriptCommand: Command() {
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val filePath = this.getArgument(args, "File path", 0, Validator(
            CommandArgumentDto(name = "file_path", required = true)
        ))

        return try {
            val inputStream = FileInputStream(filePath as String)
            val fileReader = BufferedReader(InputStreamReader(inputStream))
            IOData.current = "file"
            IOData.fileReader = fileReader
            if (IOData.commandHistory.isEmpty()) {
                IOData.commandHistory = mutableListOf(
                    "execute_script $filePath"
                )
            }
            CommandResult("")
        } catch (e: FileNotFoundException) {
            CommandResult("Error! ${e.message}", false)
        } catch (e: Exception) {
            CommandResult("Failed run script!", false)
        }
    }
}