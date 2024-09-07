package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class PriceResponse(
    @SerializedName("releasePrice")
    val releasePrice: String?,
    @SerializedName("instantBuyPrice")
    val instantBuyPrice: Int,
    @SerializedName("status")
    val status: String
)
