package com.minitiktok.android.logic

import androidx.lifecycle.liveData
import androidx.room.Transaction
import com.minitiktok.android.TikTokApplication
import com.minitiktok.android.logic.dao.clientToken.CTDatabase
import com.minitiktok.android.logic.dao.movie.MovieDatabase
import com.minitiktok.android.logic.model.*
import com.minitiktok.android.logic.network.movie.MovieNetWork
import com.minitiktok.android.logic.network.token.AccessTokenNetwork
import com.minitiktok.android.utils.NetUtils
import com.minitiktok.android.utils.QueryBuilder
import com.minitiktok.android.utils.funs.logUtils
import com.minitiktok.android.utils.funs.sendToast
import com.minitiktok.android.utils.funs.throwRunEx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


object Repository {
    private val clientTokenDao = CTDatabase.getInstance(TikTokApplication.context).ClientTokenDao()

    private val versionDao = MovieDatabase.getInstance(TikTokApplication.context).VersionDao()

    private val movieDao = MovieDatabase.getInstance(TikTokApplication.context).MovieDao()


    fun getVersions(type: Int, version: Version? = null) = liveData(Dispatchers.IO) {
        val result = try {
            val versions = versionDao.getVersions(type, version)
            logUtils.d("版本", "从数据库成功获得版本信息")
            if (versions.isNotEmpty()) {
                Result.success(versions)
            } else {
                //判断是否有网络
                if (NetUtils.getNetWorkStart(TikTokApplication.context) == NetUtils.NETWORK_NONE) {
                    logUtils.d("后台", "网络未连接")
                    withContext(Dispatchers.Main) {
                        "网络出现故障了".sendToast()
                    }
                    Result.failure<List<Version>>("获取失败".throwRunEx())
                } else {
                    logUtils.d("版本", "从数据库获取失败")
                    logUtils.d("版本", "开始网络获取数据")
                    //获取client_token
                    val token = getClientToken(
                        TikTokApplication.CLIENT_KEY,
                        TikTokApplication.CLIENT_SECRET
                    ).getOrNull()?.access_token
                    //从网络获取版本
                    val resp =
                        token?.let { MovieNetWork.getVersions(it, 10, type, version?.cursor) }
                    if (resp != null && resp.respDate.error_code == 0L) {
                        val versionsByNet = resp.respDate.versions
                        //插入数据库
                        if (versionsByNet != null && versionsByNet.isNotEmpty() && resp.respDate.cursor != null) {
                            versionDao.insertVersions(
                                versionsByNet,
                                Date().time,
                                10,
                                resp.respDate.cursor
                            )
                            logUtils.d("版本", "从网络获取成功")
                            Result.success(versionsByNet)
                        } else {
                            logUtils.d("版本", "从网络获取失败")
                            Result.failure("从网络获取失败".throwRunEx())
                        }
                    } else {
                        logUtils.d("版本", "从网络获取失败")
                        Result.failure("从网络获取失败".throwRunEx())
                    }
                }
            }
        } catch (e: Exception) {
            logUtils.d("版本", "获取失败")
            Result.failure("获取失败".throwRunEx())
        }
        emit(result)
    }

