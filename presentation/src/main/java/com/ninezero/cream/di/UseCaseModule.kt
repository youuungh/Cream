package com.ninezero.cream.di

import com.ninezero.domain.repository.CartRepository
import com.ninezero.domain.repository.OrderRepository
import com.ninezero.domain.repository.ProductRepository
import com.ninezero.domain.repository.SaveRepository
import com.ninezero.domain.repository.SearchRepository
import com.ninezero.domain.usecase.AuthUseCase
import com.ninezero.domain.usecase.CartUseCase
import com.ninezero.domain.usecase.OrderUseCase
import com.ninezero.domain.usecase.SaveUseCase
import com.ninezero.domain.usecase.SearchUseCase
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

    @Provides
    @Singleton
    fun provideSearchUseCase(
        productRepository: ProductRepository,
        searchRepository: SearchRepository
    ): SearchUseCase {
        return SearchUseCase(productRepository, searchRepository)
    }

    @Provides
    @Singleton
    fun provideOrderUseCase(
        orderRepository: OrderRepository,
        authUseCase: AuthUseCase
    ) : OrderUseCase {
        return OrderUseCase(orderRepository, authUseCase)
    }
}