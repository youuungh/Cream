package com.ninezero.data.di

import android.content.Context
import com.google.gson.Gson
import com.ninezero.data.BuildConfig
import com.ninezero.data.datasource.NetworkStatus
import com.ninezero.data.remote.api.ApiService
import com.ninezero.data.remote.api.CustomTokenApiService
import com.ninezero.data.remote.retrofit.NetworkRequestFactory
import com.ninezero.data.remote.retrofit.NetworkRequestFactoryImpl
import com.ninezero.data.remote.retrofit.StringConverterFactory
import com.ninezero.data.repository.NetworkRepositoryImpl
import com.ninezero.data.utils.CREAM_API
import com.ninezero.data.utils.KAKAO_FUNCTIONS_URL
import com.ninezero.data.utils.NAVER_FUNCTIONS_URL
import com.ninezero.domain.repository.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(logBaseUrl(CREAM_API))
            .addConverterFactory(StringConverterFactory(gson))
            .build()

    private fun logBaseUrl(baseUrl: String): String {
        Timber.d("baseUrl $baseUrl")
        return baseUrl
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    @Named("NaverRetrofit")
    fun provideNaverRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(NAVER_FUNCTIONS_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("KakaoRetrofit")
    fun provideKakaoRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(KAKAO_FUNCTIONS_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("NaverCustomTokenApiService")
    fun provideNaverCustomTokenApiService(@Named("NaverRetrofit") retrofit: Retrofit): CustomTokenApiService =
        retrofit.create(CustomTokenApiService::class.java)

    @Provides
    @Singleton
    @Named("KakaoCustomTokenApiService")
    fun provideKakaoCustomTokenApiService(@Named("KakaoRetrofit") retrofit: Retrofit): CustomTokenApiService =
        retrofit.create(CustomTokenApiService::class.java)

    @Provides
    @Singleton
    fun provideNetworkStatus(@ApplicationContext context: Context): NetworkStatus =
        NetworkStatus(context)

    @Provides
    @Singleton
    fun provideNetworkRepository(networkStatus: NetworkStatus): NetworkRepository =
        NetworkRepositoryImpl(networkStatus)

    @Provides
    @Singleton
    fun provideNetworkRequestFactory(networkRequestFactory: NetworkRequestFactoryImpl): NetworkRequestFactory =
        networkRequestFactory
}