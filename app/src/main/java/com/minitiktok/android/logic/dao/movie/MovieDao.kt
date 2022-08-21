package com.minitiktok.android.logic.dao.movie

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.minitiktok.android.utils.QueryBuilder
import com.minitiktok.android.utils.funs.logUtils
import com.minitiktok.android.logic.model.MovieEntity

@Dao
interface MovieDao {
    @Insert
    fun insertMovie(movie: MovieEntity): Long

    @Transaction
    fun insertMovies(movies: List<MovieEntity>) {
        for (movie in movies) {
            insertMovie(movie)
        }
    }

    @Delete
    fun deleteMovie(movie: MovieEntity)

    @Update
    fun updateMovie(movie: MovieEntity)

    @Query("select * from MovieEntity")
    fun getAllMovies(): List<MovieEntity>

    @Query("select * from MovieEntity where type = :type order by hot")
    fun getMoviesByType(type: Int): List<MovieEntity>

    @Query("select * from MovieEntity where type in (:types) order by hot")
    fun getMoviesByTypes(types: List<Int>): List<MovieEntity>

    @RawQuery
    fun getMoviesBySql(sql: SimpleSQLiteQuery): List<MovieEntity>

    @Query("select activityTime from MovieEntity where version is null limit 1")
    fun getActivityTimeThisWeek(): String

    @Transaction
    fun getMovieByTypeAndVersion(type: Int, version: Int? = null): List<MovieEntity> {
        val query = QueryBuilder().apply {
            table = "MovieEntity"
            mode = 1
            addSelectFields(listOf("*"))
            version?.let {
                and("version = $version")
            } ?: and("version is null")
            and("type = $type")
        }.build()
        logUtils.d("榜单", "开始从数据库获取榜单")
        return getMoviesBySql(query)
    }

    @Query("delete from MovieEntity where version is null")
    fun deleteNullMovies()

    @Query("delete from MovieEntity where version = :version")
    fun deleteMoviesByVersion(version: Int)

    @Query("delete from MovieEntity")
    fun clearAll()
}