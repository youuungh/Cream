package com.ninezero.cream.ui.category

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.Category
import javax.inject.Inject

sealed class CategoryAction : MviAction {
    object Fetch : CategoryAction()
    object Refresh : CategoryAction()
    data class CategoryClicked(val categoryId: String) : CategoryAction()
}

sealed class CategoryResult : MviResult {
    object Loading : CategoryResult()
    data class CategoryContent(val categories: List<Category>) : CategoryResult()
    data class Error(val message: String) : CategoryResult()
}

sealed class CategoryEvent : MviEvent, CategoryResult() {
    data class NavigateToCategoryDetail(val categoryId: String) : CategoryEvent()
}

sealed class CategoryState : MviViewState {
    object Loading : CategoryState()
    data class Content(val categories: List<Category>) : CategoryState()
    data class Error(val message: String) : CategoryState()
}

class CategoryReducer @Inject constructor() : MviStateReducer<CategoryState, CategoryResult> {
    override fun CategoryState.reduce(result: CategoryResult): CategoryState {
        return when (result) {
            is CategoryResult.Loading -> CategoryState.Loading
            is CategoryResult.CategoryContent -> CategoryState.Content(result.categories)
            is CategoryResult.Error -> CategoryState.Error(result.message)
            is CategoryEvent -> this
        }
    }
}
