package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class ClearCommand: Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    override fun execute(args: List<String>): Boolean {
        collection.clear()
        return true
    }
}