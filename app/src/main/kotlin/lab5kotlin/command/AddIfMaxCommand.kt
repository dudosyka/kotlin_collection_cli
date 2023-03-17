package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.flat.Flat
import lab5kotlin.human.NameComparator
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
    private val entityBuilder: EntityBuilder<Flat> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    private val writer: Writer by inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>): Boolean {
        this.writer.writeLine("Write down the fields values: ")
        val entity = this.getEntityData(this.entityBuilder.fields).let { this.entityBuilder.build(it) }
        val result = collection.addIfMax(NameComparator(), entity)
        if (!result) {
            this.writer.writeLine("Failed number of rooms is lower than max in collection");
            return true;
        }
        this.writer.writeLine("Item successfully created. Write down `show` to see collection")
        return true
    }
}