package lab5kotlin.flat

import lab5kotlin.collection.Collection

class FlatCollection(override var items: MutableList<Flat>) : Collection<Flat>(items) {
    override fun sortBy(comparator: Comparator<Flat>) {
        this.items.sortWith(comparator)
    }

    override fun <T : Any> countBy(comparable: T): Int {
        return this.items.filter { it.id == (comparable as Int) }.size
    }

    override fun countLessThanBy(comparable: Any): Int {
        return this.items.filter { it.id < (comparable as Int) }.size
    }

    override fun filterLessThanBy(comparable: Any): MutableList<Flat> {
        return this.items.filter { it.id > (comparable as Int) }.toMutableList()
    }

    override fun addIfMax(comparable: Any, item: Flat): Boolean {
        this.sortBy(RoomsComparator().reversed())
        if (this.items.size <= 0)
            return true
        val maxRoom = this.items[0].numberOfRooms
        val providedName = item.numberOfRooms
        return if (maxRoom < providedName) {
            this.addItem(item)
            true
        } else {
            false
        }
    }
}