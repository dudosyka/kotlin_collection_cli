package lab5kotlin.command

import lab5kotlin.collection.item.Validator
import lab5kotlin.io.IOData
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
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        val filePath = this.getArgument(args, "File path", 0, Validator(mapOf(
            "required" to true
        )))

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