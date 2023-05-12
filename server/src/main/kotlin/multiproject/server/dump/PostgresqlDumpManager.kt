package multiproject.server.dump

import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.database.DatabaseManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class PostgresqlDumpManager<T: Entity>(private val entityBuilder: EntityBuilder<T>): DumpManager<T>() {
    private val dbManager: DatabaseManager by inject(DatabaseManager::class.java, named("dbManager"))
    private val logger: Logger by inject(Logger::class.java, named("logger"))
    init {
        dbManager.initModel(entityBuilder)
    }
    /**
     * Load dump
     *
     * @return
     */
    override fun loadDump(): MutableList<T> {
        return dbManager.findAll(entityBuilder).apply { logger(LogLevel.DEBUG, "Dump loaded: $this") }
    }

    /**
     * Dump
     *
     * @param items
     */
    override fun dump(items: MutableList<T>) {
        logger(LogLevel.DEBUG, "Send on dump $items")
        dbManager.replaceAll(items)
    }
}