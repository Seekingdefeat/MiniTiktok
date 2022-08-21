package com.minitiktok.android.logic.dao.clientToken

import androidx.room.*
import com.minitiktok.android.logic.model.ClientToken

@Dao
interface ClientTokenDao {
    @Insert
    fun insertToken(token: ClientToken): Long

    @Update
    fun updateToken(token: ClientToken)

    @Delete
    fun deleteToken(token: ClientToken)

    @Query("select * from ClientToken where client_key = :client_key order by create_time desc")
    fun getTokenByKey(client_key: String): List<ClientToken>

    @Query("delete from ClientToken where client_key = :client_key")
    fun deleteByKey(client_key: String)

    @Query("delete from ClientToken")
    fun clearTokens()

}