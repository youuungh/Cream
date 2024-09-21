package com.ninezero.cream.utils

data class CategorySharedElementKey(
    val categoryId: String,
    val categoryName: String,
    val type: CategorySharedElementType
)

enum class CategorySharedElementType {
    Bounds, Image, Title, Background
}