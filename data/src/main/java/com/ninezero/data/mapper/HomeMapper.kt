package com.ninezero.data.mapper

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.data.remote.model.BannerResponse
import com.ninezero.data.remote.model.BrandResponse
import com.ninezero.data.remote.model.CategoryResponse
import com.ninezero.data.remote.model.HomeResponse
import com.ninezero.data.remote.model.PriceResponse
import com.ninezero.data.remote.model.ProductResponse
import com.ninezero.data.remote.model.TopBannerResponse
import com.ninezero.domain.model.Banner
import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.model.Price
import com.ninezero.domain.model.PriceStatus
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.TopBanner
import javax.inject.Inject

class HomeMapper @Inject constructor() : BaseMapper<HomeResponse, HomeData>() {
    override fun getSuccess(model: HomeResponse?, extra: Any?): EntityWrapper.Success<HomeData> {
        return EntityWrapper.Success(
            HomeData(
                topBanners = model?.topBanners?.map { it.toDomain() } ?: emptyList(),
                banner = model?.banner?.toDomain(),
                justDropped = model?.products
                    ?.asSequence()
                    ?.sortedByDescending { it.releaseDate }
                    ?.take(10)
                    ?.map { it.toDomain() }
                    ?.toList() ?: emptyList(),
                mostPopular = model?.products
                    ?.asSequence()
                    ?.sortedByDescending { it.tradingVolume }
                    ?.take(10)
                    ?.map { it.toDomain() }
                    ?.toList() ?: emptyList(),
                forYou = model?.products
                    ?.asSequence()
                    ?.shuffled()
                    ?.map { it.toDomain() }
                    ?.toList() ?: emptyList(),
                brands = model?.products
                    ?.asSequence()
                    ?.map { it.brand.toDomain() }
                    ?.distinctBy { it.brandId }
                    ?.toList() ?: emptyList()
            )
        )
    }

    override fun getFailure(error: Throwable): EntityWrapper.Fail<HomeData> {
        return EntityWrapper.Fail(error)
    }

    private fun TopBannerResponse.toDomain() = TopBanner(
        bannerId = bannerId,
        imageUrl = listOf(imageUrl)
    )

    private fun BannerResponse.toDomain() = Banner(
        bannerId = bannerId,
        imageUrl = imageUrl
    )

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