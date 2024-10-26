package com.ninezero.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.functions.FirebaseFunctions
import com.ninezero.data.service.FirebaseAuthService
import com.ninezero.data.service.FirebaseCustomTokenService
import com.ninezero.data.local.UserPreferences
import com.ninezero.data.remote.api.CustomTokenApiService
import com.ninezero.data.repository.AuthRepositoryImpl
import com.ninezero.domain.repository.AuthRepository
import com.ninezero.data.service.AuthService
import com.ninezero.data.service.CustomTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(com.ninezero.cream.R.string.default_web_client_id))
            .requestId()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideAuthService(firebaseAuth: FirebaseAuth, userPreferences: UserPreferences): AuthService {
        return FirebaseAuthService(firebaseAuth, userPreferences)
    }

    @Provides
    @Singleton
    fun provideCustomTokenService(
        @Named("NaverCustomTokenApiService") naverApiService: CustomTokenApiService,
        @Named("KakaoCustomTokenApiService") kakaoApiService: CustomTokenApiService
    ): CustomTokenService {
        return FirebaseCustomTokenService(naverApiService, kakaoApiService)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        customTokenService: CustomTokenService
    ): AuthRepository {
        return AuthRepositoryImpl(authService, customTokenService)
    }
}