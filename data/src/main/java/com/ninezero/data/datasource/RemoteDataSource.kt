package com.ninezero.data.datasource

import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.CategoryDetailsResponse
import com.ninezero.data.remote.model.CategoryResponse
import com.ninezero.data.remote.model.HomeResponse
import com.ninezero.data.remote.model.ProductResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun fetchData(): Flow<ApiResult<HomeResponse>>
    fun getProductDetails(productId: String): Flow<ApiResult<ProductResponse>>
    fun getProductsByBrand(brandId: String): Flow<ApiResult<List<ProductResponse>>>
    fun getCategories(): Flow<ApiResult<List<CategoryResponse>>>
    fun getCategoryDetails(categoryId: String): Flow<ApiResult<CategoryDetailsResponse>>
}