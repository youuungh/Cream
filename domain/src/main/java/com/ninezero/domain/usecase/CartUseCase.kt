package com.ninezero.domain.usecase

import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    fun fetchAll(): Flow<List<Product>> = cartRepository.fetchAll()
        .map { products -> products.sortedByDescending { it.addedToCartAt } }

    suspend fun addToCart(product: Product) {
        if (!isInCart(product.productId).first()) {
            cartRepository.addToCart(product.copy(isSelected = true))
        }
    }

    suspend fun removeFromCart(productId: String) = cartRepository.removeFromCart(productId)

    suspend fun removeSelected(selectedProductIds: List<String>) {
        selectedProductIds.forEach { productId ->
            cartRepository.removeFromCart(productId)
        }
    }

    suspend fun removeAll() = cartRepository.removeAll()

    suspend fun updateSelection(productId: String, isSelected: Boolean) =
        cartRepository.updateSelection(productId, isSelected)

    suspend fun updateAllSelection(isSelected: Boolean) {
        val products = fetchAll().first()
        products.forEach { product ->
            updateSelection(product.productId, isSelected)
        }
    }

    fun isInCart(productId: String): Flow<Boolean> = cartRepository.isInCart(productId)

    fun calculateTotalPrice(products: List<Product>): Int = products.sumOf { it.price.instantBuyPrice }

    fun calculateTotalFee(products: List<Product>): Double = calculateTotalPrice(products) * 0.05
}