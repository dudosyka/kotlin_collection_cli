package lab5kotlin.dump

import kotlinx.serialization.SerializationException
import lab5kotlin.exceptions.FileDumpException
import lab5kotlin.human.Human
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.nio.file.Paths
import kotlin.test.assertEquals

class FileDumpManagerTest {
    private fun createManager(relativePath: String): FileDumpManager<Human> {
        val path = Paths.get("").toAbsolutePath().toString()
        return FileDumpManager("$path$relativePath", Human.serializer())
    }
    @Test
    fun testLoadInvalidFileValue() {
        val fileDumpManager = createManager("/../invalidData.csv")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.loadDump() }
        assertEquals(SerializationException::class, exception.parent::class)
    }

    @Test
    fun testLoadFileNotFound() {
        val fileDumpManager = createManager("/no/such/file/")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.loadDump() }
        assertEquals(FileNotFoundException::class, exception.parent::class)
    }

    @Test
    fun testLoadFileIsDirectory() {
        val fileDumpManager = createManager("")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.loadDump() }
        assertEquals(FileNotFoundException::class, exception.parent::class)
        assertEquals("${fileDumpManager.filePath} (Is a directory)", exception.parent.message)
    }

    @Test
    fun testLoadFilePermissionDenied() {
        val fileDumpManager = createManager("/../permission.csv")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.loadDump() }
        assertEquals(FileNotFoundException::class, exception.parent::class)
        assertEquals("${fileDumpManager.filePath} (Permission denied)", exception.parent.message)
    }

    @Test
    fun testDumpToFileNotFound() {
        val fileDumpManager = createManager("/no/such/file/")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.dump(
            mutableListOf()
        ) }
        assertEquals(FileNotFoundException::class, exception.parent::class)
    }

    @Test
    fun testDumpToDirectory() {
        val fileDumpManager = createManager("")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.dump(
            mutableListOf()
        ) }
        assertEquals(FileNotFoundException::class, exception.parent::class)
        assertEquals("${fileDumpManager.filePath} (Is a directory)", exception.parent.message)
    }

    @Test
    fun testDumpToPermissionDeniedFile() {
        val fileDumpManager = createManager("/../permission.csv")
        val exception: FileDumpException = assertThrows(FileDumpException::class.java) { fileDumpManager.dump(
            mutableListOf()
        ) }
        assertEquals(FileNotFoundException::class, exception.parent::class)
        assertEquals("${fileDumpManager.filePath} (Permission denied)", exception.parent.message)
    }
}