package com.minitiktok.android.logic.network.movie

import com.minitiktok.android.logic.model.MovieResp
import com.minitiktok.android.logic.model.VersionResp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface MovieService {

    //获取版本信息
    @Headers("Content-Type: application/json")
    @GET("discovery/ent/rank/version/")
    fun getVersion(
        @Header("access-token") token: String,
        @Query("count") count: Long,
        @Query("type") type: Int,
        @Query("cursor") cursor: Long?,
    ): Call<VersionResp>

    //获取榜单数据
    @Headers("Content-Type: application/json")
    @GET("discovery/ent/rank/item/")
    fun getMovies(
        @Query("type") type: Int,
        @Header("access-token") token: String,
        @Query("version") version: Int?,
    ): Call<MovieResp>


}