    fun getMovies(type: Int, version: Int? = null) =
        liveData(Dispatchers.IO) {
            val result = try {
                val query = QueryBuilder().apply {
                    table = "MovieEntity"
                    mode = 1
                    addSelectFields(listOf("*"))
                    version?.let {
                        and("version = $version")
                    } ?: and("version is null")
                    and("type = $type")
                    order("hot", "desc")
                }.build()

                logUtils.d("榜单", "开始从数据库获取榜单")
                val movies = movieDao.getMoviesBySql(query)
                if (movies.isNotEmpty()) {
                    logUtils.d("榜单", "从数据库成功获取到榜单")
                    Result.success(movies)
                } else {
                    //判断是否有网络
                    if (NetUtils.getNetWorkStart(TikTokApplication.context) == NetUtils.NETWORK_NONE) {
                        logUtils.d("后台", "网络未连接")
                        withContext(Dispatchers.Main) {
                            "网络出现故障了".sendToast()
                        }
                        Result.failure<List<MovieEntity>>("获取失败".throwRunEx())
                    } else {
                        logUtils.d("榜单", "开始从网络获取榜单")
                        //获取client_token
                        val token = getClientToken(
                            TikTokApplication.CLIENT_KEY,
                            TikTokApplication.CLIENT_SECRET
                        ).getOrNull()?.access_token
                        //发起网络请求
                        val movieResp = token?.let { MovieNetWork.refreshMovies(type, it, version) }
                        val moviesByNet = movieResp!!.respDate.movies
                        //插入数据库
                        if (moviesByNet != null) {
                            val movieEntityList = mutableListOf<MovieEntity>()
                            for (movie in moviesByNet) {
                                movieEntityList.add(
                                    movie.toEntity(
                                        movieResp.respDate.activeTime,
                                        version
                                    )
                                )
                            }
                            //获取成功后插入数据库
                            movieDao.insertMovies(movieEntityList)
                            logUtils.d("榜单", "从网络成功获取到榜单")
                            Result.success(movieEntityList)
                        } else {
                            logUtils.d("榜单", "无法从网络成功获取到榜单")
                            Result.failure("获取数据库失败".throwRunEx())
                        }
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    @Transaction
    suspend fun getClientToken(clientKey: String, clientSecret: String) =
        withContext(Dispatchers.IO) {
            //尝试从数据库获
            logUtils.d("授权", "从数据库获取token")
            val clientTokenByKey =
                clientTokenDao.getTokenByKey(clientKey)
            if (clientTokenByKey.isNotEmpty() && !clientTokenByKey[0].isExpire()) {
                //数据库获取成功回调
                logUtils.d(
                    "授权",
                    "token的获取时间:${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(clientTokenByKey[0].create_time)}"
                )
                Result.success(clientTokenByKey[0])
            } else {
                //判断是否有网络
                if (NetUtils.getNetWorkStart(TikTokApplication.context) == NetUtils.NETWORK_NONE) {
                    logUtils.d("后台", "网络未连接")
                    withContext(Dispatchers.Main) {
                        "网络出现故障了".sendToast()
                    }
                    Result.failure<ClientToken>("获取失败".throwRunEx())
                } else {
                    //清空token
                    clientTokenDao.clearTokens()
                    //尝试从网络获取
                    logUtils.d("授权", "从网络获取token")
                    val clientToken = AccessTokenNetwork.getClientToken(clientKey, clientSecret)
                    if (clientToken.respData.error_code == 0) {
                        //获取成功
                        val token = clientToken.respData.toEntity(clientKey)
                        //插入数据库
                        clientTokenDao.insertToken(token)
                        logUtils.d(
                            "授权",
                            "token的获取时间:${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(token.create_time)}"
                        )

                        //网络获取成功回调
                        Result.success(token)
                    } else {
                        //获取失败回调
                        Result.failure("获取失败".throwRunEx())
                    }
                }
            }
        }

    @Transaction
    suspend fun refreshMovieCache() =
        withContext(Dispatchers.IO) {
            //判断是否有网络
            if (NetUtils.getNetWorkStart(TikTokApplication.context) == NetUtils.NETWORK_NONE) {
                logUtils.d("后台", "网络未连接")
                withContext(Dispatchers.Main) {
                    "网络出现故障了".sendToast()
                }
                Result.failure<Boolean>("获取失败".throwRunEx())
            } else {
                //获取授权
                val clientToken =
                    getClientToken(
                        TikTokApplication.CLIENT_KEY,
                        TikTokApplication.CLIENT_SECRET
                    )
                val token = clientToken.getOrNull()?.access_token
                logUtils.d("后台", "获取token成功 ${this.isActive}")

                //获取最新版本
                val resp1 = token?.let { MovieNetWork.getVersions(it, 10, 1) }
                val resp2 = token?.let { MovieNetWork.getVersions(it, 10, 2) }
                val resp3 = token?.let { MovieNetWork.getVersions(it, 10, 3) }
                if (resp1?.respDate?.error_code == 0L && resp2?.respDate?.error_code == 0L && resp3?.respDate?.error_code == 0L) {
                    //清空所有的版本缓存
                    val version1 = resp1.respDate.versions
                    if (version1 != null && resp1.respDate.cursor != null) {
                        versionDao.clearAndInsertVersions(
                            1,
                            version1,
                            Date().time,
                            10,
                            resp1.respDate.cursor
                        )
                    } else {
                        //获取失败
                        Result.failure<Boolean>("获取失败".throwRunEx())
                    }
                    val version2 = resp2.respDate.versions
                    if (version2 != null && resp2.respDate.cursor != null) {
                        versionDao.clearAndInsertVersions(
                            2,
                            version2,
                            Date().time,
                            10,
                            resp2.respDate.cursor
                        )
                    } else {
                        //获取失败
                        Result.failure<Boolean>("获取失败".throwRunEx())
                    }
                    val version3 = resp3.respDate.versions
                    if (version3 != null && resp3.respDate.cursor != null) {
                        versionDao.clearAndInsertVersions(
                            3,
                            version3,
                            Date().time,
                            10,
                            resp3.respDate.cursor
                        )
                    } else {
                        //获取失败
                        Result.failure<Boolean>("获取失败".throwRunEx())
                    }
                    logUtils.d("后台", "版本刷新成功")
                    //刷新版本号成功后，清除本周缓存并且重新获取本周缓存
                    val result1 = refreshMovies(token)
                    if (result1.getOrNull() == null) {
                        //获取失败
                        Result.failure("获取失败".throwRunEx())
                    } else {
                        //获取成功
                        Result.success(true)
                    }
                    //获取上一版本的缓存
                    if (version1?.get(0) != null) {
                        val result2 = refreshMovies(token, version1[0].version)
                        if (result2.getOrNull() == null) {
                            //获取失败
                            Result.failure("获取失败".throwRunEx())
                        } else {
                            //获取成功
                            Result.success(true)
                        }
                    } else {
                        //获取失败
                        Result.failure("获取失败".throwRunEx())
                    }
                } else {
                    logUtils.d("后台", "网络申请失败,错误码:${resp1?.respDate?.error_code}")
                    logUtils.d("后台", "网络申请失败,错误码:${resp2?.respDate?.error_code}")
                    logUtils.d("后台", "网络申请失败,错误码:${resp3?.respDate?.error_code}")
                    Result.failure("获取失败".throwRunEx())
                }
            }
        }


    @Transaction
    suspend fun refreshMovies(clientToken: String? = null, version: Int? = null) =
        withContext(Dispatchers.IO) {
            //判断是否有网络
            if (NetUtils.getNetWorkStart(TikTokApplication.context) == NetUtils.NETWORK_NONE) {
                logUtils.d("后台", "网络未连接")
                withContext(Dispatchers.Main) {
                    "网络出现故障了".sendToast()
                }
                Result.failure<Boolean>("获取失败".throwRunEx())
            } else {
                //如果刷新的是本周榜单则清空缓存
                logUtils.d("后台", "开始清空缓存")
                var token = clientToken
                //获取授权如果为空
                if (clientToken == null) {
                    //获取授权
                    val resp =
                        getClientToken(
                            TikTokApplication.CLIENT_KEY,
                            TikTokApplication.CLIENT_SECRET
                        )
                    token = resp.getOrNull()?.access_token
                }

                //清除缓存的操作
                //version?.let { movieDao.deleteMoviesByVersion(version) } ?: movieDao.deleteNullMovies()

                if (token != null) {
                    //开始获取榜单数据
                    token.let {
                        val moviesFilm = MovieNetWork.refreshMovies(1, token, version)
                        val moviesTv = MovieNetWork.refreshMovies(2, token, version)
                        val moviesVariety = MovieNetWork.refreshMovies(3, token, version)
                        if (moviesFilm.respDate.errorCode == 0L && moviesTv.respDate.errorCode == 0L
                            && moviesVariety.respDate.errorCode == 0L
                        ) {
                            val movies = ArrayList<MovieEntity>()
                            for (movie in moviesFilm.respDate.movies!!) {
                                movies.add(movie.toEntity(moviesFilm.respDate.activeTime))
                            }
                            for (movie in moviesTv.respDate.movies!!) {
                                movies.add(movie.toEntity(moviesFilm.respDate.activeTime))
                            }
                            for (movie in moviesVariety.respDate.movies!!) {
                                movies.add(movie.toEntity(moviesFilm.respDate.activeTime))
                            }
                            movieDao.insertMovies(movies)
                            logUtils.d("后台", "刷新今日榜单成功")
                            Result.success(true)
                        } else {
                            logUtils.d("后台", "网络申请失败,电影榜单错误码:${moviesFilm.respDate.errorCode}")
                            logUtils.d("后台", "网络申请失败,影视榜单错误码:${moviesTv.respDate.errorCode}")
                            logUtils.d("后台", "网络申请失败,综艺榜单错误码:${moviesVariety.respDate.errorCode}")
                            Result.failure("获取失败".throwRunEx())
                        }
                    }
                } else {
                    logUtils.d("后台", "token为空")
                    Result.failure("获取失败".throwRunEx())
                }
            }
        }

    @Transaction
    fun clearAllMovies() {
        movieDao.clearAll()
        versionDao.clearAll()
    }

    fun getActivityTimeThisWeek() = movieDao.getActivityTimeThisWeek()
}
