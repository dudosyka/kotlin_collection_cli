package multiproject.server.collection

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.actor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommitDto
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.sort.CollectionSortType
import multiproject.server.collection.sort.IdComparator
import multiproject.server.dump.DumpManager
import multiproject.server.exceptions.NotUniqueException
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

/**
 * Collection
 *
 * @param T
 * @constructor Create empty Collection
 */
@Serializable
abstract class Collection<T : Entity> {
    protected abstract var items: MutableList<T>
    private var lastInsertId = 0
    private val dumpManager: DumpManager<T> by KoinJavaComponent.inject(
        DumpManager::class.java,
        named("dumpManager")
    )
    @Transient private val initializationTimestamp: ZonedDateTime = ZonedDateTime.now()
    @Transient protected var lastAccessTimestamp: ZonedDateTime = initializationTimestamp
    @Transient lateinit var builder: EntityBuilder<T>

    open class CollectionCommand(val lastAccessTimestamp: ZonedDateTime) {
        class AddItem(val item: Entity, lastAccessTimestamp: ZonedDateTime): CollectionCommand(lastAccessTimestamp)

        class RemoveItem(val index: Int, val response: CompletableDeferred<Long> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class RemoveItemById(val id: Int, val response: CompletableDeferred<Long> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class UpdateItem(val id: Int, val item: Entity, val response: CompletableDeferred<Boolean> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class GetItem(val index: Int, val response: CompletableDeferred<Entity> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class PullCommits(val commits: MutableMap<Long, CommitDto>, lastAccessTimestamp: ZonedDateTime):CollectionCommand(
            lastAccessTimestamp
        )

        class ShowItems(val response: CompletableDeferred<String> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class SortAndReturn(val sortType: CollectionSortType, val response: CompletableDeferred<String> = CompletableDeferred(),
                            lastAccessTimestamp: ZonedDateTime
        ): CollectionCommand(lastAccessTimestamp)

        class ClearItems(lastAccessTimestamp: ZonedDateTime) : CollectionCommand(lastAccessTimestamp)

        class Info(val response: CompletableDeferred<CollectionInfo> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime = ZonedDateTime.now()) : CollectionCommand(
            lastAccessTimestamp
        )

        class Dump(lastAccessTimestamp: ZonedDateTime) : CollectionCommand(lastAccessTimestamp)

        class LoadDump(lastAccessTimestamp: ZonedDateTime) : CollectionCommand(lastAccessTimestamp)
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    protected abstract fun commandProcessor(command: CollectionCommand): ActorScope<CollectionCommand>.() -> Unit

    constructor(items: MutableList<T>, builder: EntityBuilder<T>) {
        checkUniqueId(items)
        this.builder = builder
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Transient private val collectionActor = CoroutineScope(Job()).actor<CollectionCommand> {
        for (command in this) {
            when (command) {
                is CollectionCommand.AddItem -> run {
                    addItem(command.item as T, command.lastAccessTimestamp)
                }
                is CollectionCommand.RemoveItem -> run {
                    command.response.complete(removeAt(command.index, command.lastAccessTimestamp))
                }
                is CollectionCommand.RemoveItemById -> run {
                    val index = getIndexById(command.id)
                    command.response.complete(removeAt(index, command.lastAccessTimestamp))
                }
                is CollectionCommand.UpdateItem -> run {
                    command.response.complete(update(command.id, command.item as T, command.lastAccessTimestamp))
                }
                is CollectionCommand.GetItem -> run {
                    command.response.complete(items[command.index])
                }
                is CollectionCommand.PullCommits -> run {
                    pull(command.commits, command.lastAccessTimestamp)
                }
                is CollectionCommand.ShowItems -> run {
                    command.response.complete(this@Collection.toString())
                }
                is CollectionCommand.SortAndReturn -> run {
                    sort(command.sortType, command.lastAccessTimestamp)
                    command.response.complete(this@Collection.toString())
                }
                is CollectionCommand.ClearItems -> run {
                    this@Collection.items = mutableListOf()
                }
                is CollectionCommand.Info -> run {
                    command.response.complete(CollectionInfo(items.javaClass.simpleName.toString(), items.size, lastInsertId, lastAccessTimestamp, initializationTimestamp))
                }
                is CollectionCommand.Dump -> {
                    lastAccessTimestamp = ZonedDateTime.now()
                    dumpManager.dump(items)
                }
                is CollectionCommand.LoadDump -> {
                    lastAccessTimestamp = command.lastAccessTimestamp
                    val loaded = dumpManager.loadDump()
                    checkUniqueId(loaded)
                    this@Collection.items = loaded
                    sort(time = command.lastAccessTimestamp)
                    lastInsertId = if (items.size > 0)
                        items.last().id
                    else
                        0
                }
                else -> commandProcessor(command)
            }
        }
    }

    private fun checkUniqueId(items: MutableList<T>) {
        val set: Set<Int> = items.map { it.id }.toSortedSet()
        if (set.size < items.size)
            throw NotUniqueException("id")
    }


    private fun sort(type: CollectionSortType = CollectionSortType.DESC, time: ZonedDateTime) {
        lastAccessTimestamp = time
        if (type == CollectionSortType.DESC)
            this.items.sortWith(IdComparator())
        else
            this.items.sortWith(IdComparator().reversed())
    }

    /**
     * Sort
     *
     * @param type
     */
    suspend fun sort(type: CollectionSortType = CollectionSortType.DESC): String {
        val sortCommand = CollectionCommand.SortAndReturn(type, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(sortCommand)
        return sortCommand.response.await()
    }

    /**
     * Sort by
     *
     * @param comparator
     */
    abstract fun sortBy(comparator: Comparator<T>)


    /**
     * Get item
     *
     * @param index
     * @return
     */
    private fun getItem(index: Int): T {
        this.lastAccessTimestamp = ZonedDateTime.now()
        return this.items.elementAt(index)
    }

    /**
     * Get index by id
     *
     * @param id
     * @return
     */
    private fun getIndexById(id: Int): Int? {
        this.items.withIndex().forEach {
            if (it.value.id == id)
                return it.index
        }
        return null
    }

    protected fun addItem(collectionItem: T, time: ZonedDateTime) {
        this.lastAccessTimestamp = time
        this.items.add(collectionItem)
        this.sort(time = this.lastAccessTimestamp)
    }
    /**
     * Add item
     *
     * @param collectionItem
     */
    fun addItem(collectionItem: T) {
        collectionActor.trySend(CollectionCommand.AddItem(collectionItem, ZonedDateTime.now()))
    }

    suspend fun loadDump(time: ZonedDateTime) {
        this.lastAccessTimestamp = time
        val items = dumpManager.loadDump()
        checkUniqueId(items)
        this.items = items
        this.sort(time = time)
        if (this.items.size > 0)
            this.lastInsertId = this.items.last().id
        else
            this.lastInsertId = 0
    }

    /**
     * Load dump
     *
     */
    fun loadDump() {
        collectionActor.trySend(CollectionCommand.LoadDump(ZonedDateTime.now()))
    }


    private suspend fun dump(time: ZonedDateTime) {
        this.lastAccessTimestamp = time
        dumpManager.dump(this.items)
    }
    /**
     * Dump
     *
     */
    fun dump() {
        collectionActor.trySend(CollectionCommand.Dump(ZonedDateTime.now()))
    }

    private fun clear(time: ZonedDateTime) {
        this.lastAccessTimestamp = time
        this.items = mutableListOf()
    }
    /**
     * Clear
     *
     */
    fun clear() {
        collectionActor.trySend(CollectionCommand.ClearItems(ZonedDateTime.now()))
    }

    private fun removeAt(index: Int?, time: ZonedDateTime): Long {
        if (index == null)
            return 0
        this.lastAccessTimestamp = time
        return this.items.removeAt(index).id.toLong()
    }

    /**
     * Remove at
     *
     * @param index
     */
    suspend fun removeAt(index: Int): Long {
        val command = CollectionCommand.RemoveItem(index, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    /**
     * Remove by id
     *
     * @param id
     * @return
     */
    suspend fun removeById(id: Int): Long {
        val command = CollectionCommand.RemoveItemById(id, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    private fun checkIdExists(id: Int): Int {
        return this.getIndexById(id) ?: 0
    }

    private fun update(id: Int, item: T, time: ZonedDateTime): Boolean {
        lastAccessTimestamp = time
        val index = this.checkIdExists(id)
        if (index == 0)
            return false
        val old = this.getItem(index)
        item.id = old.id
        item.creationDate = old.creationDate
        this.items[index] = item
        return true
    }
    /**
     * Update
     *
     * @param id
     * @param item
     * @return
     */
    suspend fun update(id: Int, item: T): Boolean {
        val command = CollectionCommand.UpdateItem(id, item = item, lastAccessTimestamp = ZonedDateTime.now())
        return command.response.await()
    }

    /**
     * Collection info
     *
     * @property type
     * @property size
     * @property lastInsertId
     * @property lastAccessTimestamp
     * @property initializationTimestamp
     * @constructor Create empty Collection info
     */
    data class CollectionInfo(
        val type: String,
        val size: Int,
        val lastInsertId: Int,
        val lastAccessTimestamp: ZonedDateTime,
        val initializationTimestamp: ZonedDateTime,
    ) {
        override fun toString(): String {
            return "Collection info: {\n" +
                    "\tType: $type\n" +
                    "\tSize: $size\n" +
                    "\tLast insert ID: $lastInsertId\n" +
                    "\tLast access timestamp: $lastAccessTimestamp\n" +
                    "\tInitialization timestamp: $initializationTimestamp\n" +
                    "}\n"
        }
    }


    /**
     * Get info
     *
     * @return
     */
    suspend fun getInfo(): CollectionInfo {
        val command = CollectionCommand.Info()
        collectionActor.send(command)
        delay(10_000)
        return command.response.await()
    }

    override fun toString(): String {
        var result = ""
        return if (items.isNotEmpty()) {
            items.withIndex().forEach {
                result += "[Index: ${it.index}] ${it.value}\n"
            }
            result
        }
        else
            "Collection is empty"
    }

    /**
     * Count by
     *
     * @param T
     * @param comparable
     * @return
     */
    abstract suspend fun <T:Any> countBy(comparable: T): Int

    /**
     * Count less than by
     *
     * @param comparable
     * @return
     */
    abstract suspend fun countLessThanBy(comparable: Any): Int

    /**
     * Filter less than by
     *
     * @param comparable
     * @return
     */
    abstract suspend fun filterLessThanBy(comparable: Any): String

    abstract suspend fun addIfMax(item: T): Boolean

    private suspend fun pull(commitsById: MutableMap<Long, CommitDto>, time: ZonedDateTime) {
        commitsById.forEach {
            (key, value) -> run {
                val ids = items.mapIndexed { key, value -> value.id.toLong() to key }.toMap()
                if (ids.keys.contains(key)) {
                    if (value.data != null) {
                    items[ids[key]!!] = builder.build(value.data!!.toMutableMap())
                } else {
                        removeAt(ids[key]!!, time)
                    }
                } else {
                    if (value.data != null) {
                        items.add(builder.build(value.data!!.toMutableMap()))
                    }
                }
            }
        }

        lastAccessTimestamp = time
        dumpManager.dump(items)
    }

    fun pull(commits: List<CommitDto>) {
        val commitsById: MutableMap<Long, CommitDto> = mutableMapOf()
        commits.forEach {
            if (commitsById.keys.contains(it.id)) {
                if ((commitsById[it.id]?.timestamp ?: ZonedDateTime.now().toEpochSecond()) < it.timestamp)
                    commitsById[it.id] = it
            }
            else {
                commitsById[it.id] = it
            }
        }

        collectionActor.trySend(CollectionCommand.PullCommits(commitsById, lastAccessTimestamp = ZonedDateTime.now()))
    }
}