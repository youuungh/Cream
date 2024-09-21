package com.ninezero.domain.repository

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun fetchProductDetails(productId: String): Flow<EntityWrapper<Product>>
}