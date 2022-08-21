package com.minitiktok.android

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory
import com.bytedance.sdk.open.douyin.DouYinOpenConfig
import com.minitiktok.android.logic.network.movie.GetMovieService

class TikTokApplication : Application() {
    companion object {
        @Suppress("StaticFieldLeak")
        lateinit var context: Context
        const val CLIENT_KEY = "aw5aucrvxokli37i"
        const val CLIENT_SECRET = "4f7e2faf07f89de2df59e0340feae4f2"
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        DouYinOpenApiFactory.init(DouYinOpenConfig(CLIENT_KEY))
        //开启service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, GetMovieService::class.java))
        } else {
            startService(Intent(this, GetMovieService::class.java))
        }
    }

}