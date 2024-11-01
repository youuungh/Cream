package com.ninezero.di

import com.ninezero.data.datasource.RemoteDataSource
import com.ninezero.data.datasource.RemoteDataSourceImpl
import com.ninezero.data.repository.CartRepositoryImpl
import com.ninezero.data.repository.CategoryRepositoryImpl
import com.ninezero.domain.repository.HomeRepository
import com.ninezero.data.repository.HomeRepositoryImpl
import com.ninezero.data.repository.OrderRepositoryImpl
import com.ninezero.data.repository.ProductRepositoryImpl
import com.ninezero.data.repository.SaveRepositoryImpl
import com.ninezero.data.repository.SearchRepositoryImpl
import com.ninezero.domain.repository.CartRepository
import com.ninezero.domain.repository.CategoryRepository
import com.ninezero.domain.repository.OrderRepository
import com.ninezero.domain.repository.ProductRepository
import com.ninezero.domain.repository.SaveRepository
import com.ninezero.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindRemoteDataSource(remoteDataSourceImpl: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    fun bindHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    fun bindSaveRepository(saveRepositoryImpl: SaveRepositoryImpl): SaveRepository

    @Binds
    @Singleton
    fun bindCartRepository(cartRepositoryImpl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    fun bindOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository
}