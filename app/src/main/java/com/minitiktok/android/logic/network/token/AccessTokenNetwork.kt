package com.minitiktok.android.logic.network.token

import com.minitiktok.android.utils.funs.netAwait
import com.minitiktok.android.logic.network.ServiceCreator

object AccessTokenNetwork {
    private val accessTokenService = ServiceCreator.create<AccessTokenService>()

    suspend fun getAccessToken(
        clientSecret: String,
        code: String,
        clientKey: String
    ) = accessTokenService.getAccessToken(clientSecret, code, clientKey).netAwait();

    suspend fun getClientToken(
        clientKey: String, clientSecret: String
    ) = accessTokenService.getClientToken(clientKey, clientSecret).netAwait()
}