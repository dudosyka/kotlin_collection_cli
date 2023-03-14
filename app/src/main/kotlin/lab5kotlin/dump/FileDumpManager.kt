package lab5kotlin.dump

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import lab5kotlin.exceptions.NotUniqueIdException
import lab5kotlin.collection.item.Entity
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.io.*

/**
 * File dump manager
 *
 * @param T
 * @property filePath
 * @property serializer
 * @constructor Create empty File dump manager
 */
@OptIn(ExperimentalSerializationApi::class)
class FileDumpManager<T : Entity> (private val filePath: String, private val serializer: KSerializer<T>): DumpManager<T>() {
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))

    override fun loadDump(): MutableList<T> {
        try {
            val inputStream = FileInputStream(filePath)
            val fileReader = BufferedReader(InputStreamReader(inputStream))
            val csv = Csv { hasHeaderRecord = true; ignoreUnknownColumns = true }
            val parsed = csv.decodeFromString(ListSerializer(serializer), fileReader.readText())
            return parsed.toMutableList()
        } catch (e: IOException) {
            this.writer.writeLine("Error! Failed read from file! ${e.message}")
        } catch (e: NotUniqueIdException) {
            this.writer.writeLine("Error! There are duplicates id in file.")
        }
        return mutableListOf()
    }

    override fun dump(items: MutableList<T>) {
        try {
            val outputStream = FileOutputStream(filePath)
            val fileWriter = BufferedWriter(OutputStreamWriter(outputStream))
            val csv = Csv { hasHeaderRecord = true; ignoreUnknownColumns = true }
            val encoded = csv.encodeToString(ListSerializer(serializer), items.toList())
            fileWriter.use {
                out -> out.write(encoded)
            }
        } catch (e: IOException){
            this.writer.writeLine("Error! Failed read from file! (${e.message})")
        }
    }
}