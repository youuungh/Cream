package com.ninezero.cream.ui.category

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.CategoryDetails
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class CategoryDetailAction : MviAction {
    object Fetch : CategoryDetailAction()
    data class ProductClicked(val productId: String) : CategoryDetailAction()
    data class ToggleSave(val product: Product) : CategoryDetailAction()
    data class UpdateSavedIds(val savedIds: Set<String>) : CategoryDetailAction()
    object NavigateToSaved : CategoryDetailAction()
    object ObserveSavedIds : CategoryDetailAction()
}

sealed class CategoryDetailResult : MviResult {
    object Fetching : CategoryDetailResult()
    data class CategoryDetailContent(val categoryDetails: CategoryDetails, val savedIds: Set<String>) : CategoryDetailResult()
    data class Error(val message: String, val categoryId: String, val categoryName: String) : CategoryDetailResult()
    data class SaveToggled(val productId: String, val isSaved: Boolean) : CategoryDetailResult()
}

sealed class CategoryDetailEvent : MviEvent, CategoryDetailResult() {
    data class NavigateToProductDetail(val productId: String) : CategoryDetailEvent()
    object NavigateToSaved : CategoryDetailEvent()
}

sealed class CategoryDetailState : MviViewState {
    object Fetching : CategoryDetailState()
    data class Content(val categoryDetails: CategoryDetails, val savedIds: Set<String>) : CategoryDetailState()
    data class Error(val message: String, val categoryId: String, val categoryName: String) : CategoryDetailState()
}

class CategoryDetailReducer @Inject constructor() : MviStateReducer<CategoryDetailState, CategoryDetailResult> {
    override fun CategoryDetailState.reduce(result: CategoryDetailResult): CategoryDetailState {
        return when (result) {
            is CategoryDetailResult.Fetching -> CategoryDetailState.Fetching
            is CategoryDetailResult.CategoryDetailContent -> CategoryDetailState.Content(result.categoryDetails, result.savedIds)
            is CategoryDetailResult.Error -> CategoryDetailState.Error(result.message, result.categoryId, result.categoryName)
            is CategoryDetailResult.SaveToggled -> {
                if (this is CategoryDetailState.Content) {
                    val updatedSavedIds = if (result.isSaved) {
                        savedIds + result.productId
                    } else {
                        savedIds - result.productId
                    }
                    val updatedProducts = categoryDetails.products.map {
                        if (it.productId == result.productId) it.copy(isSaved = result.isSaved) else it
                    }
                    CategoryDetailState.Content(categoryDetails.copy(products = updatedProducts), updatedSavedIds)
                } else this
            }
            is CategoryDetailEvent -> this
        }
    }
}