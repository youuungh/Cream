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
    val isSaved: Boolean = false,
    val isInCart: Boolean = false,
    val isSelected: Boolean = false,
    val savedAt: Long? = null,
    val addedToCartAt: Long? = null
)

fun List<Product>.updateSaveStatus(saveIds: Set<String>): List<Product> {
    return map { it.copy(isSaved = it.productId in saveIds) }
}

fun List<Product>.updateCartStatus(cartIds: Set<String>): List<Product> {
    return map { it.copy(isInCart = it.productId in cartIds) }
}