package com.minitiktok.android.utils.funs

import android.app.Activity
import com.bytedance.sdk.open.aweme.authorize.model.Authorization
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory

//开启授权
fun Activity.getAccessToken(block: Authorization.Request.() -> Unit): Boolean {
    val douYinOpenApi = DouYinOpenApiFactory.create(this);
    val request = Authorization.Request()
    request.block();
    return douYinOpenApi.authorize(request)
}