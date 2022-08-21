package com.minitiktok.android.logic.model

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
    @SerializedName("data") val respData: AccessTokenResponseData,
    val message: String
)

data class AccessTokenResponseData(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("open_id") val openId: String,
    @SerializedName("refresh_expires_in") val refreshExpireIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("access_token") val accessToken: String,
    val scope: String,
    val description: String
)