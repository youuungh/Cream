package com.ninezero.domain.model

data class Category(
    val categoryId: String,
    val categoryName: String,
    val ko: String
)

data class CategoryDetails(
    val category: Category,
    val products: List<Product>
)