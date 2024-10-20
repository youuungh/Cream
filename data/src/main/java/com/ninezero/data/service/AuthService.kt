package com.ninezero.data.service

import com.ninezero.domain.model.User

interface AuthService {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithCustomToken(token: String, authType: User.AuthType): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
}