package com.minitiktok.android.utils.funs

import java.util.*

//获取一个时间戳的前后的某个时间
fun Date.getTodayHour(
    offset: Int,
    hour: Int,
    timeZone: TimeZone? = TimeZone.getTimeZone("UTC")
): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    if (timeZone != null) {
        cal.timeZone = timeZone
    }
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal[Calendar.DAY_OF_MONTH] += offset
    return cal.time
}

//获取这个星期一个时间戳的前后的某个时间
fun Date.getThisWeekHour(
    offset: Int,
    hour: Int,
    timeZone: TimeZone? = TimeZone.getTimeZone("UTC")
): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    if (timeZone != null) {
        cal.timeZone = timeZone
    }
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.minimalDaysInFirstWeek = 7
    logUtils.d("后台，", cal.timeZone.displayName)
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek + offset
    return cal.time
}


//获得今天是这个星期的第几天
fun Date.getDayOfWeek(
    timeZone: TimeZone? = TimeZone.getTimeZone("UTC")
): Int {
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.time = this
    if (timeZone != null) {
        cal.timeZone = timeZone
    }
    return cal[Calendar.DAY_OF_WEEK] - 1
}