package lab5kotlin.collection

import lab5kotlin.dump.DumpManager
import lab5kotlin.dump.FileDumpManager
import lab5kotlin.exceptions.FileDumpException
import lab5kotlin.exceptions.NotUniqueIdException
import lab5kotlin.human.Human
import lab5kotlin.human.HumanCollection
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.FileNotFoundException
import java.nio.file.Paths

class CollectionTest {
    @Test
    fun testLoadDuplicates() {
        val path = Paths.get("").toAbsolutePath().toString()
        val module = module {
            single<DumpManager<Human>>(named("dumpManager")) {
                FileDumpManager("$path/../duplicateIds.csv", Human.serializer())
            }
        }
        GlobalContext.startKoin {
            modules(
                module
            )
        }
        val collection = HumanCollection()
        assertThrows(NotUniqueIdException::class.java) { collection.loadDump() }
    }

    @Test
    fun testDumpToNotFoundFile() {
        val path = Paths.get("").toAbsolutePath().toString()
        val module = module {
            single<DumpManager<Human>>(named("dumpManager")) {
                FileDumpManager("$path/../duplicate/Ids.csv", Human.serializer())
            }
        }
        stopKoin()
        GlobalContext.startKoin {
            modules(
                module
            )
        }
        val collection = HumanCollection()
        val exception = assertThrows(FileDumpException::class.java) { collection.dump() }
        assertEquals(FileNotFoundException::class, exception.parent::class)
    }
}