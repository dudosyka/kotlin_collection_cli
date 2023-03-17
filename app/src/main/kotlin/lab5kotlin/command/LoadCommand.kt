package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.exceptions.FileDumpException
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class LoadCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): CommandResult {
        return try {
            this.collection.loadDump()
            CommandResult("Collection successfully restored!")
        } catch (e: FileDumpException) {
            CommandResult(e.message)
        }
    }
}