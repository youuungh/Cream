package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.product.ProductDetailAction
import com.ninezero.cream.ui.product.ProductDetailEvent
import com.ninezero.cream.ui.product.ProductDetailReducer
import com.ninezero.cream.ui.product.ProductDetailResult
import com.ninezero.cream.ui.product.ProductDetailState
import com.ninezero.cream.ui.navigation.AppRoutes
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    reducer: ProductDetailReducer,
    networkRepository: NetworkRepository,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<ProductDetailAction, ProductDetailResult, ProductDetailEvent, ProductDetailState, ProductDetailReducer>(
    initialState = ProductDetailState.Loading,
    reducer = reducer
) {
    private val productId: String = checkNotNull(savedStateHandle[AppRoutes.PRODUCT_ID_KEY])

    init {
        setNetworkRepository(networkRepository)
        action(ProductDetailAction.Fetch)
    }

    override fun ProductDetailAction.process(): Flow<ProductDetailResult> {
        return when (this) {
            ProductDetailAction.Fetch, ProductDetailAction.Refresh -> fetchProductDetails()
            is ProductDetailAction.FetchRelatedProducts -> fetchRelatedProducts(this.brandId)
            is ProductDetailAction.ToggleSave -> toggleSave()
        }
    }

    private fun fetchProductDetails(): Flow<ProductDetailResult> = flow {
        emit(ProductDetailResult.Loading)
        if (!networkState.value) {
            delay(3000)
            emit(ProductDetailResult.Error("No internet connection"))
        } else {
            productUseCase.getProductDetails(productId).collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        emit(ProductDetailResult.ProductContent(it.entity))
                        action(ProductDetailAction.FetchRelatedProducts(it.entity.brand.brandId))
                    }
                    is EntityWrapper.Fail -> emit(ProductDetailResult.Error(
                        it.error.message ?: "Unknown error occurred"
                    ))
                }
            }
        }
    }

    private fun fetchRelatedProducts(brandId: String): Flow<ProductDetailResult> = flow {
        productUseCase.getProductsByBrand(brandId).collect {
            emit(
                when(it) {
                    is EntityWrapper.Success -> ProductDetailResult.RelatedProducts(it.entity)
                    is EntityWrapper.Fail -> ProductDetailResult.Error(
                        it.error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    private fun toggleSave(): Flow<ProductDetailResult> = flow {
        val currentState = state.value
        if (currentState is ProductDetailState.Content) {
            val newSavedState = !currentState.product.isSaved
            emit(ProductDetailResult.SaveToggled(newSavedState))
        }
    }

    override fun refreshData() { action(ProductDetailAction.Refresh) }
}