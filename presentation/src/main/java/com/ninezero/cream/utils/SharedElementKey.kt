package com.ninezero.cream.utils

data class ProductSharedElementKey(
    val productId: String,
    val type: ProductSharedElementType
)

data class CategorySharedElementKey(
    val categoryId: String,
    val categoryName: String,
    val type: CategorySharedElementType
)

enum class ProductSharedElementType {
    Bounds, Image, Title, Background
}

enum class CategorySharedElementType {
    Bounds, Image, Title, Background
}