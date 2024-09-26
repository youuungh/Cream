package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.product.ProductDetailAction
import com.ninezero.cream.ui.product.ProductDetailEvent
import com.ninezero.cream.ui.product.ProductDetailReducer
import com.ninezero.cream.ui.product.ProductDetailResult
import com.ninezero.cream.ui.product.ProductDetailState
import com.ninezero.cream.ui.navigation.AppRoutes
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.ProductUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val saveUseCase: SaveUseCase,
    reducer: ProductDetailReducer,
    networkRepository: NetworkRepository,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<ProductDetailAction, ProductDetailResult, ProductDetailEvent, ProductDetailState, ProductDetailReducer>(
    initialState = ProductDetailState.Fetching,
    reducer = reducer
) {
    private val productId: String = checkNotNull(savedStateHandle[AppRoutes.PRODUCT_ID_KEY])

    init {
        setNetworkStatus(networkRepository)
        action(ProductDetailAction.Fetch)
        viewModelScope.launch {
            saveUseCase.fetchProductIds().collect { savedIds ->
                action(ProductDetailAction.UpdateSavedIds(savedIds))
            }
        }
    }

    override fun ProductDetailAction.process(): Flow<ProductDetailResult> {
        return when (this) {
            ProductDetailAction.Fetch, ProductDetailAction.Refresh -> fetchProductDetails()
            is ProductDetailAction.FetchRelatedProducts -> fetchRelatedProducts(this.brandId)
            is ProductDetailAction.ToggleSave -> toggleSave(this.product)
            is ProductDetailAction.UpdateSavedIds -> updateSavedIds(this.savedIds)
        }
    }

    private fun fetchProductDetails(): Flow<ProductDetailResult> = flow {
        emit(ProductDetailResult.Fetching)
        if (!networkState.value) {
            delay(3000)
            emit(ProductDetailResult.Error("No internet connection"))
        } else {
            productUseCase.getProductDetails(productId).collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val product = it.entity
                        val savedIds = saveUseCase.fetchProductIds().first()
                        emit(
                            ProductDetailResult.ProductContent(
                                product.copy(isSaved = product.productId in savedIds),
                                savedIds
                            )
                        )
                        action(ProductDetailAction.FetchRelatedProducts(product.brand.brandId))
                    }

                    is EntityWrapper.Fail -> emit(
                        ProductDetailResult.Error(
                            it.error.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        }
    }

    private fun fetchRelatedProducts(brandId: String): Flow<ProductDetailResult> = flow {
        productUseCase.getProductsByBrand(brandId).collect {
            when (it) {
                is EntityWrapper.Success -> {
                    val savedIds = saveUseCase.fetchProductIds().first()
                    val updatedProducts = updateSaveStatus(it.entity, savedIds)
                    emit(ProductDetailResult.RelatedProducts(updatedProducts))
                }
                is EntityWrapper.Fail -> emit(ProductDetailResult.Error(it.error.message ?: "Unknown error occurred"))
            }
        }
    }

    private fun toggleSave(product: Product): Flow<ProductDetailResult> = flow {
        saveUseCase.toggleSave(product)
        emit(ProductDetailResult.SaveToggled(product.productId, !product.isSaved))
    }

    private fun updateSavedIds(savedIds: Set<String>): Flow<ProductDetailResult> = flow {
        val currentState = state.value
        if (currentState is ProductDetailState.Content) {
            val updatedProduct = currentState.product.copy(isSaved = currentState.product.productId in savedIds)
            val updatedRelatedProducts = updateSaveStatus(currentState.relatedProducts, savedIds)
            emit(ProductDetailResult.ProductContent(updatedProduct, savedIds))
            emit(ProductDetailResult.RelatedProducts(updatedRelatedProducts))
        }
    }

    private fun updateSaveStatus(products: List<Product>, savedIds: Set<String>): List<Product> {
        return products.map { it.copy(isSaved = it.productId in savedIds) }
    }

    override fun refreshData() = action(ProductDetailAction.Refresh)
}