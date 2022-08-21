package com.minitiktok.android.logic.dao.movie

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.minitiktok.android.logic.model.Version
import com.minitiktok.android.utils.QueryBuilder

@Dao
interface VersionDao {
    @Insert
    fun insertVersion(version: Version): Long

    @Transaction
    fun insertVersions(versions: List<Version>, now: Long, count: Long, cursor: Long) {
        for (version in versions) {
            version.createTime = now
            version.count = count
            version.cursor = cursor
            insertVersion(version);
        }
    }

    @Query("select * from Version order by version desc")
    fun getAllVersions(): List<Version>

    @Query("select max(version) from Version")
    fun getMaxVersion(): Int

    @RawQuery
    fun getVersionsByQuery(query: SimpleSQLiteQuery): List<Version>

    @Transaction
    fun getVersions(type: Int, version: Version? = null): List<Version> {
        val queryBuilder = QueryBuilder()
        if (version == null) {
            val query = queryBuilder.apply {
                mode = 1
                table = "Version"
                addSelectFields(listOf("*"))
                and("type = $type")
                order("version", "desc")
                limit(10)
            }.build()
            return getVersionsByQuery(query)
        } else {
            val query = queryBuilder.apply {
                mode = 1
                table = "Version"
                addSelectFields(listOf("*"))
                and("version < ${version.version}")
                and("type = ${version.type}")
                order("version", "desc")
                limit(10)
            }.build()
            return getVersionsByQuery(query)
        }
    }

    @Query("delete from Version where type = :type")
    fun clearVersionsByType(type: Int)

    @Transaction
    fun clearAndInsertVersions(
        type: Int,
        versions: List<Version>,
        now: Long,
        count: Long,
        cursor: Long
    ) {
        clearVersionsByType(type)
        insertVersions(versions, now, count, cursor)
    }

    @Query("delete from Version")
    fun clearAll()
}