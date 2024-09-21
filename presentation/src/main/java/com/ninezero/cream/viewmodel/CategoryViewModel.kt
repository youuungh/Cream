package com.ninezero.cream.viewmodel

import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.category.CategoryAction
import com.ninezero.cream.ui.category.CategoryEvent
import com.ninezero.cream.ui.category.CategoryReducer
import com.ninezero.cream.ui.category.CategoryResult
import com.ninezero.cream.ui.category.CategoryState
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    reducer: CategoryReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<CategoryAction, CategoryResult, CategoryEvent, CategoryState, CategoryReducer>(
    initialState = CategoryState.Loading,
    reducer = reducer
) {
    init {
        setNetworkRepository(networkRepository)
        action(CategoryAction.Fetch)
    }

    override fun CategoryAction.process(): Flow<CategoryResult> {
        return when (this) {
            CategoryAction.Fetch, CategoryAction.Refresh -> fetchCategories()
            is CategoryAction.CategoryClicked -> flow {
                emit(CategoryEvent.NavigateToCategoryDetail(categoryId, categoryName))
            }
        }
    }

    private fun fetchCategories(): Flow<CategoryResult> = flow {
        emit(CategoryResult.Loading)
        if (!networkState.value) {
            delay(3000)
            emit(CategoryResult.Error("No internet connection"))
        } else {
            categoryUseCase().collect {
                emit(
                    when (it) {
                        is EntityWrapper.Success -> CategoryResult.CategoryContent(it.entity)
                        is EntityWrapper.Fail -> CategoryResult.Error(
                            it.error.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }

    override fun refreshData() { action(CategoryAction.Refresh) }
}