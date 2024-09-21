package com.ninezero.domain.usecase

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    fun getProductDetails(productId: String): Flow<EntityWrapper<Product>> {
        return productRepository.fetchProductDetails(productId)
    }
}