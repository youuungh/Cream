package com.ninezero.domain.repository

import com.ninezero.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface SaveRepository {
    fun fetchAll(): Flow<List<Product>>
    suspend fun saveProduct(product: Product)
    suspend fun removeFromSaved(productId: String)
    suspend fun removeAll()
    fun isSaved(productId: String): Flow<Boolean>
}