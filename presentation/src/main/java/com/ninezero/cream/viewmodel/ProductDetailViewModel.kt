package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.product.ProductDetailAction
import com.ninezero.cream.ui.product.ProductDetailEvent
import com.ninezero.cream.ui.product.ProductDetailReducer
import com.ninezero.cream.ui.product.ProductDetailResult
import com.ninezero.cream.ui.product.ProductDetailState
import com.ninezero.cream.ui.navigation.Routes
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.cream.utils.SnackbarUtils.showSnack
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.ProductUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
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
    private val productId: String = checkNotNull(savedStateHandle[Routes.PRODUCT_ID_KEY])

    init {
        setNetworkRepository(networkRepository)
        action(ProductDetailAction.Fetch)
        viewModelScope.launch {
            saveUseCase.fetchProductIds().collect { savedIds ->
                action(ProductDetailAction.UpdateSavedIds(savedIds))
            }
        }
    }

    override fun ProductDetailAction.process(): Flow<ProductDetailResult> = flow {
        when (this@process) {
            ProductDetailAction.Fetch -> fetchProductDetails()
            is ProductDetailAction.FetchRelatedProducts -> fetchRelatedProducts(brandId)
            is ProductDetailAction.ToggleSave -> toggleSave(product)
            is ProductDetailAction.UpdateSavedIds -> updateSavedIds(savedIds)
            is ProductDetailAction.NavigateToSaved -> emit(ProductDetailEvent.NavigateToSaved)
        }
    }

    private suspend fun FlowCollector<ProductDetailResult>.fetchProductDetails() {
        emit(ProductDetailResult.Fetching)
        handleNetworkCallback { productUseCase.getProductDetails(productId) }.collect {
            when (it) {
                is EntityWrapper.Success -> {
                    val product = it.entity
                    val savedIds = saveUseCase.fetchProductIds().first()
                    emit(ProductDetailResult.ProductContent(product.copy(isSaved = product.productId in savedIds), savedIds))
                    action(ProductDetailAction.FetchRelatedProducts(product.brand.brandId))
                }
                is EntityWrapper.Fail -> emit(
                    ProductDetailResult.Error(ErrorHandler.getErrorMessage(it.error))
                )
            }
        }
    }

    private suspend fun FlowCollector<ProductDetailResult>.fetchRelatedProducts(brandId: String) {
        handleNetworkCallback { productUseCase.getProductsByBrand(brandId) }.collect {
            when (it) {
                is EntityWrapper.Success -> {
                    val savedIds = saveUseCase.fetchProductIds().first()
                    val updatedProducts = updateSaveStatus(it.entity, savedIds)
                    emit(ProductDetailResult.RelatedProducts(updatedProducts))
                }
                is EntityWrapper.Fail -> emit(
                    ProductDetailResult.Error(ErrorHandler.getErrorMessage(it.error))
                )
            }
        }
    }

    private suspend fun FlowCollector<ProductDetailResult>.toggleSave(product: Product) {
        saveUseCase.toggleSave(product)
        emit(ProductDetailResult.SaveToggled(product.productId, !product.isSaved))
        if (!product.isSaved) {
            showSnack(
                messageTextId = R.string.saved_item_added,
                actionLabelId = R.string.view_saved,
                onAction = { action(ProductDetailAction.NavigateToSaved) }
            )
        }
    }

    private suspend fun FlowCollector<ProductDetailResult>.updateSavedIds(savedIds: Set<String>) {
        val currentState = state.value
        if (currentState is ProductDetailState.Content) {
            val updatedProduct =
                currentState.product.copy(isSaved = currentState.product.productId in savedIds)
            val updatedRelatedProducts = updateSaveStatus(currentState.relatedProducts, savedIds)
            emit(ProductDetailResult.ProductContent(updatedProduct, savedIds))
            emit(ProductDetailResult.RelatedProducts(updatedRelatedProducts))
        }
    }

    private fun updateSaveStatus(products: List<Product>, savedIds: Set<String>): List<Product> {
        return products.map { it.copy(isSaved = it.productId in savedIds) }
    }

    override fun refreshData() = action(ProductDetailAction.Fetch)
}