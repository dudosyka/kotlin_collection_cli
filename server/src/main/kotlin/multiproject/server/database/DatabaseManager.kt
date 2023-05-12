package multiproject.server.database

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.lib.utils.LogLevel
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import multiproject.lib.utils.Logger

class DatabaseManager {
    private lateinit var connection: Connection
    val logger: Logger by inject(Logger::class.java, named("logger"))
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
            log("$e", LogLevel.ERROR)
            0
        }
    }
    private fun stringifyPredicates(predicates: Map<String, DatabasePredicate>): String {
        val result = predicates.map { "${it.key}.${it.value.column} ${it.value.op} ${it.value.value}" }.joinToString(" ")
        return if (result.isNotEmpty()) "where $result" else result
    }
    fun <T: Entity> findOne(entityBuilder: EntityBuilder<T>, predicates: Map<String, DatabasePredicate>): T? {
        return this.findAll(entityBuilder, predicates).takeIf { it.size > 0 }?.first()
    }
    private fun buildFromRow(tableName: String, fields: Map<String, CommandArgumentDto>, row: ResultSet): MutableMap<String, Any?> {
        return fields.map {
            val columnIndex = "$tableName$${it.key}"
            if (it.value.nested != null && it.value.show)
                it.key to buildFromRow(it.value.nestedTable!!, it.value.nested!!, row)
            else
                it.key to when (it.value.type) {
                    FieldType.STRING -> row.getString(columnIndex)
                    FieldType.LONG -> row.getInt(columnIndex).toLong()
                    FieldType.FLOAT -> row.getFloat(columnIndex)
                    FieldType.INT -> row.getInt(columnIndex).toLong()
                    FieldType.BOOLEAN -> row.getInt(columnIndex) == 1
                    else -> row.getString(columnIndex)
                }
        }.toMap().toMutableMap()
    }
    fun <T: Entity> findAll(entity: EntityBuilder<T>, predicates: Map<String, DatabasePredicate> = mapOf()): MutableList<T> {
        val fieldsOnSelect = entity.fields.filter { it.value.nested == null }.map { "${entity.tableName}.${it.key} as ${entity.tableName}$${it.key}" }.toMutableList()
        val joinQueries = mutableListOf<String>()
        val nested = entity.fields.filter { it.value.nested != null }
        nested.forEach { model ->
            model.value.nested!!.map {
                fieldsOnSelect.add("${model.value.nestedTable}.${it.key} as ${model.value.nestedTable}$${it.key}")
            }
            joinQueries.add("left join ${model.value.nestedTable} on ${model.value.nestedTable}.${model.value.nestedJoinOn!!.second} = ${entity.tableName}.${model.value.nestedJoinOn!!.first}")
        }

        val data = mutableListOf<T>()
        try {
            val queryResult = execute("select ${fieldsOnSelect.joinToString(",") } from ${entity.tableName} ${joinQueries.joinToString(" ")} ${stringifyPredicates(predicates)}") {}

            while (queryResult.next()) {
                data.add(entity.build(buildFromRow(entity.tableName, entity.fields, queryResult)))
            }

        } catch (e: Exception) {
            log("$e", LogLevel.ERROR)
        }

        return data
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
    fun <T: Entity> initModel(model: EntityBuilder<T>) {
        val query = this.createTableQuery(model.tableName, model.fields)
        try {
            query.forEach {
                executeUpdate(it) {}
            }
        } catch (e: SQLException) {
            log("SQL. Execution error: ${e.message}", LogLevel.ERROR)
        }
    }
    private data class InsertTemplate(
        val schema: Map<String, CommandArgumentDto>,
        val statement: PreparedStatement,
        var index: Int = 1,
        val level: Int,
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
            level = level
        )
    }
    private fun entityToRow(schema: Map<String, CommandArgumentDto>, row: MutableMap<String, Any?>, tableName: String, inserts: MutableMap<String, InsertTemplate>) {
        val insert = inserts[tableName]
        val statement = insert!!.statement
        println("${schema.size}, $statement, $row")
        schema.forEach {
            val item = row[it.key]
            if (it.value.nested != null) {
                val nestedValues = row[it.key]!! as MutableMap<String, Any?>
                statement.setLong(insert.index, nestedValues[it.value.nestedJoinOn!!.second].toString().toLong())
                entityToRow(
                    it.value.nested!!,
                    nestedValues,
                    it.value.nestedTable!!,
                    inserts
                )
            } else
                when (it.value.type) {
                    FieldType.STRING -> statement.setString(insert.index, item.toString())
                    FieldType.LONG -> statement.setLong(insert.index, item.toString().toLong())
                    FieldType.FLOAT -> statement.setFloat(insert.index, item.toString().toFloat())
                    FieldType.INT -> statement.setLong(insert.index, item.toString().toLong())
                    FieldType.BOOLEAN -> statement.setBoolean(insert.index, item == 1)
                    else -> statement.setString(insert.index, item.toString())
                }
            insert.index++
        }
        statement.addBatch()
    }
    fun <T: Entity> replaceAll(items: MutableList<T>) {
        if (items.size == 0)
            return
        val item = items.first()
        val query = mutableListOf<String>()
        query.add("truncate table ${item.tableName} cascade")
        item.fieldsSchema.forEach {
            if (it.value.nested != null)
                query.add("truncate table ${it.value.nestedTable} cascade")
        }
        try {
            query.forEach {
                executeUpdate(it) {}
            }
            this.insert(items)
        } catch (e: Exception) {
            log("$e", LogLevel.FATAL)
        }
    }
    fun <T: Entity> insert(items: MutableList<T>) {
        if (items.size == 0)
            return
        val item = items.first()
        val inserts = mutableMapOf<String, InsertTemplate>()
        inserts[item.tableName] = this.generateInsertTemplate(item.tableName, item.fieldsSchema, items.size, 1)
        item.fieldsSchema.forEach {
            if (it.value.nested != null)
                inserts[it.value.nestedTable!!] = this.generateInsertTemplate(it.value.nestedTable!!, it.value.nested!!, items.size, 2)
        }
        items.forEach {
            entityToRow(it.fieldsSchema, it.pureData.toMutableMap(), it.tableName, inserts)
        }

        try {
            inserts.map {
                it.value
            }.toMutableList().apply { this.sortByDescending { it.level } }.forEach {
                it.statement.executeUpdate()
            }
        } catch (e: Exception) {
            log("$e", LogLevel.ERROR)
        }
    }
}