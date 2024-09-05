package com.ninezero.domain.model

data class Product(
    val productId: String,
    val productName: String,
    val ko: String,
    val imageUrl: String,
    val price: Price,
    val tradingVolume: Int,
    val releaseDate: String,
    val mainColor: String,
    val category: Category,
    val brand: Brand,
    val isNew: Boolean,
    val isFreeShipping: Boolean,
    val isSaved: Boolean
)
