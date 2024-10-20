package com.ninezero.domain.usecase

import com.ninezero.domain.model.User
import com.ninezero.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun signInWithGoogle(idToken: String): Result<User> =
        authRepository.signInWithGoogle(idToken)

    suspend fun signInWithNaver(accessToken: String): Result<User> =
        authRepository.signInWithNaver(accessToken)

    suspend fun signInWithKakao(accessToken: String): Result<User> =
        authRepository.signInWithKakao(accessToken)

    suspend fun signOut(): Result<Unit> = authRepository.signOut()

    suspend fun getCurrentUser(): User? = authRepository.getCurrentUser()
}