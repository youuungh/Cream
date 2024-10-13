package com.ninezero.domain.usecase

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.SearchHistory
import com.ninezero.domain.repository.ProductRepository
import com.ninezero.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val searchRepository: SearchRepository
) {
    fun searchProducts(keyword: String): Flow<EntityWrapper<List<Product>>> =
        productRepository.searchProducts(keyword)

    fun getSearchHistory(): Flow<List<SearchHistory>> = searchRepository.getSearchHistory()

    suspend fun addSearchHistory(keyword: String) = searchRepository.addSearchHistory(keyword)

    suspend fun removeSearchHistory(keyword: String) = searchRepository.removeSearchHistory(keyword)

    suspend fun clearSearchHistory() = searchRepository.clearSearchHistory()

    suspend fun hideSearchHistory() = searchRepository.hideSearchHistory()

    fun isSearchHistoryHidden(): Flow<Boolean> = searchRepository.isSearchHistoryHidden()

    fun getSuggestedKeywords(keyword: String): Flow<List<String>> = flow {
        val products = productRepository.getAllProducts().first()
        val history = searchRepository.getSearchHistory().first()

        val suggestions = (products.flatMap {
            listOf(
                it.brand.brandName,
                it.productName,
                it.ko
            )
        } + history.map { it.keyword })
            .distinct()
            .filter { it.contains(keyword, ignoreCase = true) }

        emit(suggestions)
    }
}