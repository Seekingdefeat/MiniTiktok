package com.minitiktok.android.utils

import android.content.Context
import android.net.ConnectivityManager

object NetUtils {
    //没有网络
   const val NETWORK_NONE = 1

    //移动网络
   const val NETWORK_MOBILE = 0

    //无线网络
    const val NETWORW_WIFI = 2

    //获取网络启动
    fun getNetWorkStart(context: Context): Int {
        //连接服务 CONNECTIVITY_SERVICE
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //网络信息 NetworkInfo
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            //判断是否是wifi
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                //返回无线网络
                //  Toast.makeText(context, "当前处于无线网络", Toast.LENGTH_SHORT).show();
                return NETWORW_WIFI
                //判断是否移动网络
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                //  Toast.makeText(context, "当前处于移动网络", Toast.LENGTH_SHORT).show();
                //返回移动网络
                return NETWORK_MOBILE
            }
        } else {
            //没有网络
            //  Toast.makeText(context, "当前没有网络", Toast.LENGTH_SHORT).show();
            return NETWORK_NONE
        }
        //默认返回  没有网络
        return NETWORK_NONE
    }
}