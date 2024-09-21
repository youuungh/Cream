package com.ninezero.cream.ui.category

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.CategoryDetails
import javax.inject.Inject

sealed class CategoryDetailAction : MviAction {
    data object Fetch : CategoryDetailAction()
    data object Refresh : CategoryDetailAction()
    data class ProductClicked(val productId: String) : CategoryDetailAction()
}

sealed class CategoryDetailResult : MviResult {
    data object Loading : CategoryDetailResult()
    data class CategoryDetailContent(val categoryDetails: CategoryDetails) : CategoryDetailResult()
    data class Error(val message: String, val categoryId: String, val categoryName: String) : CategoryDetailResult()
}

sealed class CategoryDetailEvent : MviEvent, CategoryDetailResult() {
    data class NavigateToProductDetail(val productId: String) : CategoryDetailEvent()
}

sealed class CategoryDetailState : MviViewState {
    data object Loading : CategoryDetailState()
    data class Content(val categoryDetails: CategoryDetails) : CategoryDetailState()
    data class Error(val message: String, val categoryId: String, val categoryName: String) : CategoryDetailState()
}

class CategoryDetailReducer @Inject constructor() : MviStateReducer<CategoryDetailState, CategoryDetailResult> {
    override fun CategoryDetailState.reduce(result: CategoryDetailResult): CategoryDetailState {
        return when (result) {
            is CategoryDetailResult.Loading -> CategoryDetailState.Loading
            is CategoryDetailResult.CategoryDetailContent -> CategoryDetailState.Content(result.categoryDetails)
            is CategoryDetailResult.Error -> CategoryDetailState.Error(
                result.message,
                result.categoryId,
                result.categoryName
            )
            is CategoryDetailEvent.NavigateToProductDetail -> this
        }
    }
}