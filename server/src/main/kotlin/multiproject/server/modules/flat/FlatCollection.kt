package multiproject.server.modules.flat

import multiproject.server.collection.Collection

class FlatCollection(override var items: MutableList<Flat>, builder: FlatBuilder) : Collection<Flat>(items, builder) {

    override fun sortBy(comparator: Comparator<Flat>) {
        this.items.sortWith(comparator)
    }

    override fun <T : Any> countBy(comparable: T): Int {
        return this.items.filter { it.numberOfRooms == comparable.toString().toLongOrNull() }.size
    }

    override fun countLessThanBy(comparable: Any): Int {
        val compare = comparable.toString().toIntOrNull() ?: 0
        return this.items.filter { it.timeToMetroByTransport < compare }.size
    }

    override fun filterLessThanBy(comparable: Any): MutableList<Flat> {
        val furnishValues = Furnish.values().map { it.toString() }
        return this.items.filter {
            return@filter try {
                val currFurnish = Furnish.valueOf(it.furnish.toString()).toString()
                val checkFurnish = Furnish.valueOf(comparable.toString()).toString()
                val currFurnishIndex = furnishValues.indexOf(currFurnish)
                val checkFurnishIndex = furnishValues.indexOf(checkFurnish)
                checkFurnishIndex <= currFurnishIndex
            } catch (e: IllegalArgumentException) {
                false
            }
        }.toMutableList()
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