package com.ninezero.data.repository

import com.ninezero.data.datasource.RemoteDataSource
import com.ninezero.data.mapper.ProductMapper
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val productMapper: ProductMapper
) : ProductRepository {
    override fun fetchProductDetails(productId: String): Flow<EntityWrapper<Product>> =
        flow {
            emit(productMapper.mapFromResult(remoteDataSource.getProductDetails(productId)))
        }.catch { e ->
            emit(EntityWrapper.Fail(e))
        }
}