package com.ninezero.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomTokenApiService {
    @POST("/")
    suspend fun getNaverCustomToken(@Body body: TokenRequest): TokenResponse

    @POST("/")
    suspend fun getKakaoCustomToken(@Body body: TokenRequest): TokenResponse
}

data class TokenRequest(val token: String)
data class TokenResponse(@SerializedName("firebase_token") val firebaseToken: String)