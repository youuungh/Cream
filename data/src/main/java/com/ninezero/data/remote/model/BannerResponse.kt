package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("bannerId")
    val bannerId: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)
