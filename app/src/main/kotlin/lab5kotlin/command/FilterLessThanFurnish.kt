package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.exceptions.InvalidArgumentException
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import lab5kotlin.flat.Furnish
import lab5kotlin.human.Fatness
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Filter less than furnish
 *
 * @constructor Create empty Filter less than furnish
 */
class FilterLessThanFurnish : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    override fun execute(args: List<String>): Boolean {
        val furnish = args.firstOrNull()
        val validator = Validator(mapOf(
            "required" to false,
            "type" to FieldType.ENUM,
            "childEnum" to "Furnish",
            "childEnumVariants" to Furnish.values().map { it.toString() }
        ))
        if (!validator.validate(furnish))
            throw InvalidArgumentException("Furnish", validator.describe("Fatness"))

        this.writer.writeLine(collection.filterLessThanBy(furnish!!).toString())

        return true
}

}
