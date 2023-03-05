package lab5kotlin.file

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import java.io.*

@OptIn(ExperimentalSerializationApi::class)
class FileManager<T : Entity> (val serializer: KSerializer<T>) {
    fun readDumpFile(filePath: String): Collection<T> {
        try {
            val inputStream = FileInputStream(filePath)
            val fileReader = BufferedReader(InputStreamReader(inputStream))
            val csv = Csv { hasHeaderRecord = true; ignoreUnknownColumns = true }

            try {
                val parsed = csv.decodeFromString(ListSerializer(serializer), fileReader.readText())
                println(parsed)
                return Collection(parsed.toMutableList())
            } catch (e: Exception) {
                println(e.message)
                println(e.localizedMessage)
                println(e)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return Collection(mutableListOf())
    }

    fun writeFileDump(filePath: String, collection: Collection<T>) {
        try {
            val outputStream = FileOutputStream(filePath)
            val fileWriter = BufferedWriter(OutputStreamWriter(outputStream))
            val csv = Csv { hasHeaderRecord = true; ignoreUnknownColumns = true }
            val encoded = csv.encodeToString(ListSerializer(serializer), collection.items.toList())
            fileWriter.use {
                out -> out.write(encoded)
            }
        } catch (e: IOException){
            println(e.message)
        }
    }
}