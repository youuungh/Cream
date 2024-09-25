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
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    }

    override fun ProductDetailAction.process(): Flow<ProductDetailResult> {
        return when (this) {
            ProductDetailAction.Fetch, ProductDetailAction.Refresh -> fetchProductDetails()
            is ProductDetailAction.FetchRelatedProducts -> fetchRelatedProducts(this.brandId)
            is ProductDetailAction.ToggleSave -> toggleSave()
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
                        val isSaved = saveUseCase.isSaved(product.productId).first()
                        emit(ProductDetailResult.ProductContent(product.copy(isSaved = isSaved)))
                        action(ProductDetailAction.FetchRelatedProducts(product.brand.brandId))
                    }
                    is EntityWrapper.Fail -> emit(ProductDetailResult.Error(it.error.message ?: "Unknown error occurred"))
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
            val product = currentState.product
            saveUseCase.toggleSave(product)
            emit(ProductDetailResult.SaveToggled(!product.isSaved))
        }
    }

    override fun refreshData() = action(ProductDetailAction.Refresh)
}