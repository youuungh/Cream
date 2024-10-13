package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.category_detail.CategoryDetailAction
import com.ninezero.cream.ui.category_detail.CategoryDetailEvent
import com.ninezero.cream.ui.category_detail.CategoryDetailReducer
import com.ninezero.cream.ui.category_detail.CategoryDetailResult
import com.ninezero.cream.ui.category_detail.CategoryDetailState
import com.ninezero.cream.ui.navigation.Routes
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.updateSaveStatus
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.CategoryUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
        observeSavedIds()
    }

    override fun CategoryDetailAction.process(): Flow<CategoryDetailResult> = when (this@process) {
        is CategoryDetailAction.Fetch -> fetchCategoryDetails()
        is CategoryDetailAction.ToggleSave -> toggleSave(product)
        is CategoryDetailAction.UpdateSavedIds -> updateSavedIds(savedIds)
        is CategoryDetailAction.NavigateToSaved -> emitEvent(CategoryDetailEvent.NavigateToSaved)
        is CategoryDetailAction.ProductClicked -> emitEvent(CategoryDetailEvent.NavigateToProductDetail(productId))
    }

    private fun fetchCategoryDetails(): Flow<CategoryDetailResult> = flow {
        emit(CategoryDetailResult.Fetching)
        try {
            handleNetworkCallback { categoryUseCase.getCategoryDetails(categoryId) }.collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val savedIds = saveUseCase.savedProductIds.value
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

    private fun toggleSave(product: Product): Flow<CategoryDetailResult> = flow {
        saveUseCase.toggleSave(product)
        emit(CategoryDetailResult.SaveToggled(product.productId, !product.isSaved))
        if (!product.isSaved) {
            emit(
                CategoryDetailEvent.ShowSnackbar(
                    Message(
                        messageId = R.string.saved_item_added,
                        actionLabelId = R.string.view_saved,
                        onAction = { action(CategoryDetailAction.NavigateToSaved) }
                    )
                )
            )
        }
    }

    private fun updateSavedIds(savedIds: Set<String>): Flow<CategoryDetailResult> = flow {
        val currentState = state.value
        if (currentState is CategoryDetailState.Content) {
            val updatedProducts = currentState.categoryDetails.products.updateSaveStatus(savedIds)
            emit(CategoryDetailResult.CategoryDetailContent(currentState.categoryDetails.copy(products = updatedProducts), savedIds))
        }
    }

    private fun observeSavedIds() {
        viewModelScope.launch {
            saveUseCase.savedProductIds.collect { action(CategoryDetailAction.UpdateSavedIds(it)) }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is CategoryDetailState.Error
    override fun refreshData() = action(CategoryDetailAction.Fetch)
}