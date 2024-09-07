package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("ko")
    val ko: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("price")
    val price: PriceResponse,
    @SerializedName("tradingVolume")
    val tradingVolume: Int,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("mainColor")
    val mainColor: String,
    @SerializedName("category")
    val category: CategoryResponse,
    @SerializedName("brand")
    val brand: BrandResponse,
    @SerializedName("isNew")
    val isNew: Boolean,
    @SerializedName("isFreeShipping")
    val isFreeShipping: Boolean,
    @SerializedName("isSaved")
    val isSaved: Boolean
)
