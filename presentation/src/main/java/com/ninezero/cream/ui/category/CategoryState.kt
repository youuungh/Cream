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
    data class CategoryClicked(val categoryId: String, val categoryName: String) : CategoryAction()
}

sealed class CategoryResult : MviResult {
    object Fetching : CategoryResult()
    data class CategoryContent(val categories: List<Category>) : CategoryResult()
    data class Error(val message: String) : CategoryResult()
}

sealed class CategoryEvent : MviEvent, CategoryResult() {
    data class NavigateToCategoryDetail(val categoryId: String, val categoryName: String) : CategoryEvent()
}

sealed class CategoryState : MviViewState {
    object Fetching : CategoryState()
    data class Content(val categories: List<Category>) : CategoryState()
    data class Error(val message: String) : CategoryState()
}

class CategoryReducer @Inject constructor() : MviStateReducer<CategoryState, CategoryResult> {
    override fun CategoryState.reduce(result: CategoryResult): CategoryState {
        return when (result) {
            is CategoryResult.Fetching -> CategoryState.Fetching
            is CategoryResult.CategoryContent -> CategoryState.Content(result.categories)
            is CategoryResult.Error -> CategoryState.Error(result.message)
            is CategoryEvent -> this
        }
    }
}
