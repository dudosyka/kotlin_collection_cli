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
    private var modelInit: Boolean = false
    /**
     * Load dump
     *
     * @return
     */
    override suspend fun loadDump(): MutableList<T> {
        if (!modelInit) {
            modelInit = true
            dbManager.initModel(entityBuilder as EntityBuilder<Entity>)
        }
        return dbManager.getAll(entityBuilder as EntityBuilder<Entity>).apply {  } as MutableList<T>
    }

    override suspend fun dumpOnly(removedItems: MutableList<Int>, updatedItems: MutableList<T>): MutableList<T> {
        return dbManager.replace(entityBuilder as EntityBuilder<Entity>, removedItems, updatedItems as MutableList<Entity>) as MutableList<T>
    }

    /**
     * Dump
     *
     * @param items
     */
    override suspend fun dump(items: MutableList<T>): Boolean {
        logger(LogLevel.DEBUG, "Send on dump $items")
        return dbManager.replaceAll(items as MutableList<Entity>)
    }
}