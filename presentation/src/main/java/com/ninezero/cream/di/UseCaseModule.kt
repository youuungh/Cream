package com.ninezero.cream.di

import com.ninezero.domain.repository.CartRepository
import com.ninezero.domain.repository.SaveRepository
import com.ninezero.domain.usecase.CartUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSaveUseCase(
        saveRepository: SaveRepository
    ): SaveUseCase {
        return SaveUseCase(saveRepository, CoroutineScope(SupervisorJob()))
    }

    @Provides
    @Singleton
    fun provideCartUseCase(
        cartRepository: CartRepository
    ): CartUseCase {
        return CartUseCase(cartRepository)
    }
}