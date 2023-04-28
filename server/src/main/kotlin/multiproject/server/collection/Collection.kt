package multiproject.server.collection

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.lib.exceptions.*
import multiproject.server.collection.item.Entity
import multiproject.server.collection.sort.CollectionSortType
import multiproject.server.collection.sort.IdComparator
import multiproject.server.dump.DumpManager
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
    @Transient private var lastAccessTimestamp: ZonedDateTime = initializationTimestamp

    constructor(items: MutableList<T>) {
        checkUniqueId(items)
    }

    private fun checkUniqueId(items: MutableList<T>) {
        val set: Set<Int> = items.map { it.id }.toSortedSet()
        if (set.size < items.size)
            throw NotUniqueIdException()
    }
    /**
     * Sort
     *
     * @param type
     */
    fun sort(type: CollectionSortType = CollectionSortType.DESC) {
        return if (type == CollectionSortType.DESC) {
            this.items.sortWith(IdComparator())
        } else {
            this.items.sortWith(IdComparator().reversed())
        }
    }

    /**
     * Sort by
     *
     * @param comparator
     */
    abstract fun sortBy(comparator: Comparator<T>)

    /**
     * Get all
     *
     * @return
     */
    fun getAll(): MutableList<T> {
        return this.items
    }

    /**
     * Get item
     *
     * @param index
     * @return
     */
    fun getItem(index: Int): T {
        this.lastAccessTimestamp = ZonedDateTime.now()
        return this.items.elementAt(index)
    }

    /**
     * Get index by id
     *
     * @param id
     * @return
     */
    fun getIndexById(id: Int): Int? {
        this.items.withIndex().forEach {
            if (it.value.id == id)
                return it.index
        }
        return null
    }

    /**
     * Get unique id
     *
     * @return
     */
    fun getUniqueId(): Int {
        lastInsertId += 1
        return lastInsertId
    }

    /**
     * Add item
     *
     * @param collectionItem
     */
    fun addItem(collectionItem: T) {
        this.lastAccessTimestamp = ZonedDateTime.now()
        this.items.add(collectionItem)
        this.sort()
    }

    /**
     * Load dump
     *
     */
    fun loadDump() {
        this.lastAccessTimestamp = ZonedDateTime.now()
        val items = dumpManager.loadDump()
        checkUniqueId(items)
        this.items = items
        this.sort()
        if (this.items.size > 0)
            this.lastInsertId = this.items.last().id
        else
            this.lastInsertId = 0
    }

    /**
     * Dump
     *
     */
    fun dump() {
        this.lastAccessTimestamp = ZonedDateTime.now()
        dumpManager.dump(this.items)
    }

    /**
     * Clear
     *
     */
    fun clear() {
        this.lastAccessTimestamp = ZonedDateTime.now()
        this.items = mutableListOf()
    }

    /**
     * Remove at
     *
     * @param index
     */
    fun removeAt(index: Int) {
        this.lastAccessTimestamp = ZonedDateTime.now()
        if (index < 0 || this.items.size < index)
            throw ItemNotFoundException("index", index)
        this.items.removeAt(index)
    }

    /**
     * Remove by id
     *
     * @param id
     * @return
     */
    fun removeById(id: Int): Boolean {
        val index = this.getIndexById(id) ?: throw ItemNotFoundException("id", id)
        this.removeAt(index)
        return true
    }

    /**
     * Check id exists
     *
     * @param id
     * @return
     */
    fun checkIdExists(id: Int): Int {
        return this.getIndexById(id) ?: throw ItemNotFoundException("id", id)
    }

    /**
     * Update
     *
     * @param id
     * @param item
     * @return
     */
    fun update(id: Int, item: T): Boolean {
        val index = this.checkIdExists(id)
        val old = this.getItem(index)
        item.id = old.id
        item.creationDate = old.creationDate
        this.items[index] = item
        return true
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
    fun getInfo(): CollectionInfo {
        return CollectionInfo(this.items.javaClass.simpleName.toString(), this.items.size, this.lastInsertId, this.lastAccessTimestamp, this.initializationTimestamp)
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
    abstract fun <T:Any> countBy(comparable: T): Int

    /**
     * Count less than by
     *
     * @param comparable
     * @return
     */
    abstract fun countLessThanBy(comparable: Any): Int

    /**
     * Filter less than by
     *
     * @param comparable
     * @return
     */
    abstract fun filterLessThanBy(comparable: Any): MutableList<T>

    /**
     * Add if max
     *
     * @param comparable
     * @param item
     * @return
     */
    abstract fun addIfMax(comparable: Any, item: T): Boolean
}