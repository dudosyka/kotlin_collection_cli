package multiproject.server.database

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.sql.*

class DatabaseManager {
    private lateinit var connection: Connection
    val logger: Logger by inject(Logger::class.java, named("logger"))
    val scope = CoroutineScope(Job())
    private sealed class DatabaseCommand() {
        class InitModel(val entityBuilder: EntityBuilder<Entity>): DatabaseCommand()
        class GetNewId(val tableName: String, val response: CompletableDeferred<Int> = CompletableDeferred()): DatabaseCommand()
        class FindOne(val entityBuilder: EntityBuilder<Entity>, val predicates: Map<String, DatabasePredicate> = mapOf(), val response: CompletableDeferred<Entity?> = CompletableDeferred()): DatabaseCommand()
        class ReplaceAll(val items: MutableList<Entity>): DatabaseCommand()
        class FindAll(val entityBuilder: EntityBuilder<Entity>, val predicates: Map<String, DatabasePredicate> = mapOf(), val response: CompletableDeferred<MutableList<Entity>> = CompletableDeferred()): DatabaseCommand()
        class Insert(val items: MutableList<Entity>): DatabaseCommand()
    }
    @OptIn(ObsoleteCoroutinesApi::class)
    private val actor = scope.actor<DatabaseCommand> {
        for (command in this) {
            when (command) {
                is DatabaseCommand.InitModel -> run {
                    val query = createTableQuery(command.entityBuilder.tableName, command.entityBuilder.fields)
                    try {
                        query.forEach {
                            executeUpdate(it) {}
                        }
                    } catch (e: SQLException) {
                        log("SQL. Execution error: ${e.message} ${e.stackTraceToString()}", LogLevel.ERROR)
                    }
                }
                is DatabaseCommand.GetNewId -> run {
                    command.response.complete(nextId(command.tableName))
                }
                is DatabaseCommand.FindOne -> run {
                    command.response.complete(findAll(command.entityBuilder, command.predicates).takeIf { it.size > 0 }?.first())
                }
                is DatabaseCommand.ReplaceAll -> run {
                    if (command.items.size == 0)
                        return@run
                    val item = command.items.first()
                    val query = mutableListOf<String>()
                    query.add("truncate table ${item.tableName} cascade")
                    item.fieldsSchema.forEach {
                        if (it.value.nested != null && it.value.show)
                            query.add("truncate table ${it.value.nestedTable} cascade")
                    }
                    try {
                        query.forEach {
                            executeUpdate(it) {}
                        }
                        insertRows(command.items)
                    } catch (e: Exception) {
                        log("$e ${e.stackTraceToString()}", LogLevel.FATAL)
                    }
                }
                is DatabaseCommand.FindAll -> run {
                    command.response.complete(findAll(command.entityBuilder, command.predicates))
                }
                is DatabaseCommand.Insert -> run {
                    insertRows(command.items)
                }
            }
        }
    }
    init {
        val connectionUrl = "jdbc:postgresql://localhost:5432/postgres"
        logger(LogLevel.INFO, "SQL. Start connection on $connectionUrl")
        try {
            connection = DriverManager.getConnection(connectionUrl)
        } catch (e: SQLException) {
            log( "Error during connection: ${e.message}")
        }
        log("Connection init.")
    }
    private fun log(message: String, logLevel: LogLevel = LogLevel.INFO) {
        logger(logLevel, "SQL. $message")
    }
    private fun execute(sql: String, data: PreparedStatement.() -> Unit): ResultSet {
        val statement = connection.prepareStatement(sql).apply(data)
        log("executed: $sql")
        return statement.executeQuery()
    }
    private fun executeUpdate(sql: String, data: PreparedStatement.() -> Unit): Int {
        val statement = connection.prepareStatement(sql).apply(data)
        log("executed: $sql")
        return try {
            statement.executeUpdate()
        } catch (e: Exception) {
            log("$e ${e.stackTraceToString()}", LogLevel.ERROR)
            0
        }
    }
    private fun stringifyPredicates(predicates: Map<String, DatabasePredicate>): String {
        val result = predicates.map { "${it.key}.${it.value.column} ${it.value.op} ${it.value.value}" }.joinToString(" ")
        return if (result.isNotEmpty()) "where $result" else result
    }
    private fun buildFromRow(tableName: String, fields: Map<String, CommandArgumentDto>, row: ResultSet): MutableMap<String, Any?> {
        return fields.map {
            val columnIndex = "$tableName$${it.key}"
            if (it.value.nested != null)
                it.key to buildFromRow(it.value.nestedTable!!, it.value.nested!!, row)
            else {
                it.key to when (it.value.type) {
                    FieldType.STRING -> row.getString(columnIndex)
                    FieldType.LONG -> row.getInt(columnIndex).toLong()
                    FieldType.FLOAT -> row.getFloat(columnIndex)
                    FieldType.INT -> row.getInt(columnIndex).toLong()
                    FieldType.BOOLEAN -> row.getInt(columnIndex) == 1
                    else -> row.getString(columnIndex)
                }
            }
        }.toMap().toMutableMap()
    }
    private fun createTableQuery(tableName: String, fields: Map<String, CommandArgumentDto>): MutableList<String> {
        val queryQueue = mutableListOf<String>()
        var query = "create table if not exists $tableName ("
        val fieldCreationStatements = fields.map {
            var statement = "${it.key} ${FieldTypeInterpreter.interpret(it.value.type)} ${if (it.value.required) "NOT NULL" else "DEFAULT NULL"}"
            if (it.value.nested != null) {
                statement = "${it.key} INT references ${it.value.nestedTable!!}(${it.value.nestedJoinOn!!.second})"
                queryQueue.addAll(createTableQuery(it.value.nestedTable!!, it.value.nested!!))
            }
            if (it.key == "id")
                statement = "id serial primary key"
            statement
        }

        query += fieldCreationStatements.joinToString(",") + ");"
        queryQueue.add(query)
        return queryQueue
    }
    private data class InsertTemplate(
        val schema: Map<String, CommandArgumentDto>,
        val statement: PreparedStatement,
        var index: Int = 1,
        val level: Int,
        var currId: Int,
        val data: MutableList<Map<String, Any?>> = mutableListOf()
    )
    private fun generateInsertTemplate(tableName: String, schema: Map<String, CommandArgumentDto>, itemsCount: Int, level: Int): InsertTemplate {
        val schemaFields = schema.map {
            if (it.value.nested != null) it.value.nestedJoinOn!!.first else it.key
        }
        val row = schemaFields.joinToString(",") { "?" }
        var values = ""
        for (i in 1 until itemsCount) {
            values += "($row),"
        }
        values += "($row)"
        val statement = connection.prepareStatement("insert into $tableName (${schemaFields.joinToString(",")}) values $values")
        return InsertTemplate(
            schema,
            statement,
            level = level,
            currId = if (level > 1) nextId(tableName) else (nextId(tableName) - itemsCount - 1)
        )
    }
    private fun entityToRow(schema: Map<String, CommandArgumentDto>, row: MutableMap<String, Any?>, tableName: String, inserts: MutableMap<String, InsertTemplate>) {
        val insert = inserts[tableName]
        val statement = insert!!.statement
        schema.forEach {
            val item = row[it.key]
            if (it.value.nested != null && it.value.show) {
                val nestedValues = row[it.key]!! as MutableMap<String, Any?>
                val nestedInsert = inserts[it.value.nestedTable!!]!!
                nestedInsert.currId++
                nestedValues[it.value.nestedJoinOn!!.second] = nestedInsert.currId
                statement.setInt(insert.index, nestedInsert.currId)
                entityToRow(
                    it.value.nested!!,
                    nestedValues,
                    it.value.nestedTable!!,
                    inserts
                )
            } else if (!it.value.autoIncrement) {
                when (it.value.type) {
                    FieldType.STRING -> statement.setString(insert.index, item.toString())
                    FieldType.LONG -> statement.setLong(insert.index, item.toString().toLong())
                    FieldType.FLOAT -> statement.setFloat(insert.index, item.toString().toFloat())
                    FieldType.INT -> statement.setLong(insert.index, item.toString().toLong())
                    FieldType.BOOLEAN -> statement.setBoolean(insert.index, item == 1)
                    else -> statement.setString(insert.index, item.toString())
                }
            } else if (it.value.autoIncrement && it.value.nested != null) {
                val nestedValues = row[it.key]!! as MutableMap<String, Any?>
                statement.setInt(insert.index, nestedValues[it.value.nestedJoinOn!!.second].toString().toInt())
            } else if (it.value.autoIncrement) {
                statement.setInt(insert.index, insert.currId)
            }
            insert.index++
        }
        statement.addBatch()
    }
    private fun insertRows(items: MutableList<Entity>) {
        if (items.size == 0)
            return
        val item = items.first()
        val inserts = mutableMapOf<String, InsertTemplate>()
        inserts[item.tableName] = this.generateInsertTemplate(item.tableName, item.fieldsSchema, items.size, 1)
        item.fieldsSchema.forEach {
            if (it.value.nested != null && it.value.show)
                inserts[it.value.nestedTable!!] = this.generateInsertTemplate(it.value.nestedTable!!, it.value.nested!!, items.size, 2)
        }
        items.forEach {
            inserts[it.tableName]!!.currId++
            val data = it.pureData.toMutableMap()
            data.apply {
                this["id"] = inserts[it.tableName]!!.currId
            }
            entityToRow(it.fieldsSchema, data, it.tableName, inserts)
        }

        try {
            inserts.map {
                it.value
            }.toMutableList().apply { this.sortByDescending { it.level } }.forEach {
                log("executed: ${it.statement}")
                it.statement.executeUpdate()
            }
        } catch (e: Exception) {
            log("$e ${e.stackTraceToString()}", LogLevel.ERROR)
        }
    }
    private fun findAll(entityBuilder: EntityBuilder<Entity>, predicates: Map<String, DatabasePredicate> = mapOf()): MutableList<Entity> {
        val fieldsOnSelect = entityBuilder.fields.filter { it.value.nested == null }.map { "${entityBuilder.tableName}.${it.key} as ${entityBuilder.tableName}$${it.key}" }.toMutableList()
        val joinQueries = mutableListOf<String>()
        val nested = entityBuilder.fields.filter { it.value.nested != null }
        nested.forEach { model ->
            model.value.nested!!.map {
                fieldsOnSelect.add("${model.value.nestedTable}.${it.key} as ${model.value.nestedTable}$${it.key}")
            }
            joinQueries.add("left join ${model.value.nestedTable} on ${model.value.nestedTable}.${model.value.nestedJoinOn!!.second} = ${entityBuilder.tableName}.${model.value.nestedJoinOn!!.first}")
        }

        val data = mutableListOf<Entity>()
        try {
            val queryResult = execute("select ${fieldsOnSelect.joinToString(",") } from ${entityBuilder.tableName} ${joinQueries.joinToString(" ")} ${stringifyPredicates(predicates)}") {}

            while (queryResult.next()) {
                data.add(entityBuilder.build(buildFromRow(entityBuilder.tableName, entityBuilder.fields, queryResult)))
            }

        } catch (e: Exception) {
            log("$e ${e.stackTraceToString()}", LogLevel.ERROR)
        }

        return data
    }
    private fun nextId(tableName: String): Int {
        val query = "select nextval('${tableName}_id_seq')"
        val result = execute(query) {}
        result.next()
        return result.getInt(1)
    }


    fun initModel(entityBuilder: EntityBuilder<Entity>) {
        val command = DatabaseCommand.InitModel(entityBuilder)
        actor.trySend(command)
    }
    suspend fun getNewId(tableName: String): Int {
        val command = DatabaseCommand.GetNewId(tableName)
        actor.send(command)
        return command.response.await()
    }
    suspend fun <T: Entity> findOne(entityBuilder: EntityBuilder<T>, predicates: Map<String, DatabasePredicate>): Entity? {
        val command = DatabaseCommand.FindOne(entityBuilder as EntityBuilder<Entity>, predicates)
        actor.send(command)
        return command.response.await()
    }
    fun replaceAll(items: MutableList<Entity>) {
        val command = DatabaseCommand.ReplaceAll(items)
        actor.trySend(command)
    }
    suspend fun getAll(entityBuilder: EntityBuilder<Entity>, predicates: Map<String, DatabasePredicate> = mapOf()): MutableList<Entity> {
        val command = DatabaseCommand.FindAll(entityBuilder, predicates)
        actor.send(command)
        return command.response.await()
    }
    fun insert(items: MutableList<Entity>) {
        val command = DatabaseCommand.Insert(items)
        actor.trySend(command)
    }
}