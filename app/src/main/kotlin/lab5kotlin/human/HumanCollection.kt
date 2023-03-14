package lab5kotlin.human

import lab5kotlin.collection.Collection
import lab5kotlin.collection.sort.NameComparator

class HumanCollection(override var items: MutableList<Human> = mutableListOf()) : Collection<Human>(items) {
    override fun sortBy(comparator: Comparator<Human>) {
        this.items.sortWith(comparator)
    }

    override fun <T : Any> countBy(comparable: T): Int {
        return this.items.filter { it.id == (comparable as Int) }.size
    }

    override fun countLessThanBy(comparable: Any): Int {
        return this.items.filter { it.id < (comparable as Int) }.size
    }

    override fun filterLessThanBy(comparable: Any): MutableList<Human> {
        return this.items.filter { it.id > (comparable as Int) }.toMutableList()
    }

    override fun addIfMax(comparable: Any, item: Human): Boolean {
        this.sortBy(NameComparator().reversed())
        if (this.items.size <= 0)
            return true
        val maxName = if (this.items[0].name != null) this.items[0].name!! else ""
        val providedName = if (item.name != null) item.name!! else ""
        return if (maxName.length < providedName.length) {
            this.addItem(item)
            true
        } else {
            false
        }
    }

}