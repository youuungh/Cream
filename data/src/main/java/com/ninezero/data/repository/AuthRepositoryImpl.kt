package com.ninezero.data.repository

import com.ninezero.domain.model.User
import com.ninezero.domain.repository.AuthRepository
import com.ninezero.data.service.AuthService
import com.ninezero.data.service.CustomTokenService
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val customTokenService: CustomTokenService
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        authService.signInWithGoogle(idToken)

    override suspend fun signInWithNaver(accessToken: String): Result<User> =
        customTokenService.getNaverCustomToken(accessToken).mapCatching { token ->
            authService.signInWithCustomToken(token, User.AuthType.NAVER).getOrThrow()
        }

    override suspend fun signInWithKakao(accessToken: String): Result<User> =
        customTokenService.getKakaoCustomToken(accessToken).mapCatching { token ->
            authService.signInWithCustomToken(token, User.AuthType.KAKAO).getOrThrow()
        }

    override suspend fun signOut(): Result<Unit> = authService.signOut()

    override suspend fun getCurrentUser(): User? = authService.getCurrentUser()
}