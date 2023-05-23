package multiproject.server.modules.flat

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ActorScope
import multiproject.server.collection.Collection
import java.time.ZonedDateTime

class FlatCollection(override var items: MutableList<Flat>, builder: FlatBuilder) : Collection<Flat>(items, builder) {

    override fun sortBy(comparator: Comparator<Flat>) {
        this.items.sortWith(comparator)
    }

    class CountLessThanByCommand(val comparable: Any, val response: CompletableDeferred<Int> = CompletableDeferred(),
                                 lastAccessTimestamp: ZonedDateTime
    ): CollectionCommand(lastAccessTimestamp)
    class FilterLessThanByCommand(val comparable: Any, val response: CompletableDeferred<String> = CompletableDeferred(),
                                  lastAccessTimestamp: ZonedDateTime
    ): CollectionCommand(lastAccessTimestamp)
    class AddIfMaxCommand(val item: Flat, val response: CompletableDeferred<Boolean> = CompletableDeferred(),
                          lastAccessTimestamp: ZonedDateTime
    ): CollectionCommand(lastAccessTimestamp)
    class CountByCommand(val comparable: Any, val response: CompletableDeferred<Int> = CompletableDeferred(),
                         lastAccessTimestamp: ZonedDateTime
    ): CollectionCommand(lastAccessTimestamp)

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun commandProcessor(command: CollectionCommand): ActorScope<CollectionCommand>.() -> Unit {
        return {
            when(command) {
                is CountLessThanByCommand -> run {
                    command.response.complete(countLessThanBy(command.comparable, command.lastAccessTimestamp))
                }
                is FilterLessThanByCommand -> run {
                    filterLassThanBy(command.comparable, command.lastAccessTimestamp)
                    command.response.complete(this@FlatCollection.toString())
                }
                is AddIfMaxCommand -> run {
                    command.response.complete(addIfMax(command.item, command.lastAccessTimestamp))
                }
                is CountByCommand -> run {
                    command.response.complete(countBy(command.comparable, command.lastAccessTimestamp))
                }
                else -> run {}
            }
        }
    }

    private fun countBy(comparable: Any, time: ZonedDateTime): Int {
        lastAccessTimestamp = time
        return this.items.filter { it.numberOfRooms == comparable.toString().toLongOrNull() }.size
    }

    override suspend fun <T : Any> countBy(comparable: T): Int {
        val command = CountByCommand(comparable, lastAccessTimestamp = ZonedDateTime.now())
        return command.response.await()
    }

    private fun countLessThanBy(comparable: Any, time: ZonedDateTime): Int {
        lastAccessTimestamp = time
        val compare = comparable.toString().toIntOrNull() ?: 0
        return this.items.filter { it.timeToMetroByTransport < compare }.size
    }

    override suspend fun countLessThanBy(comparable: Any): Int {
        val command = CountLessThanByCommand(comparable, lastAccessTimestamp = ZonedDateTime.now())
        return command.response.await()
    }

    private fun filterLassThanBy(comparable: Any, time: ZonedDateTime) {
        val furnishValues = Furnish.values().map { it.toString() }
        this.items = this.items.filter {
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
        lastAccessTimestamp = time
    }

    override suspend fun filterLessThanBy(comparable: Any): String {
        val command = FilterLessThanByCommand(comparable, lastAccessTimestamp = ZonedDateTime.now())
        return command.response.await()
    }

    private fun addIfMax(item: Flat, time: ZonedDateTime): Boolean {
        this.sortBy(RoomsComparator().reversed())
        if (this.items.size <= 0) {
            this.addItem(item, time)
            return true
        }
        val maxRoom = this.items[0].numberOfRooms
        val providedName = item.numberOfRooms
        return if (maxRoom < providedName) {
            this.addItem(item, time)
            true
        } else {
            false
        }
    }

    override suspend fun addIfMax(item: Flat): Boolean {
        val command = AddIfMaxCommand(item, lastAccessTimestamp = ZonedDateTime.now())
        return command.response.await()
    }
}