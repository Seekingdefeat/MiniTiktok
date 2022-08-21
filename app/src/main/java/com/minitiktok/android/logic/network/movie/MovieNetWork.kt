package com.minitiktok.android.logic.network.movie

import com.minitiktok.android.logic.network.ServiceCreator
import com.minitiktok.android.utils.funs.netAwait

object MovieNetWork {
    private val movieService = ServiceCreator.create<MovieService>()

    suspend fun refreshTvMovies(token: String, version: Int? = null) =
        movieService.getMovies(2, token, version).netAwait()

    suspend fun refreshMovies(type: Int, token: String, version: Int? = null) =
        movieService.getMovies(type, token, version).netAwait()

    suspend fun getTvVersions(token: String, count: Long, cursor: Long? = null) =
        movieService.getVersion(token, count, 2, cursor).netAwait()

    suspend fun getVersions(token: String, count: Long, type: Int, cursor: Long? = null) =
        movieService.getVersion(token, count, type, cursor).netAwait()
}