package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.exceptions.FileDumpException
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Save command
 *
 * @constructor Create empty Save command
 */
class SaveCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): Boolean {
        try {
            this.collection.dump()
            this.writer.writeLine("Collection successfully dumped!")
        } catch (e: FileDumpException) {
            this.writer.writeLine(e.message)
        }
        return true
    }
}