package lab5kotlin.collection

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import lab5kotlin.exceptions.ItemNotFoundException
import lab5kotlin.exceptions.NotUniqueIdException
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.sort.CollectionSortType
import lab5kotlin.collection.sort.IdComparator
import lab5kotlin.dump.DumpManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

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
        val set: Set<Int> = items.map { it.id }.toSortedSet()
        if (set.size < items.size)
            throw NotUniqueIdException()
    }

    fun sort(type: CollectionSortType = CollectionSortType.DESC) {
        return if (type == CollectionSortType.DESC) {
            this.items.sortWith(IdComparator())
        } else {
            this.items.sortWith(IdComparator().reversed())
        }
    }

    abstract fun sortBy(comparator: Comparator<T>)

    fun getAll(): MutableList<T> {
        return this.items
    }

    fun getItem(index: Int): T {
        this.lastAccessTimestamp = ZonedDateTime.now()
        return this.items.elementAt(index)
    }

    fun getIndexById(id: Int): Int? {
        this.items.withIndex().forEach {
            if (it.value.id == id)
                return it.index
        }
        return null
    }

    fun getUniqueId(): Int {
        lastInsertId += 1
        return lastInsertId
    }

    fun addItem(collectionItem: T) {
        this.lastAccessTimestamp = ZonedDateTime.now()
        this.items.add(collectionItem)
        this.sort()
    }

    fun loadDump() {
        this.lastAccessTimestamp = ZonedDateTime.now()
        this.items = dumpManager.loadDump()
        this.sort()
        if (this.items.size > 0)
            this.lastInsertId = this.items.last().id
        else
            this.lastInsertId = 0
    }

    fun dump() {
        this.lastAccessTimestamp = ZonedDateTime.now()
        dumpManager.dump(this.items)
    }

    fun clear() {
        this.lastAccessTimestamp = ZonedDateTime.now()
        this.items = mutableListOf()
    }

    fun removeAt(index: Int) {
        this.lastAccessTimestamp = ZonedDateTime.now()
        if (index < 0 || this.items.size < index)
            throw ItemNotFoundException("index", index)
        this.items.removeAt(index)
    }

    fun removeById(id: Int): Boolean {
        val index = this.getIndexById(id) ?: throw ItemNotFoundException("id", id)
        this.removeAt(index)
        return true
    }

    fun checkIdExists(id: Int): Int {
        return this.getIndexById(id) ?: throw ItemNotFoundException("id", id)
    }

    fun update(id: Int, item: T): Boolean {
        val index = this.checkIdExists(id)
        val old = this.getItem(index)
        item.id = old.id
        item.creationDate = old.creationDate
        this.items[index] = item
        return true
    }

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


    fun getInfo(): CollectionInfo {
        return CollectionInfo(this.items.javaClass.simpleName.toString(), this.items.size, this.lastInsertId, this.lastAccessTimestamp, this.initializationTimestamp)
    }

    abstract fun <T:Any> countBy(comparable: T): Int

    abstract fun countLessThanBy(comparable: Any): Int

    abstract fun filterLessThanBy(comparable: Any): MutableList<T>

    abstract fun addIfMax(comparable: Any, item: T): Boolean
}