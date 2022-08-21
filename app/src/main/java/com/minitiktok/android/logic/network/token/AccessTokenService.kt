package com.minitiktok.android.logic.network.token

import com.minitiktok.android.logic.model.AccessTokenResponse
import com.minitiktok.android.logic.model.ClientTokenResp
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AccessTokenService {
    //Content-Type: application/x-www-form-urlencoded
    @POST("oauth/access_token/?grant_type=authorization_code")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    fun getAccessToken(
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String,
        @Query("client_key") clientKey: String
    ): Call<AccessTokenResponse>


    //Content-Type: multipart/form-data
    @Headers("Content-Type:multipart/form-data")
    @POST("oauth/client_token/")
    fun getClientToken(
        @Query("client_key") clientKey: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grant_type: String = "client_credential"
    ): Call<ClientTokenResp>
}