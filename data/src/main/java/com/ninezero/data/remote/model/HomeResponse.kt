package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    @SerializedName("topBanners")
    val topBanners: List<TopBannerResponse>,
    @SerializedName("banner")
    val banner: BannerResponse,
    @SerializedName("product")
    val products: List<ProductResponse>
)
