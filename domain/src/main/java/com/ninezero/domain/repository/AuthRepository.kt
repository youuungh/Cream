package com.ninezero.domain.repository

import com.ninezero.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithNaver(accessToken: String): Result<User>
    suspend fun signInWithKakao(accessToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
}