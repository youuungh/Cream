package com.ninezero.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ninezero.domain.model.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PrefsKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PROFILE_URL = stringPreferencesKey("user_profile_url")
        val USER_AUTH_TYPE = stringPreferencesKey("user_auth_type")
    }

    private val mutex = Mutex()

    suspend fun saveUser(user: User) = mutex.withLock {
        dataStore.edit {
            it[PrefsKeys.USER_ID] = user.id
            it[PrefsKeys.USER_EMAIL] = user.email ?: ""
            it[PrefsKeys.USER_NAME] = user.name ?: ""
            it[PrefsKeys.USER_PROFILE_URL] = user.profileUrl ?: ""
            it[PrefsKeys.USER_AUTH_TYPE] = user.authType.name
        }
    }

    suspend fun getUser(): User? = mutex.withLock {
        return dataStore.data.map { preferences ->
            val userId = preferences[PrefsKeys.USER_ID] ?: return@map null
            User(
                id = userId,
                email = preferences[PrefsKeys.USER_EMAIL],
                name = preferences[PrefsKeys.USER_NAME],
                profileUrl = preferences[PrefsKeys.USER_PROFILE_URL],
                authType = User.AuthType.valueOf(
                    preferences[PrefsKeys.USER_AUTH_TYPE] ?: User.AuthType.GOOGLE.name
                )
            )
        }.firstOrNull()
    }

    suspend fun clearUser() = mutex.withLock { dataStore.edit { it.clear() } }
}