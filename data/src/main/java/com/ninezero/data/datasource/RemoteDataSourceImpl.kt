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
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val networkRequestFactory: NetworkRequestFactory
) : RemoteDataSource {
    private var cachedHomeResponse: HomeResponse? = null

    override suspend fun fetchData(): ApiResult<HomeResponse> {
        cachedHomeResponse?.let { return ApiResult(ApiResponse.Success(it)) }

        return networkRequestFactory.create<HomeResponse>(
            url = "cream.json",
            requestInfo = NetworkRequestInfo.Builder(RequestType.GET).build(),
            type = HomeResponse::class.java
        ).also {
            if (it.response is ApiResponse.Success) {
                cachedHomeResponse = it.response.data
            }
        }
    }

    override suspend fun getProductDetails(productId: String): ApiResult<ProductResponse> {
        return when (val response = fetchData().response) {
            is ApiResponse.Success -> {
                val product = response.data.products.find { it.productId == productId }
                if (product != null) {
                    ApiResult(ApiResponse.Success(product))
                } else {
                    ApiResult(ApiResponse.Fail(Exception("Product not found")))
                }
            }
            is ApiResponse.Fail -> ApiResult(response)
        }
    }

    override suspend fun getProductsByBrand(brandId: String): ApiResult<List<ProductResponse>> {
        return when (val response = fetchData().response) {
            is ApiResponse.Success -> {
                val products = response.data.products.filter { it.brand.brandId == brandId }
                ApiResult(ApiResponse.Success(products))
            }
            is ApiResponse.Fail -> ApiResult(response)
        }
    }

    override suspend fun getCategories(): ApiResult<List<CategoryResponse>> {
        return when (val response = fetchData().response) {
            is ApiResponse.Success -> {
                val categories =
                    response.data.products.map { it.category }.distinctBy { it.categoryId }
                ApiResult(ApiResponse.Success(categories))
            }

            is ApiResponse.Fail -> ApiResult(response)
        }
    }

    override suspend fun getCategoryDetails(categoryId: String): ApiResult<CategoryDetailsResponse> {
        return when (val response = fetchData().response) {
            is ApiResponse.Success -> {
                val category =
                    response.data.products.first { it.category.categoryId == categoryId }.category
                val products =
                    response.data.products.filter { it.category.categoryId == categoryId }
                ApiResult(ApiResponse.Success(CategoryDetailsResponse(category, products)))
            }

            is ApiResponse.Fail -> ApiResult(response)
        }
    }
}