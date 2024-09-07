package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class TopBannerResponse(
    @SerializedName("bannerId")
    val bannerId: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)
