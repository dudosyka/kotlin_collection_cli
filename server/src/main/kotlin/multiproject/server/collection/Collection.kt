package multiproject.server.collection

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.sort.CollectionSortType
import multiproject.server.collection.sort.IdComparator
import multiproject.server.dump.DumpManager
import multiproject.server.exceptions.execution.NotUniqueException
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
    private val logger: Logger by KoinJavaComponent.inject(Logger::class.java, named("logger"))

    open class CollectionCommand(val lastAccessTimestamp: ZonedDateTime) {
        class AddItem(val item: Entity, lastAccessTimestamp: ZonedDateTime): CollectionCommand(lastAccessTimestamp)

        class RemoveItem(val index: Int, val author: Long, val response: CompletableDeferred<Long> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class RemoveItemById(val id: Int, val author: Long, val response: CompletableDeferred<Long> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class UpdateItem(val id: Int, val author: Long, val item: Entity, val response: CompletableDeferred<Boolean> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class GetItem(val index: Int, val response: CompletableDeferred<Entity> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
            lastAccessTimestamp
        )

        class GetItemById(val id: Int, val response: CompletableDeferred<Entity?> = CompletableDeferred(), lastAccessTimestamp: ZonedDateTime): CollectionCommand(
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

        class CountLessThanByCommand(val comparable: Any, val response: CompletableDeferred<Int> = CompletableDeferred(),
                                     lastAccessTimestamp: ZonedDateTime
        ): CollectionCommand(lastAccessTimestamp)
        class FilterLessThanByCommand(val comparable: Any, val response: CompletableDeferred<String> = CompletableDeferred(),
                                      lastAccessTimestamp: ZonedDateTime
        ): CollectionCommand(lastAccessTimestamp)
        class AddIfMaxCommand(val item: Entity, val response: CompletableDeferred<Boolean> = CompletableDeferred(),
                              lastAccessTimestamp: ZonedDateTime
        ): CollectionCommand(lastAccessTimestamp)
        class CountByCommand(val comparable: Any, val response: CompletableDeferred<Int> = CompletableDeferred(),
                             lastAccessTimestamp: ZonedDateTime
        ): CollectionCommand(lastAccessTimestamp)
    }

    constructor(items: MutableList<T>, builder: EntityBuilder<T>) {
        checkUniqueId(items)
        this.builder = builder
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Transient protected val collectionActor = CoroutineScope(Job()).actor<CollectionCommand> {
        for (command in this) {
            when (command) {
                is CollectionCommand.AddItem -> run {
                    addItem(command.item as T, command.lastAccessTimestamp)
                }
                is CollectionCommand.RemoveItem -> run {
                    if (command.index < 0 || command.index >= items.size) {
                        command.response.complete(-1)
                        return@run
                    }
                    val item = items[command.index]
                    val authorData = (item.pureData["author"] as MutableMap<String, Any>)
                    if (authorData["id"].toString().toLong() == command.author)
                        command.response.complete(removeAt(command.index, command.lastAccessTimestamp))
                    else
                        command.response.complete(0)
                }
                is CollectionCommand.RemoveItemById -> run {
                    val index = getIndexById(command.id)
                    if (index == null) {
                        command.response.complete(-1)
                        return@run
                    }

                    val authorData = (items[index].pureData["author"] as MutableMap<String, Any>)

                    if (authorData["id"].toString().toLong() == command.author)
                        command.response.complete(removeAt(index, command.lastAccessTimestamp))
                    else {
                        command.response.complete(0)
                    }
                }
                is CollectionCommand.UpdateItem -> run {
                    val index = getIndexById(command.id)
                    if (index == null) {
                        command.response.complete(false)
                        return@run
                    }
                    val authorData = (items[index].pureData["author"] as MutableMap<String, Any>)

                    if (authorData["id"].toString().toLong() == command.author)
                        command.response.complete(update(command.id, command.item as T, command.lastAccessTimestamp))
                    else
                        command.response.complete(false)
                }
                is CollectionCommand.GetItem -> run {
                    command.response.complete(items[command.index])
                }
                is CollectionCommand.GetItemById -> run {
                    getItemById(command)
                }
                is CollectionCommand.PullCommits -> run {
                    pull(command.commits, command.lastAccessTimestamp)
                }
                is CollectionCommand.ShowItems -> run {
                    sort(CollectionSortType.DESC, lastAccessTimestamp)
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
                    loadDump(command.lastAccessTimestamp)
                }
                is CollectionCommand.CountLessThanByCommand -> run {
                    countLessThanBy(command)
                }
                is CollectionCommand.FilterLessThanByCommand -> run {
                    filterLessThanBy(command)
                }
                is CollectionCommand.AddIfMaxCommand -> run {
                    addIfMax(command)
                }
                is CollectionCommand.CountByCommand -> run {
                    countBy(command)
                }
                else -> run {
                    logger(LogLevel.ERROR, "Unknown command was caught in collection actor")
                }
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

    private fun getItemById(command: CollectionCommand.GetItemById) {
        val index = getIndexById(command.id)
        val res = this.items.getOrNull(index ?: -1)
        command.response.complete(res)
    }

    suspend fun getItemById(id: Int): T? {
        val command = CollectionCommand.GetItemById(id, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await() as? T
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

    private suspend fun loadDump(time: ZonedDateTime) {
        lastAccessTimestamp = time
        val loaded = dumpManager.loadDump()
        this.items = loaded
        sort(time = lastAccessTimestamp)
        lastInsertId = if (items.size > 0)
            items.last().id
        else
            0
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
    suspend fun removeAt(index: Int, author: Long): Long {
        val command = CollectionCommand.RemoveItem(index, author, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    /**
     * Remove by id
     *
     * @param id
     * @return
     */
    suspend fun removeById(id: Int, author: Long): Long {
        val command = CollectionCommand.RemoveItemById(id, author, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
        return command.response.await()
    }

    private fun checkIdExists(id: Int): Int {
        return this.getIndexById(id) ?: 0
    }

    private fun update(id: Int, item: T, time: ZonedDateTime): Boolean {
        lastAccessTimestamp = time
        val index = checkIdExists(id)
        if (index == 0)
            return false
        val old = getItem(index)
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
    suspend fun update(id: Int, item: T, author: Long): Boolean {
        val command = CollectionCommand.UpdateItem(id, item = item, author = author, lastAccessTimestamp = ZonedDateTime.now())
        collectionActor.send(command)
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

    protected fun stringifyItems(items: MutableList<T>): String {
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

    override fun toString(): String = stringifyItems(items)

    abstract fun countBy(command: CollectionCommand.CountByCommand): Int

    abstract fun countLessThanBy(command: CollectionCommand.CountLessThanByCommand): Int

    abstract fun filterLessThanBy(command: CollectionCommand.FilterLessThanByCommand): String

    abstract fun addIfMax(command: CollectionCommand.AddIfMaxCommand): Boolean

    abstract suspend fun <T:Any> countBy(comparable: T): Int

    abstract suspend fun countLessThanBy(comparable: Any): Int

    abstract suspend fun filterLessThanBy(comparable: Any): String

    abstract suspend fun addIfMax(item: Entity): Boolean

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