package com.minitiktok.android.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

data class ClientTokenResp(
    @SerializedName("data") val respData: ClientTokenRespData,
    val message: String
)

data class ClientTokenRespData(
    val access_token: String,
    val error_code: Int,
    val description: String,
    val expires_in: Int
)

@Entity
data class ClientToken(
    var access_token: String? = null,
    var description: String? = null,
    var expires_in: Int? = null,
    var client_key: String? = null,
    var create_time: Long? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

fun ClientTokenRespData.toEntity(clientKey: String) =
    ClientToken(access_token, description, expires_in, clientKey, Date().time)

fun ClientToken.isExpire() =
    Date().time.div(1000).minus(this.create_time!!.div(1000)) > this.expires_in!!