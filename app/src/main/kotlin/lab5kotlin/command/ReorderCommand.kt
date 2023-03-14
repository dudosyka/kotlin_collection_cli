package lab5kotlin.command

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.sort.CollectionSortType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class ReorderCommand: ShowCommand() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(
        Collection::class.java,
        named("collection")
    )
    override fun execute(args: List<String>): Boolean {
        this.collection.sort(CollectionSortType.ASC)
        return super.execute(args)
    }
}