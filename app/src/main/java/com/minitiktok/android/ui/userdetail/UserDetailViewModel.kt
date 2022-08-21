package com.minitiktok.android.ui.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserDetailViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is UserDetail Fragment"
    }
    val text: LiveData<String> = _text

    private val _avatar = MutableLiveData<String>().apply {
        value = ""
    }
    val avater:LiveData<String> = _avatar

    private val _avatar_larger = MutableLiveData<String>().apply {
        value = ""
    }
    val avatar_larger:LiveData<String> = _avatar_larger

    private val _nickname = MutableLiveData<String>().apply {
        value = "张三哥"
    }
    val nickname:LiveData<String> = _nickname

    val following_number:LiveData<Int> = MutableLiveData<Int>(20)
    val fans_total:LiveData<Int> = MutableLiveData<Int>(10)
    val like_total:LiveData<Int> = MutableLiveData<Int>(300)
    val douyin_id:LiveData<String> = MutableLiveData<String>("00123456789")
}