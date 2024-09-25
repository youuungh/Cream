package com.ninezero.domain.usecase

import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.SaveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SaveUseCase @Inject constructor(
    private val saveRepository: SaveRepository
) {
    fun fetchAll(): Flow<List<Product>> = saveRepository.fetchAll()

    suspend fun toggleSave(product: Product) {
        if (saveRepository.isSaved(product.productId).first()) {
            saveRepository.removeSavedProduct(product.productId)
        } else {
            saveRepository.saveProduct(product)
        }
    }

    fun isSaved(productId: String): Flow<Boolean> = saveRepository.isSaved(productId)

    suspend fun removeAll() = saveRepository.removeAll()

    fun fetchProductIds(): Flow<Set<String>> = saveRepository.fetchAll().map { products ->
        products.mapTo(HashSet()) { it.productId }
    }
}