package com.minitiktok.android.utils

import androidx.sqlite.db.SimpleSQLiteQuery

class QueryBuilder {
    var table: String? = null
    var mode = 1
    private val selectedFields = mutableListOf<String>()
    private val andCondition = mutableListOf<String>()
    private var orderCondition: String? = null
    private var groupCondition: String? = null
    private var limit: Long? = null

    fun setTable(table: String): QueryBuilder = this.apply {
        this.table = table
    }

    fun setMode(mode: Int) = this.apply {
        this.mode = mode
    }

    fun addSelectFields(fileds: List<String>) = this.apply {
        selectedFields.addAll(fileds)
    }

    fun and(condition: String) = this.apply {
        andCondition.add(condition)
    }

    fun order(field: String, sort: String = "asc") = this.apply {
        orderCondition = "order by $field $sort"
    }

    fun group(field: String) = this.apply {
        orderCondition = "group by $field "
    }

    fun limit(limit: Long) = this.apply {
        this.limit = limit
    }

    fun getSql() = when (mode) {
        Companion.SELECT_MODE -> {
            StringBuilder().apply {
                append("select ")
                for (filed in selectedFields) {
                    append("$filed,")
                }
                deleteAt(this.length - 1)
                append(" from $table")
                append(" where ")
                for (and in andCondition) {
                    append(" $and  and")
                }
                delete(this.length - 3, this.length)
                groupCondition?.let { append(" $groupCondition ") }
                orderCondition?.let { append(" $orderCondition ") }
                limit?.let { append(" limit $limit") }
            }.toString()
        }
        else -> {
            ""
        }
    }


    fun build(): SimpleSQLiteQuery {
        return SimpleSQLiteQuery(getSql())
    }

    companion object {
        const val SELECT_MODE = 1;
        const val DELETE_MODE = 2;
        const val INSERT_MODE = 3;
        const val UPDATE_MODE = 4;
    }

}