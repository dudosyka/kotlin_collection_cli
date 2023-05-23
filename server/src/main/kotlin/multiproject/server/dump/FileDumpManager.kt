package multiproject.server.dump

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import multiproject.server.collection.item.Entity
import multiproject.server.exceptions.FileDumpException
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

    override suspend fun loadDump(): MutableList<T> {
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

    override suspend fun dump(items: MutableList<T>) {
        try {
            val fileWriter = FileWriter(filePath)
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