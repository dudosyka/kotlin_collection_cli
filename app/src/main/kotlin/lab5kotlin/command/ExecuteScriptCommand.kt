package lab5kotlin.command

import lab5kotlin.collection.item.Validator
import lab5kotlin.io.IOData
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
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
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): Boolean {
        val filePath = this.getArgument(args, "File path", 0, Validator(mapOf(
            "required" to true
        )))

        try {
            val inputStream = FileInputStream(filePath as String)
            val fileReader = BufferedReader(InputStreamReader(inputStream))
            IOData.current = "file"
            IOData.fileReader = fileReader
            if (IOData.commandHistory.isEmpty()) {
                IOData.commandHistory = mutableListOf(
                    "execute_script $filePath"
                )
            }
        } catch (e: FileNotFoundException) {
            this.writer.writeLine("Error! ${e.message}");
        } catch (e: Exception) {
            this.writer.writeLine("Failed run script!");
        }

        return true
    }
}