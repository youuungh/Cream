package com.ninezero.data.datasource

import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.CategoryDetailsResponse
import com.ninezero.data.remote.model.CategoryResponse
import com.ninezero.data.remote.model.HomeResponse

interface RemoteDataSource {
    suspend fun fetchData(): ApiResult<HomeResponse>
    suspend fun getCategories(): ApiResult<List<CategoryResponse>>
    suspend fun getCategoryDetails(categoryId: String): ApiResult<CategoryDetailsResponse>
}