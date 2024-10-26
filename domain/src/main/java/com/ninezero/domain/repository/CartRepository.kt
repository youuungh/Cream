package com.ninezero.domain.repository

import com.ninezero.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun fetchAll(): Flow<List<Product>>
    suspend fun addToCart(product: Product)
    suspend fun removeFromCart(productId: String)
    suspend fun removeAll()
    suspend fun updateSelection(productId: String, isSelected: Boolean)
    suspend fun updateAllSelection(isSelected: Boolean)
    fun isInCart(productId: String): Flow<Boolean>
}