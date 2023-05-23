package multiproject.server.modules.human

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ActorScope
import multiproject.server.collection.Collection
import java.time.ZonedDateTime

/**
 * Human collection
 *
 * @property items
 * @constructor Create empty Human collection
 */
class HumanCollection(override var items: MutableList<Human> = mutableListOf(), humanBuilder: HumanBuilder) : Collection<Human>(items, humanBuilder) {
    override fun sortBy(comparator: Comparator<Human>) {
        this.items.sortWith(comparator)
    }

    @ObsoleteCoroutinesApi
    override fun commandProcessor(command: CollectionCommand): ActorScope<CollectionCommand>.() -> Unit {
        TODO("Not yet implemented")
    }

    override suspend fun <T : Any> countBy(comparable: T): Int {
        return this.items.filter { it.id == (comparable as Int) }.size
    }

    override suspend fun countLessThanBy(comparable: Any): Int {
        return this.items.filter { it.id < (comparable as Int) }.size
    }

    override suspend fun filterLessThanBy(comparable: Any): String {
        this.items = this.items.filter { it.id > (comparable as Int) }.toMutableList()
        return this@HumanCollection.toString()
    }

    override suspend fun addIfMax(item: Human): Boolean {
        this.sortBy(NameComparator().reversed())
        if (this.items.size <= 0)
            return true
        val maxName = if (this.items[0].name != null) this.items[0].name!! else ""
        val providedName = if (item.name != null) item.name!! else ""
        return if (maxName.length < providedName.length) {
            this.addItem(item, ZonedDateTime.now())
            true
        } else {
            false
        }
    }

}