package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.product_detail.ProductDetailAction
import com.ninezero.cream.ui.product_detail.ProductDetailEvent
import com.ninezero.cream.ui.product_detail.ProductDetailReducer
import com.ninezero.cream.ui.product_detail.ProductDetailResult
import com.ninezero.cream.ui.product_detail.ProductDetailState
import com.ninezero.cream.ui.navigation.Routes
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.updateSaveStatus
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.AuthUseCase
import com.ninezero.domain.usecase.CartUseCase
import com.ninezero.domain.usecase.ProductUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val authUseCase: AuthUseCase,
    private val saveUseCase: SaveUseCase,
    private val cartUseCase: CartUseCase,
    reducer: ProductDetailReducer,
    networkRepository: NetworkRepository,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<ProductDetailAction, ProductDetailResult, ProductDetailEvent, ProductDetailState, ProductDetailReducer>(
    initialState = ProductDetailState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    private val productId: String = checkNotNull(savedStateHandle[Routes.PRODUCT_ID_KEY])

    init {
        action(ProductDetailAction.Fetch)
        observeSavedIds()
    }

    override fun ProductDetailAction.process(): Flow<ProductDetailResult> = when (this@process) {
        is ProductDetailAction.Fetch -> fetchProductDetails()
        is ProductDetailAction.ToggleSave -> toggleSave(product)
        is ProductDetailAction.AddToCart -> addToCart(product)
        is ProductDetailAction.FetchRelatedProducts -> fetchRelatedProducts(brandId)
        is ProductDetailAction.UpdateSavedIds -> updateSavedIds(savedIds)
        is ProductDetailAction.NavigateToSaved -> emitEvent(ProductDetailEvent.NavigateToSaved)
        is ProductDetailAction.NavigateToCart -> emitEvent(ProductDetailEvent.NavigateToCart)
    }

    private fun fetchProductDetails(): Flow<ProductDetailResult> = flow {
        emit(ProductDetailResult.Fetching)
        try {
            handleNetworkCallback { productUseCase.getProductDetails(productId) }.collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val product = it.entity
                        val savedIds = saveUseCase.savedProductIds.value
                        emit(ProductDetailResult.ProductContent(product.copy(isSaved = product.productId in savedIds), savedIds))
                        action(ProductDetailAction.FetchRelatedProducts(product.brand.brandId))
                    }

                    is EntityWrapper.Fail -> emit(
                        ProductDetailResult.Error(ErrorHandler.getErrorMessage(it.error))
                    )
                }
            }
        } catch (e: Exception) {
            emit(ProductDetailResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private fun fetchRelatedProducts(brandId: String): Flow<ProductDetailResult> = flow {
        try {
            handleNetworkCallback { productUseCase.getProductsByBrand(brandId) }.collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val savedIds = saveUseCase.savedProductIds.value
                        val updatedProducts = it.entity.updateSaveStatus(savedIds)
                        emit(ProductDetailResult.RelatedProducts(updatedProducts))
                    }

                    is EntityWrapper.Fail -> emit(
                        ProductDetailResult.Error(ErrorHandler.getErrorMessage(it.error))
                    )
                }
            }
        } catch (e: Exception) {
            emit(ProductDetailResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private fun toggleSave(product: Product): Flow<ProductDetailResult> = flow {
        if (authUseCase.getCurrentUser() != null) {
            saveUseCase.toggleSave(product)
            emit(ProductDetailResult.SaveToggled(product.productId, !product.isSaved))
            if (!product.isSaved) {
                emit(
                    ProductDetailEvent.ShowSnackbar(
                        Message(
                            messageId = R.string.saved_item_added,
                            actionLabelId = R.string.view_saved,
                            onAction = { action(ProductDetailAction.NavigateToSaved) }
                        )
                    )
                )
            }
        } else emit(ProductDetailEvent.NavigateToLogin)
    }

    private fun addToCart(product: Product): Flow<ProductDetailResult> = flow {
        if (authUseCase.getCurrentUser() != null) {
            try {
                val isAlreadyInCart = cartUseCase.isInCart(product.productId).first()
                if (isAlreadyInCart) {
                    emit(ProductDetailEvent.ShowSnackbar(Message(messageId = R.string.already_in_cart)))
                    emit(ProductDetailResult.AlreadyInCart)
                } else {
                    cartUseCase.addToCart(product)
                    emit(
                        ProductDetailEvent.ShowSnackbar(
                            Message(
                                messageId = R.string.added_to_cart,
                                actionLabelId = R.string.view_cart,
                                onAction = { action(ProductDetailAction.NavigateToCart) }
                            )
                        )
                    )
                    emit(ProductDetailResult.AddToCartSuccess)
                }
            } catch (e: Exception) {
                emit(ProductDetailResult.Error(ErrorHandler.getErrorMessage(e)))
            }
        } else emit(ProductDetailEvent.NavigateToLogin)
    }

    private fun updateSavedIds(savedIds: Set<String>): Flow<ProductDetailResult> = flow {
        val currentState = state.value
        if (currentState is ProductDetailState.Content) {
            val updatedProduct =
                currentState.product.copy(isSaved = currentState.product.productId in savedIds)
            val updatedRelatedProducts = currentState.relatedProducts.updateSaveStatus(savedIds)
            emit(ProductDetailResult.ProductContent(updatedProduct, savedIds))
            emit(ProductDetailResult.RelatedProducts(updatedRelatedProducts))
        }
    }

    private fun observeSavedIds() {
        viewModelScope.launch {
            saveUseCase.savedProductIds.collect { action(ProductDetailAction.UpdateSavedIds(it)) }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is ProductDetailState.Error
    override fun refreshData() = action(ProductDetailAction.Fetch)
}