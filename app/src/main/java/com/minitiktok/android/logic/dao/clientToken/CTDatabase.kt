package com.minitiktok.android.logic.dao.clientToken

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minitiktok.android.logic.model.ClientToken

@Database(version = 1, entities = [ClientToken::class])
abstract class CTDatabase : RoomDatabase() {
    abstract fun ClientTokenDao(): ClientTokenDao

    companion object {
        private var instance: CTDatabase? = null

        @Synchronized
        fun getInstance(context: Context): CTDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                CTDatabase::class.java,
                "client_token_db"
            ).build().apply {
                instance = this
            }
        }
    }
}