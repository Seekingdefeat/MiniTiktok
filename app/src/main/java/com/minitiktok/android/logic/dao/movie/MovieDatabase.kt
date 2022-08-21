package com.minitiktok.android.logic.dao.movie

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.minitiktok.android.logic.model.MovieEntity
import com.minitiktok.android.logic.model.Version

@Database(version = 3, entities = [MovieEntity::class, Version::class])
abstract class MovieDatabase : RoomDatabase() {
    abstract fun MovieDao(): MovieDao
    abstract fun VersionDao(): VersionDao

    companion object {
        private var instance: MovieDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table MovieEntity add column activityTime TEXT default 'null' ")
            }

        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "create table Version(" +
                            "id integer primary key autoincrement not null," +
                            "active_time text not null ," +
                            "end_time text not null," +
                            "start_time text not null," +
                            "type integer not null," +
                            "version integer not null," +
                            "createTime integer not null," +
                            "cursor integer not null," +
                            "count integer not null" +
                            ")"
                )
            }

        }

        @Synchronized
        fun getInstance(context: Context): MovieDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java,
                "movie_db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().apply {
                    instance = this
                }
        }
    }
}