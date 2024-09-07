package com.ninezero.data.repository

import com.ninezero.data.datasource.RemoteDataSource
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.data.mapper.HomeMapper
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val homeMapper: HomeMapper
) : HomeRepository {
    override fun fetchHomeData(): Flow<EntityWrapper<HomeData>> = flow {
        emit(homeMapper.mapFromResult(remoteDataSource.getHomeData()))
    }.catch { e ->
        emit(EntityWrapper.Fail(e))
    }
}

