package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.sort.NameComparator
import lab5kotlin.human.Human
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Add if max command
 *
 * @constructor Create empty Add if max command
 */
open class AddIfMaxCommand : AddCommand() {
    private val entityBuilder: EntityBuilder<Human> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    private val writer: Writer by inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>): Boolean {
        this.writer.writeLine("Write down the fields values: ")
        val human = this.getEntityData(this.entityBuilder.fields).let { this.entityBuilder.build(it) }
        collection.addIfMax(NameComparator(), human)
        this.writer.writeLine("Item successfully created. Write down `show` to see collection")
        return true
    }
}