package multiproject.server.database

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class DatabaseManager {
    private lateinit var connection: Connection

    init {
        val connectionUrl = "jdbc:postgresql://localhost:5432/postgres"
        println("SQL. Start connection on $connectionUrl")
        try {
            connection = DriverManager.getConnection(connectionUrl)
        } catch (e: SQLException) {
            println("SQL. Error during connection: ${e.message}")
        }
        println("SQL. Connection init.")
    }

    private fun execute(sql: String, data: PreparedStatement.() -> Unit): ResultSet {
        val statement = connection.prepareStatement(sql).apply(data)
        println("SQL. executed: $sql")
        return statement.executeQuery()
    }

    private fun executeUpdate(sql: String, data: PreparedStatement.() -> Unit): Int {
        val statement = connection.prepareStatement(sql).apply(data)
        println("SQL. executed: $sql")
        return statement.executeUpdate()
    }
    private fun stringifyPredicates(predicates: Map<String, DatabasePredicate>): String {
        val result = predicates.map { "${it.key} ${it.value.column} ${it.value.op} ${it.value.value}" }.joinToString(" ")
        return if (result.isNotEmpty()) "where $result" else result
    }
    fun findOne(tableName: String, fieldsOnSelect: List<String>, predicates: Map<String, DatabasePredicate>): String = "select ${fieldsOnSelect.joinToString(",") { "$tableName.$it as $tableName$$it" }} } from $tableName ${stringifyPredicates(predicates)}"

    private fun buildFromRow(tableName: String, fields: Map<String, CommandArgumentDto>, row: ResultSet): MutableMap<String, Any?> {
        return fields.map {
            val columnIndex = "$tableName$${it.key}"
            if (it.value.nested != null)
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

        val queryResult = execute("select ${fieldsOnSelect.joinToString(",") } from ${entity.tableName} ${joinQueries.joinToString(" ")} ${stringifyPredicates(predicates)}") {}

        val data = mutableListOf<T>()
        while (queryResult.next()) {
            data.add(entity.build(buildFromRow(entity.tableName, entity.fields, queryResult)))
        }

        return data
    }

    private fun generateCreateQuery(tableName: String, fields: Map<String, CommandArgumentDto>): MutableList<String> {
        val queryQueue = mutableListOf<String>()
        var query = "create table if not exists $tableName ("
        val fieldCreationStatements = fields.map {
            var statement = "${it.key} ${FieldTypeInterpreter.interpret(it.value.type)} ${if (it.value.required) "NOT NULL" else "DEFAULT NULL"}"
            if (it.value.nested != null) {
                statement = "${it.key} INT references ${it.value.nestedTable!!}(${it.value.nestedJoinOn!!.second})"
                queryQueue.addAll(generateCreateQuery(it.value.nestedTable!!, it.value.nested!!))
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
        val query = this.generateCreateQuery(model.tableName, model.fields)
        try {
            query.forEach {
                executeUpdate(it) {}
            }
        } catch (e: SQLException) {
            println("SQL. Execution error: ${e.message}")
        }
    }
}