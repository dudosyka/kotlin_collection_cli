package multiproject.server.dump

import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.database.DatabaseManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class PostgresqlDumpManager<T: Entity>(private val entityBuilder: EntityBuilder<T>): DumpManager<T>() {
    private val dbManager: DatabaseManager by KoinJavaComponent.inject(DatabaseManager::class.java, named("dbManager"))
    init {
        dbManager.initModel(entityBuilder)
    }
    /**
     * Load dump
     *
     * @return
     */
    override fun loadDump(): MutableList<T> {
        return dbManager.findAll(entityBuilder)
    }

    /**
     * Dump
     *
     * @param items
     */
    override fun dump(items: MutableList<T>) {
        println("dumped!")
    }
}