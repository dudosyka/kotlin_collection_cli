package multiproject.server.modules.human

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
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

    override fun countBy(command: CollectionCommand.CountByCommand): Int {
        TODO("Not yet implemented")
    }

    override suspend fun <T : Any> countBy(comparable: T): Int {
        return this.items.filter { it.id == (comparable as Int) }.size
    }

    override fun countLessThanBy(command: CollectionCommand.CountLessThanByCommand): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countLessThanBy(comparable: Any): Int {
        return this.items.filter { it.id < (comparable as Int) }.size
    }

    override fun filterLessThanBy(command: CollectionCommand.FilterLessThanByCommand): String {
        TODO("Not yet implemented")
    }

    override suspend fun filterLessThanBy(comparable: Any): String {
        this.items = this.items.filter { it.id > (comparable as Int) }.toMutableList()
        return this@HumanCollection.toString()
    }

    override fun addIfMax(command: CollectionCommand.AddIfMaxCommand): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addIfMax(item: Entity): Boolean {
        item as Human
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