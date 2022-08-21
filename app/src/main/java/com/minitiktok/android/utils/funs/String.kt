package com.minitiktok.android.utils.funs

import android.widget.Toast
import com.minitiktok.android.TikTokApplication

fun String.sendToast(duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(TikTokApplication.context, this, duration).show()
}

fun String.throwRunEx() = RuntimeException(this)

fun List<String>.movieFormat(pattern: String): String {
    val list = this
    return StringBuilder().apply {
        for (str in list) {
            append(str)
            append(pattern)
        }
        deleteAt(this.length - 1)
    }.toString()
}

fun String.movieDecode(pattern: String) = this.split(pattern)