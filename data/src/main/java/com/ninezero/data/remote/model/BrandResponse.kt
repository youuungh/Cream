package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class BrandResponse(
    @SerializedName("brandId")
    val brandId: String,
    @SerializedName("brandName")
    val brandName: String,
    @SerializedName("ko")
    val ko: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)
