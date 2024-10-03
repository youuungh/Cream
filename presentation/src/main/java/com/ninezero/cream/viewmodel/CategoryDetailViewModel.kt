package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.category.CategoryDetailAction
import com.ninezero.cream.ui.category.CategoryDetailEvent
import com.ninezero.cream.ui.category.CategoryDetailReducer
import com.ninezero.cream.ui.category.CategoryDetailResult
import com.ninezero.cream.ui.category.CategoryDetailState
import com.ninezero.cream.ui.navigation.Routes
import com.ninezero.cream.ui.saved.SavedState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.cream.utils.SnackbarUtils.showSnack
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.updateSaveStatus
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.CategoryUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    private val saveUseCase: SaveUseCase,
    reducer: CategoryDetailReducer,
    networkRepository: NetworkRepository,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<CategoryDetailAction, CategoryDetailResult, CategoryDetailEvent, CategoryDetailState, CategoryDetailReducer>(
    initialState = CategoryDetailState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    private val categoryId: String = checkNotNull(savedStateHandle[Routes.CATEGORY_ID_KEY])
    private val categoryName: String = checkNotNull(savedStateHandle[Routes.CATEGORY_NAME_KEY])

    init {
        action(CategoryDetailAction.Fetch)
        action(CategoryDetailAction.ObserveSavedIds)
    }

    override fun CategoryDetailAction.process(): Flow<CategoryDetailResult> = flow {
        when (this@process) {
            is CategoryDetailAction.Fetch -> fetchCategoryDetails()
            is CategoryDetailAction.ProductClicked -> emit(CategoryDetailEvent.NavigateToProductDetail(productId))
            is CategoryDetailAction.ToggleSave -> toggleSave(product)
            is CategoryDetailAction.UpdateSavedIds -> updateSavedIds(savedIds)
            is CategoryDetailAction.NavigateToSaved -> emit(CategoryDetailEvent.NavigateToSaved)
            is CategoryDetailAction.ObserveSavedIds -> observeSavedIds()
        }
    }

    private suspend fun FlowCollector<CategoryDetailResult>.fetchCategoryDetails() {
        emit(CategoryDetailResult.Fetching)
        try {
            handleNetworkCallback { categoryUseCase.getCategoryDetails(categoryId) }.collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val savedIds = (state.value as? CategoryDetailState.Content)?.savedIds ?: emptySet()
                        val updatedProducts = it.entity.products.updateSaveStatus(savedIds)
                        emit(CategoryDetailResult.CategoryDetailContent(it.entity.copy(products = updatedProducts), savedIds))
                    }
                    is EntityWrapper.Fail -> emit(CategoryDetailResult.Error(ErrorHandler.getErrorMessage(it.error), categoryId, categoryName))
                }
            }
        } catch (e: Exception) {
            emit(CategoryDetailResult.Error(ErrorHandler.getErrorMessage(e), categoryId, categoryName))
        }
    }

    private suspend fun FlowCollector<CategoryDetailResult>.toggleSave(product: Product) {
        saveUseCase.toggleSave(product)
        emit(CategoryDetailResult.SaveToggled(product.productId, !product.isSaved))
        if (!product.isSaved) {
            showSnack(
                messageTextId = R.string.saved_item_added,
                actionLabelId = R.string.view_saved,
                onAction = { action(CategoryDetailAction.NavigateToSaved) }
            )
        }
    }

    private suspend fun FlowCollector<CategoryDetailResult>.updateSavedIds(savedIds: Set<String>) {
        val currentState = state.value
        if (currentState is CategoryDetailState.Content) {
            val updatedProducts = currentState.categoryDetails.products.updateSaveStatus(savedIds)
            emit(
                CategoryDetailResult.CategoryDetailContent(
                    currentState.categoryDetails.copy(products = updatedProducts),
                    savedIds
                )
            )
        }
    }

    private fun observeSavedIds() {
        viewModelScope.launch {
            saveUseCase.fetchProductIds().collect { savedIds ->
                action(CategoryDetailAction.UpdateSavedIds(savedIds))
            }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is CategoryDetailState.Error
    override fun refreshData() = action(CategoryDetailAction.Fetch)
}