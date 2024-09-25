package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.category.CategoryDetailAction
import com.ninezero.cream.ui.category.CategoryDetailEvent
import com.ninezero.cream.ui.category.CategoryDetailReducer
import com.ninezero.cream.ui.category.CategoryDetailResult
import com.ninezero.cream.ui.category.CategoryDetailState
import com.ninezero.cream.ui.navigation.AppRoutes
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.CategoryUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    initialState = CategoryDetailState.Loading,
    reducer = reducer
) {
    private val categoryId: String = checkNotNull(savedStateHandle[AppRoutes.CATEGORY_ID_KEY])
    private val categoryName: String = checkNotNull(savedStateHandle[AppRoutes.CATEGORY_NAME_KEY])

    init {
        setNetworkStatus(networkRepository)
        action(CategoryDetailAction.Fetch)
        viewModelScope.launch {
            saveUseCase.fetchProductIds().collect { savedIds ->
                action(CategoryDetailAction.UpdateSavedIds(savedIds))
            }
        }
    }

    override fun CategoryDetailAction.process(): Flow<CategoryDetailResult> {
        return when (this) {
            CategoryDetailAction.Fetch, CategoryDetailAction.Refresh -> fetchCategoryDetails()
            is CategoryDetailAction.ProductClicked -> flow {
                emit(CategoryDetailEvent.NavigateToProductDetail(productId))
            }
            is CategoryDetailAction.ToggleSave -> toggleSave(product)
            is CategoryDetailAction.UpdateSavedIds -> updateSavedIds(savedIds)
        }
    }

    private fun fetchCategoryDetails(): Flow<CategoryDetailResult> = flow {
        emit(CategoryDetailResult.Loading)
        if (!networkState.value) {
            delay(3000)
            emit(CategoryDetailResult.Error("No internet connection", categoryId, categoryName))
        } else {
            categoryUseCase.getCategoryDetails(categoryId).collect {
                emit(
                    when (it) {
                        is EntityWrapper.Success -> {
                            val savedIds = (state.value as? CategoryDetailState.Content)?.savedIds ?: emptySet()
                            val updatedProducts = updateSaveStatus(it.entity.products, savedIds)
                            CategoryDetailResult.CategoryDetailContent(it.entity.copy(products = updatedProducts), savedIds)
                        }
                        is EntityWrapper.Fail -> CategoryDetailResult.Error(
                            it.error.message ?: "Unknown error occurred",
                            categoryId,
                            categoryName
                        )
                    }
                )
            }
        }
    }

    private fun toggleSave(product: Product): Flow<CategoryDetailResult> = flow {
        saveUseCase.toggleSave(product)
        emit(CategoryDetailResult.SaveToggled(product.productId, !product.isSaved))
    }

    private fun updateSavedIds(savedIds: Set<String>): Flow<CategoryDetailResult> = flow {
        val currentState = state.value
        if (currentState is CategoryDetailState.Content) {
            val updatedProducts = updateSaveStatus(currentState.categoryDetails.products, savedIds)
            emit(CategoryDetailResult.CategoryDetailContent(currentState.categoryDetails.copy(products = updatedProducts), savedIds))
        }
    }

    private fun updateSaveStatus(products: List<Product>, savedIds: Set<String>): List<Product> {
        return products.map { it.copy(isSaved = it.productId in savedIds) }
    }

    override fun refreshData() { action(CategoryDetailAction.Refresh) }
}