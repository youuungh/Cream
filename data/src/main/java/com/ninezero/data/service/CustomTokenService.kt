package com.ninezero.data.service

interface CustomTokenService {
    suspend fun getNaverCustomToken(accessToken: String): Result<String>
    suspend fun getKakaoCustomToken(accessToken: String): Result<String>
}