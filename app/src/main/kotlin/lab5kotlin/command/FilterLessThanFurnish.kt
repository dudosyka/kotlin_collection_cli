package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.exceptions.InvalidArgumentException
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import lab5kotlin.human.Fatness
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class FilterLessThanFurnish : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
//    private val collectionPrinter = CollectionPrinter()
    override fun execute(args: List<String>): Boolean {
        val fatness = args.firstOrNull()
    val validator = Validator(mapOf(
            "required" to false,
            "type" to FieldType.ENUM,
            "childEnum" to "Fatness",
            "childEnumVariants" to Fatness.values().map { it.toString() }
        ))
        if (!validator.validate(fatness))
            throw InvalidArgumentException("Fatness", validator.describe("Fatness"))

        this.writer.writeLine(collection.countLessThanBy(fatness!!).toString())

        return true
}

}
