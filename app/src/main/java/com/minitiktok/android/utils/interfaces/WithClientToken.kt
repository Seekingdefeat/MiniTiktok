package com.minitiktok.android.utils.interfaces

import com.minitiktok.android.logic.model.ClientToken
import com.minitiktok.android.logic.model.ClientTokenResp
import com.minitiktok.android.logic.model.isExpire
import com.minitiktok.android.logic.model.toEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

interface WithClientToken {
    //从数据库获取token成功后的回调
    fun onTokenByDb(result: ClientToken): (() -> Unit)?

    //从网络获取token成功之后的回调
    fun onTokenByNet(result: ClientToken): (() -> Unit)?

    //写下获取token失败后的回调
    fun onTokenFailure(result: String): (() -> Unit)
}

interface ClientTokenRepository {
    fun insertClientToken(token: ClientToken): Long

    fun getClientTokenByKey(clientKey: String): List<ClientToken>

    fun clearTokens()

    suspend fun getClientTokenByNet(clientKey: String, clientSecret: String): ClientTokenResp
}

//刷新client_Token的方法
suspend fun WithClientToken.refreshToken(
    repository: ClientTokenRepository,
    client_key: String,
    client_secret: String? = null
) = coroutineScope {
    launch {
        try {
            //尝试从数据库获取
            val clientTokenByKey =
                repository.getClientTokenByKey(client_key)
            if (clientTokenByKey.isNotEmpty() && !clientTokenByKey[0].isExpire()) {
                //数据库获取成功回调
                onTokenByDb(clientTokenByKey[0])?.invoke()
            } else {
                if (client_secret == null) {
                    onTokenFailure("无法从数据库获取，请输入client_secret").invoke()
                } else {
                    //清空token
                    repository.clearTokens()
                    //尝试从网络获取
                    val clientToken = repository.getClientTokenByNet(
                        client_key,
                        client_secret
                    )
                    if (clientToken.respData.error_code == 0) {
                        //获取成功
                        val token = clientToken.respData.toEntity(client_key)
                        //插入数据库
                        repository.insertClientToken(token)
                        //网络获取成功回调
                        onTokenByNet(token)?.invoke()
                    } else {
                        //获取失败回调
                        onTokenFailure("错误码：${clientToken.respData.error_code}").invoke()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}