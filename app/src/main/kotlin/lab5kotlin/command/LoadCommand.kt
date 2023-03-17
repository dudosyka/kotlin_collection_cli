package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.exceptions.FileDumpException
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class LoadCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): Boolean {
        try {
            this.collection.loadDump()
            this.writer.writeLine("Collection successfully restored!")
        } catch (e: FileDumpException) {
            this.writer.writeLine(e.message)
        }
        return true
    }
}