package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.Validator
import lab5kotlin.exceptions.ValidationFieldException
import lab5kotlin.io.Reader
import lab5kotlin.io.IOData
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject

/**
 * Add command
 *
 * @constructor Create empty Add command
 */
open class AddCommand : Command() {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    private val writer: Writer by inject(Writer::class.java, named("writer"))

    override val needObject: Boolean = true
    override val fields: Map<String, Validator> = entityBuilder.fields

    override fun execute(args: List<String>, data: MutableMap<String, Any?>): Boolean {
        val entity = this.entityBuilder.build(data)
        collection.addItem(entity)
        this.writer.writeLine("Item successfully created. Write down `show` to see collection")
        return true
    }
}