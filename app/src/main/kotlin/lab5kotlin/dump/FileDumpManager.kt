package lab5kotlin.dump

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import lab5kotlin.collection.item.Entity
import lab5kotlin.exceptions.FileDumpException
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
class FileDumpManager<T : Entity> (val filePath: String, private val serializer: KSerializer<T>): DumpManager<T>() {

    override fun loadDump(): MutableList<T> {
        try {
            val inputStream = FileInputStream(filePath)
            val fileReader = BufferedReader(InputStreamReader(inputStream))
            val csv = Csv { hasHeaderRecord = true; ignoreUnknownColumns = true }
            val parsed = csv.decodeFromString(ListSerializer(serializer), fileReader.readText())
            return parsed.toMutableList()
        } catch (e: IOException) {
            throw FileDumpException(e, this.filePath, "Error! Failed read from file! ${e.message}")
        } catch (e: FileNotFoundException) {
            throw FileDumpException(e, this.filePath, "Error! ${e.message}")
        } catch (e: SerializationException) {
            throw FileDumpException(e, this.filePath, "Error! File can`t be loaded. Invalid value! ")
        } catch (e: Exception) {
            throw FileDumpException(e, this.filePath, "Error! ${e.message}")
        }
    }

    override fun dump(items: MutableList<T>) {
        try {
            val fileWriter = FileWriter(filePath)
//            val outputStream = FileOutputStream(filePath)
//            val fileWriter = BufferedWriter(OutputStreamWriter(outputStream))
            val csv = Csv { hasHeaderRecord = true; ignoreUnknownColumns = true }
            val encoded = csv.encodeToString(ListSerializer(serializer), items.toList())
            fileWriter.use {
                out -> out.write(encoded)
            }
        } catch (e: FileNotFoundException) {
            throw FileDumpException(e, this.filePath, "Error! ${e.message}")
        }
    }
}