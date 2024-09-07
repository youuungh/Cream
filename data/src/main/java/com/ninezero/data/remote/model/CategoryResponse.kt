package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("ko")
    val ko: String
)
