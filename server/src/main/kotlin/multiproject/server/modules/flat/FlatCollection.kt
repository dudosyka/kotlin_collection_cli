package multiproject.server.modules.flat

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import java.time.ZonedDateTime

class FlatCollection(override var items: MutableList<Flat>, builder: FlatBuilder) : Collection<Flat>(items, builder) {
    override fun sortBy(comparator: Comparator<Flat>) {
        this.items.sortWith(comparator)
    }

    override fun countBy(command: CollectionCommand.CountByCommand): Int {
        val time = command.lastAccessTimestamp
        val comparable = command.comparable
        lastAccessTimestamp = time
        val result = this.items.filter { it.numberOfRooms == comparable.toString().toLongOrNull() }.size
        command.response.complete(result)
        return result
    }

    override suspend fun <T : Any> countBy(comparable: T): Int {
        val command = CollectionCommand.CountByCommand(comparable, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    override fun countLessThanBy(command: CollectionCommand.CountLessThanByCommand): Int {
        val comparable = command.comparable
        val time = command.lastAccessTimestamp
        lastAccessTimestamp = time
        val compare = comparable.toString().toIntOrNull() ?: 0
        val result = this.items.filter { it.timeToMetroByTransport < compare }.size
        command.response.complete(result)
        return result
    }

    override suspend fun countLessThanBy(comparable: Any): Int {
        val command = CollectionCommand.CountLessThanByCommand(comparable, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    override fun filterLessThanBy(command: CollectionCommand.FilterLessThanByCommand): String {
        val comparable = command.comparable
        val time = command.lastAccessTimestamp
        val furnishValues = Furnish.values().map { it.toString() }
        val items = this.items.filter {
            return@filter try {
                val currFurnish = Furnish.valueOf(it.furnish.toString()).toString()
                val checkFurnish = Furnish.valueOf(comparable.toString()).toString()
                val currFurnishIndex = furnishValues.indexOf(currFurnish)
                val checkFurnishIndex = furnishValues.indexOf(checkFurnish)
                println("Compare $currFurnish $checkFurnish, $checkFurnishIndex >= $currFurnishIndex")
                checkFurnishIndex >= currFurnishIndex
            } catch (e: IllegalArgumentException) {
                false
            }
        }.toMutableList()
        println("Result ${items.size} size ${stringifyItems(items)}")
        lastAccessTimestamp = time
        command.response.complete(stringifyItems(items))
        return stringifyItems(items)
    }

    override suspend fun filterLessThanBy(comparable: Any): String {
        val command = CollectionCommand.FilterLessThanByCommand(comparable, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    override fun addIfMax(command: CollectionCommand.AddIfMaxCommand): Boolean {
        val item = command.item as Flat
        val time = command.lastAccessTimestamp
        this.sortBy(RoomsComparator().reversed())
        if (this.items.size <= 0) {
            this.addItem(item, time)
            return true
        }
        val maxRoom = this.items[0].numberOfRooms
        val provided = item.numberOfRooms
        val result = if (maxRoom < provided) {
            this.addItem(item, time)
            true
        } else {
            false
        }
        command.response.complete(result)
        return result
    }

    override suspend fun addIfMax(item: Entity): Boolean {
        val command = CollectionCommand.AddIfMaxCommand(item as Flat, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }
}