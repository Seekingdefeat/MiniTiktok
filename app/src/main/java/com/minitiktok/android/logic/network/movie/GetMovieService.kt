package com.minitiktok.android.logic.network.movie

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.minitiktok.android.TikTokApplication
import com.minitiktok.android.logic.Repository
import com.minitiktok.android.utils.funs.getDayOfWeek
import com.minitiktok.android.utils.funs.getTodayHour
import com.minitiktok.android.utils.funs.logUtils
import com.minitiktok.android.utils.funs.sendToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class GetMovieService : Service() {
    private val anHour: Long = 60 * 60 * 1000
    lateinit var activeTime: Date

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "40",
                    "MovieService",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        startForeground(2, NotificationCompat.Builder(this, "40").build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //这里开辟一条线程,用来执行具体的逻辑操作:
        GlobalScope.launch(Dispatchers.IO) {
            //下次更新时间
            var triggerAtTime: Long?
            //中国时区
            val timeZone = TimeZone.getTimeZone("GMT+08")
            //格式化工具
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            sdf.timeZone = timeZone
            //获取当前时间
            val date = Date()
            logUtils.d("后台", "执行刷新的实时时间：${sdf.format(date.time)}")
            try {
                /**
                 * 如果是星期1则需要获取上一周的往期榜单的版本，并且重新获取本周缓存
                 * 如果不是星期1，则需要计算定时时间
                 * 如果现在是大于12点，则需要先清除数据库的本周缓存，然后从网络重新请求，然后计算下一次刷新的时间
                 * 如果现在是小于12点，则需要直接计算还需要多少时间进行下一次刷新
                 */
                //获取今天12点
                val todayHour = date.getTodayHour(0, 12, timeZone)
                val tomorrowHour = date.getTodayHour(1, 12, timeZone)
                //获取上次榜单生成的时间
                val activityTimeThisWeek = Repository.getActivityTimeThisWeek()
                if (activityTimeThisWeek != null) {
                    activeTime = sdf.parse(activityTimeThisWeek)
                } else {
                    activeTime = todayHour.getTodayHour(-1, 12, timeZone)
                }
                logUtils.d("后台", "榜单生成时间： ${activeTime.toString()}")
                //获取下次榜单更新的形式时间
                val nextActivityTime = activeTime.getTodayHour(1, 12, timeZone)
                logUtils.d("后台", "下一份榜单生成时间： ${nextActivityTime.toString()}")
                //今天是这周的第几天
                val dayOfWeek = date.getDayOfWeek(timeZone)
                logUtils.d("后台", "今天是星期 ${dayOfWeek.toString()}")
                if (nextActivityTime.time < date.time) {
                    //实际操作
                    if (dayOfWeek == 1) {
                        //如果是星期一且大于下次更新时间则需要进行旧版本的缓存和重新刷新今天的榜单
                        logUtils.d("后台", "开始进行旧版本的缓存和重新刷新今天的榜单")
                        val result = Repository.refreshMovieCache()
                        if (result.isFailure) {
                            //操作失败清空一切缓存
                            //Repository.clearAllMovies()
                            //操作失败，15分钟后重试
                            triggerAtTime = Date().time + 15 * 60 * 1000
                            logUtils.d("后台", "下次榜单的刷新时间：${sdf.format(triggerAtTime)}")
                        } else {
                            //操作失败清空一切缓存
                            //Repository.clearAllMovies()
                            //操作完成，明天中午刷新
                            logUtils.d("后台", "下次榜单的刷新时间：${sdf.format(tomorrowHour)}")
                            triggerAtTime = tomorrowHour.time
                        }
                    } else {
                        //如果大于下次更新时间则需要重新刷新今天的榜单
                        logUtils.d("后台", "开始重新刷新今天的榜单")
                        val result = Repository.refreshMovies()
                        if (result.isFailure) {
                            //操作失败，15分钟后重试
                            triggerAtTime = Date().time + 15 * 60 * 1000
                            logUtils.d("后台", "下次榜单的刷新时间：${sdf.format(triggerAtTime)}")
                        } else {
                            //操作完成,明天中午刷新
                            logUtils.d("后台", "下次榜单的刷新时间：${sdf.format(tomorrowHour)}")
                            triggerAtTime = tomorrowHour.time
                        }
                    }
                } else {
                    //如果小于下次更新时间，则设定刷新时间为下次更新时间
                    logUtils.d("后台", "下次榜单的刷新时间：${sdf.format(nextActivityTime)}")
                    triggerAtTime = nextActivityTime.time
                }
                //开启下一个定时任务
                val manager = getSystemService(ALARM_SERVICE) as AlarmManager
                val i = Intent(TikTokApplication.context, AlarmReceiver::class.java)
                val pi = PendingIntent.getBroadcast(TikTokApplication.context, 0, i, 0)
                manager[AlarmManager.RTC_WAKEUP, triggerAtTime] = pi
            } catch (e: Exception) {
                triggerAtTime = Date().time + 15 * 60 * 1000
                logUtils.d("后台", "下次榜单的刷新时间：${sdf.format(triggerAtTime)}")
                val manager = getSystemService(ALARM_SERVICE) as AlarmManager
                val i = Intent(TikTokApplication.context, AlarmReceiver::class.java)
                val pi = PendingIntent.getBroadcast(TikTokApplication.context, 0, i, 0)
                manager[AlarmManager.RTC_WAKEUP, triggerAtTime] = pi
                e.printStackTrace()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val i = Intent(context, GetMovieService::class.java)
        context.startService(i)
        "开启定时任务".sendToast()
    }
}



