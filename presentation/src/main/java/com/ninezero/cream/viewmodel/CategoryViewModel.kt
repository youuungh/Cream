package com.ninezero.cream.viewmodel

import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.category.CategoryAction
import com.ninezero.cream.ui.category.CategoryEvent
import com.ninezero.cream.ui.category.CategoryReducer
import com.ninezero.cream.ui.category.CategoryResult
import com.ninezero.cream.ui.category.CategoryState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    reducer: CategoryReducer,
    networkRepository: NetworkRepository,
) : BaseStateViewModel<CategoryAction, CategoryResult, CategoryEvent, CategoryState, CategoryReducer>(
    initialState = CategoryState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    init {
        action(CategoryAction.Fetch)
    }

    override fun CategoryAction.process(): Flow<CategoryResult> = when (this@process) {
        is CategoryAction.Fetch -> fetchCategories()
        is CategoryAction.CategoryClicked -> flow {
            emit(CategoryEvent.NavigateToCategoryDetail(categoryId, categoryName))
        }
    }

    private fun fetchCategories(): Flow<CategoryResult> = flow {
        emit(CategoryResult.Fetching)
        try {
            handleNetworkCallback { categoryUseCase() }.collect {
                emit(
                    when (it) {
                        is EntityWrapper.Success -> CategoryResult.CategoryContent(it.entity)
                        is EntityWrapper.Fail -> CategoryResult.Error(
                            ErrorHandler.getErrorMessage(
                                it.error
                            )
                        )
                    }
                )
            }
        } catch (e: Exception) {
            emit(CategoryResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is CategoryState.Error
    override fun refreshData() = action(CategoryAction.Fetch)
}