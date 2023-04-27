package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.exceptions.FileDumpException
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class LoadCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override val description: String = "Restore collection from the file"

    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        return try {
            this.collection.loadDump()
            CommandResult("Collection successfully restored!")
        } catch (e: FileDumpException) {
            CommandResult(e.message)
        }
    }
}