package com.ninezero.data.service

import com.ninezero.data.remote.api.CustomTokenApiService
import com.ninezero.data.remote.api.TokenRequest
import javax.inject.Inject
import javax.inject.Named

class FirebaseCustomTokenService @Inject constructor(
    @Named("NaverCustomTokenApiService") private val naverApiService: CustomTokenApiService,
    @Named("KakaoCustomTokenApiService") private val kakaoApiService: CustomTokenApiService
) : CustomTokenService {

    override suspend fun getNaverCustomToken(accessToken: String): Result<String> {
        return try {
            val response = naverApiService.getNaverCustomToken(TokenRequest(accessToken))
            if (response.firebaseToken.isEmpty()) {
                Result.failure(Exception("Firebase token is null or empty"))
            } else {
                Result.success(response.firebaseToken)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getKakaoCustomToken(accessToken: String): Result<String> {
        return try {
            val response = kakaoApiService.getKakaoCustomToken(TokenRequest(accessToken))
            if (response.firebaseToken.isEmpty()) {
                Result.failure(Exception("Firebase token is null or empty"))
            } else {
                Result.success(response.firebaseToken)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}