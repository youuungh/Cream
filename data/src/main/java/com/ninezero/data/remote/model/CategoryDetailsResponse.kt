package com.ninezero.data.remote.model

import com.google.gson.annotations.SerializedName

data class CategoryDetailsResponse(
    @SerializedName("category")
    val category: CategoryResponse,
    @SerializedName("products")
    val products: List<ProductResponse>
)