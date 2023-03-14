package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class RemoveAtCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>): Boolean {
        val index = this.getArgument(args, "index", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))
        collection.removeAt(index as Int)
        this.writer.writeLine("Item with index $index successfully removed!")
        return true
    }
}