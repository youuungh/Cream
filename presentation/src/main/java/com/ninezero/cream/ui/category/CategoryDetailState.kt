package com.ninezero.cream.ui.category

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.CategoryDetails
import javax.inject.Inject

sealed class CategoryDetailAction : MviAction {
    data class Fetch(val categoryId: String) : CategoryDetailAction()
}

sealed class CategoryDetailResult : MviResult {
    object Loading : CategoryDetailResult()
    data class CategoryDetailContent(val categoryDetails: CategoryDetails) : CategoryDetailResult()
    data class Error(val message: String, val categoryId: String) : CategoryDetailResult()
}

sealed class CategoryDetailEvent : MviEvent, CategoryDetailResult() {
    // productClick
}

sealed class CategoryDetailState : MviViewState {
    object Loading : CategoryDetailState()
    data class Content(val categoryDetails: CategoryDetails) : CategoryDetailState()
    data class Error(val message: String, val categoryId: String) : CategoryDetailState()
}

class CategoryDetailReducer @Inject constructor() :
    MviStateReducer<CategoryDetailState, CategoryDetailResult> {
    override fun CategoryDetailState.reduce(result: CategoryDetailResult): CategoryDetailState {
        return when (result) {
            is CategoryDetailResult.Loading -> CategoryDetailState.Loading
            is CategoryDetailResult.CategoryDetailContent -> CategoryDetailState.Content(result.categoryDetails)
            is CategoryDetailResult.Error -> CategoryDetailState.Error(result.message, result.categoryId)
        }
    }
}