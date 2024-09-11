package com.ninezero.di

import com.ninezero.data.datasource.RemoteDataSource
import com.ninezero.data.datasource.RemoteDataSourceImpl
import com.ninezero.data.repository.CategoryRepositoryImpl
import com.ninezero.domain.repository.HomeRepository
import com.ninezero.data.repository.HomeRepositoryImpl
import com.ninezero.domain.repository.CategoryRepository
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
    abstract fun bindRemoteDataSource(remoteDataSourceImpl: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(categoryRepository: CategoryRepositoryImpl): CategoryRepository
}