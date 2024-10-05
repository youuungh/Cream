package com.ninezero.domain.usecase

import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.SaveRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveUseCase @Inject constructor(
    private val saveRepository: SaveRepository,
    coroutineScope: CoroutineScope
) {
    private val _savedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val savedProductIds: StateFlow<Set<String>> = _savedProductIds

    init {
        coroutineScope.launch {
            fetchProductIds().collect { _savedProductIds.value = it }
        }
    }

    suspend fun toggleSave(product: Product) {
        if (isSaved(product.productId).first()) {
            saveRepository.removeFromSaved(product.productId)
        } else {
            saveRepository.saveProduct(product)
        }
    }

    fun isSaved(productId: String): Flow<Boolean> = savedProductIds.map { it.contains(productId) }

    suspend fun removeAll() {
        saveRepository.removeAll()
        _savedProductIds.value = emptySet()
    }

    fun fetchAll(): Flow<List<Product>> = saveRepository.fetchAll()

    private fun fetchProductIds(): Flow<Set<String>> = saveRepository.fetchAll()
        .map { products -> products.mapTo(HashSet()) { it.productId }
    }
}