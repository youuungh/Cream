package com.ninezero.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ninezero.data.local.UserPreferences
import com.ninezero.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val userPrefs: UserPreferences
) : AuthService {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = fAuth.signInWithCredential(credential).await()
            val fUser = authResult.user
            if (fUser != null) {
                val user = User(
                    id = fUser.uid,
                    email = fUser.email,
                    name = fUser.displayName,
                    profileUrl = fUser.photoUrl?.toString(),
                    authType = User.AuthType.GOOGLE
                )
                userPrefs.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithCustomToken(token: String, authType: User.AuthType): Result<User> {
        return try {
            val authResult = fAuth.signInWithCustomToken(token).await()
            val fUser = authResult.user
            if (fUser != null) {
                val user = User(
                    id = fUser.uid,
                    email = fUser.email,
                    name = fUser.displayName,
                    profileUrl = fUser.photoUrl?.toString(),
                    authType = authType
                )
                userPrefs.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            fAuth.signOut()
            userPrefs.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? = userPrefs.getUser()
}