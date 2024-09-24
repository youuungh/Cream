package com.ninezero.data.mapper

import com.ninezero.data.remote.model.ApiResponse
import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.BrandResponse
import com.ninezero.data.remote.model.CategoryResponse
import com.ninezero.data.remote.model.PriceResponse
import com.ninezero.data.remote.model.ProductResponse
import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Price
import com.ninezero.domain.model.PriceStatus
import com.ninezero.domain.model.Product
import javax.inject.Inject

class ProductMapper @Inject constructor() : BaseMapper<ProductResponse, Product>() {
    override fun getSuccess(model: ProductResponse?, extra: Any?): EntityWrapper.Success<Product> {
        return EntityWrapper.Success(
            model?.toDomain() ?: throw IllegalArgumentException("Product data is null")
        )
    }

    override fun getFailure(error: Throwable): EntityWrapper.Fail<Product> {
        return EntityWrapper.Fail(error)
    }

    fun mapProductsByBrand(result: ApiResult<List<ProductResponse>>): EntityWrapper<List<Product>> =
        when (result.response) {
            is ApiResponse.Success -> EntityWrapper.Success(result.response.data.map { it.toDomain() })
            is ApiResponse.Fail -> EntityWrapper.Fail(result.response.error)
        }

    private fun ProductResponse.toDomain() = Product(
        productId = productId,
        productName = productName,
        ko = ko,
        imageUrl = imageUrl,
        price = price.toDomain(),
        tradingVolume = tradingVolume,
        releaseDate = releaseDate,
        mainColor = mainColor,
        category = category.toDomain(),
        brand = brand.toDomain(),
        isNew = isNew,
        isFreeShipping = isFreeShipping,
        isSaved = isSaved
    )

    private fun PriceResponse.toDomain() = Price(
        releasePrice = if (releasePrice == "-") null else releasePrice?.toIntOrNull(),
        instantBuyPrice = instantBuyPrice,
        status = PriceStatus.valueOf(status)
    )

    private fun CategoryResponse.toDomain() = Category(
        categoryId = categoryId,
        categoryName = categoryName,
        ko = ko
    )

    private fun BrandResponse.toDomain() = Brand(
        brandId = brandId,
        brandName = brandName,
        ko = ko,
        imageUrl = imageUrl
    )
}