package com.ninezero.cream.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.category.CategoryDetailAction
import com.ninezero.cream.ui.category.CategoryDetailEvent
import com.ninezero.cream.ui.category.CategoryDetailReducer
import com.ninezero.cream.ui.category.CategoryDetailResult
import com.ninezero.cream.ui.category.CategoryDetailState
import com.ninezero.cream.ui.navigation.AppRoutes
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.usecase.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    reducer: CategoryDetailReducer,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<CategoryDetailAction, CategoryDetailResult, CategoryDetailEvent, CategoryDetailState, CategoryDetailReducer>(
    initialState = CategoryDetailState.Loading,
    reducer = reducer
) {
    private val categoryId: String = checkNotNull(savedStateHandle[AppRoutes.CATEGORY_ID_KEY])
    private val categoryName: String = checkNotNull(savedStateHandle[AppRoutes.CATEGORY_NAME_KEY])

    init {
        action(CategoryDetailAction.Fetch)
    }

    override fun CategoryDetailAction.process(): Flow<CategoryDetailResult> {
        return when (this) {
            is CategoryDetailAction.Fetch -> fetchCategoryDetails()
            is CategoryDetailAction.ProductClicked -> flow {
                emit(CategoryDetailEvent.NavigateToProductDetail(productId))
            }
        }
    }

    private fun fetchCategoryDetails(): Flow<CategoryDetailResult> = flow {
        emit(CategoryDetailResult.Loading)
        categoryUseCase.getCategoryDetails(categoryId).collect {
            emit(
                when (it) {
                    is EntityWrapper.Success -> CategoryDetailResult.CategoryDetailContent(it.entity)
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