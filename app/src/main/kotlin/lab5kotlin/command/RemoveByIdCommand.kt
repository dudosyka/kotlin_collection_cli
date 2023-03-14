package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class RemoveByIdCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))

    override fun execute(args: List<String>): Boolean {
        val id = this.getArgument(args, "id", 0, Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        )))
        collection.removeById(id as Int)
        this.writer.writeLine("Item with id = $id successfully removed!")
        return true
    }
}