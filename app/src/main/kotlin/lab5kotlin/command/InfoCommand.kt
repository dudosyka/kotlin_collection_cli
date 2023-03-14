package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class InfoCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>): Boolean {
        this.writer.writeLine(collection.getInfo().toString())
        return true
    }
}