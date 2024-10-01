package com.ninezero.data.datasource

import com.ninezero.data.remote.model.ApiResponse
import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.CategoryDetailsResponse
import com.ninezero.data.remote.model.CategoryResponse
import com.ninezero.data.remote.model.HomeResponse
import com.ninezero.data.remote.model.NetworkRequestInfo
import com.ninezero.data.remote.model.ProductResponse
import com.ninezero.data.remote.model.RequestType
import com.ninezero.data.remote.retrofit.NetworkRequestFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val networkRequestFactory: NetworkRequestFactory
) : RemoteDataSource {
    private var cachedHomeResponse: HomeResponse? = null

    override fun fetchData(): Flow<ApiResult<HomeResponse>> = flow {
        cachedHomeResponse?.let {
            emit(ApiResult(ApiResponse.Success(it)))
            return@flow
        }

        val result = networkRequestFactory.create<HomeResponse>(
            url = "cream.json",
            requestInfo = NetworkRequestInfo.Builder(RequestType.GET).build(),
            type = HomeResponse::class.java
        )

        if (result.response is ApiResponse.Success)
            cachedHomeResponse = result.response.data

        emit(result)
    }

    override fun getProductDetails(productId: String): Flow<ApiResult<ProductResponse>> = flow {
        fetchData().collect { result ->
            when (val response = result.response) {
                is ApiResponse.Success -> {
                    val product = response.data.products.find { it.productId == productId }
                    if (product != null) {
                        emit(ApiResult(ApiResponse.Success(product)))
                    } else {
                        emit(ApiResult(ApiResponse.Fail(Throwable("Product not found"))))
                    }
                }
                is ApiResponse.Fail -> emit(ApiResult(response))
            }

        }
    }

    override fun getProductsByBrand(brandId: String): Flow<ApiResult<List<ProductResponse>>> = flow {
        fetchData().collect { result ->
            when (val response = result.response) {
                is ApiResponse.Success -> {
                    val products = response.data.products.filter { it.brand.brandId == brandId }
                    emit(ApiResult(ApiResponse.Success(products)))
                }
                is ApiResponse.Fail -> emit(ApiResult(response))
            }
        }
    }

    override fun getCategories(): Flow<ApiResult<List<CategoryResponse>>> = flow {
        fetchData().collect { result ->
            when (val response = result.response) {
                is ApiResponse.Success -> {
                    val categories = response.data.products.map { it.category }.distinctBy { it.categoryId }
                    emit(ApiResult(ApiResponse.Success(categories)))
                }
                is ApiResponse.Fail -> emit(ApiResult(response))
            }
        }
    }

    override fun getCategoryDetails(categoryId: String): Flow<ApiResult<CategoryDetailsResponse>> = flow {
        fetchData().collect { result ->
            when (val response = result.response) {
                is ApiResponse.Success -> {
                    val category = response.data.products.first { it.category.categoryId == categoryId }.category
                    val products = response.data.products.filter { it.category.categoryId == categoryId }
                    emit(ApiResult(ApiResponse.Success(CategoryDetailsResponse(category, products))))
                }
                is ApiResponse.Fail -> emit(ApiResult(response))
            }
        }
    }
}