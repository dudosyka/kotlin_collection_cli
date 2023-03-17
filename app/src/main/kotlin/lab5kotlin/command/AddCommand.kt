package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.Validator
import lab5kotlin.exceptions.ValidationFieldException
import lab5kotlin.human.Human
import lab5kotlin.io.Reader
import lab5kotlin.io.IOData
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Add command
 *
 * @constructor Create empty Add command
 */
open class AddCommand : Command() {
    private val entityBuilder: EntityBuilder<Human> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    private val writer: Writer by inject(Writer::class.java, named("writer"))
    private val reader: Reader by inject(Reader::class.java, named("reader"))

    private fun getField(map: MutableMap<String, Any?>, key: String, validator: Validator): MutableMap<String, Any?> {
        val isNested = validator.isNested()
        if (isNested != null) {
            if (IOData.current == "console")
                this.writer.write("${key}:\n")
            map.put(key, this.getEntityData(isNested.fields))
            return map
        }
        else {
            if (IOData.current == "console")
                this.writer.write("${key}: ")
            if (validator.isChoisable() && IOData.current == "console")
                this.writer.write("(${validator.variants()}) ")
            val value = this.reader.readLine()
            if (validator.validate(value)) {
                map.put(key, validator.value)
                return map
            }
            else {
                throw ValidationFieldException(key, validator)
            }
        }
    }

    /**
     * Get entity data
     *
     * @param fieldsMap
     * @return
     */
    protected fun getEntityData(fieldsMap: Map<String, Validator>): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        fieldsMap.map {
            map = this.getField(map, it.key, it.value)
        }
        return map
    }
    override fun execute(args: List<String>): Boolean {
        if (IOData.current == "console")
            this.writer.writeLine("Write down the fields values: ")
        val human = this.getEntityData(this.entityBuilder.fields).let { this.entityBuilder.build(it) }
        collection.addItem(human)
        this.writer.writeLine("Item successfully created. Write down `show` to see collection")
        return true
    }
}