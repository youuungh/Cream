package com.ninezero.cream.ui.saved

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class SavedAction : MviAction {
    data object Fetch : SavedAction()
    data object Refresh : SavedAction()
    data class Remove(val product: Product) : SavedAction()
    data object RemoveAll : SavedAction()
    data object SortBySavedDate : SavedAction()
    data object SortByPrice : SavedAction()
    data class UpdateProducts(val products: List<Product>) : SavedAction()
}

sealed class SavedResult : MviResult {
    data object Fetching : SavedResult()
    data class FetchSuccess(val products: List<Product>) : SavedResult()
    data class Remove(val productId: String) : SavedResult()
    data object RemoveAll : SavedResult()
    data class Sorted(val sortedProducts: List<Product>) : SavedResult()
    data class Error(val message: String) : SavedResult()
}

sealed class SavedEvent : MviEvent, SavedResult() {
    data class NavigateToProductDetail(val productId: String) : SavedEvent()
}

sealed class SavedState : MviViewState {
    data object Fetching : SavedState()
    data class Content(val savedProducts: List<Product>) : SavedState()
    data class Error(val message: String) : SavedState()
}

class SavedReducer @Inject constructor() : MviStateReducer<SavedState, SavedResult> {
    override fun SavedState.reduce(result: SavedResult): SavedState = when (result) {
        is SavedResult.Fetching -> SavedState.Fetching
        is SavedResult.FetchSuccess -> SavedState.Content(result.products)
        is SavedResult.Remove -> {
            if (this is SavedState.Content) {
                SavedState.Content(savedProducts.filter { it.productId != result.productId })
            } else this
        }
        is SavedResult.RemoveAll -> SavedState.Content(emptyList())
        is SavedResult.Sorted -> {
            if (this is SavedState.Content) {
                SavedState.Content(result.sortedProducts)
            } else this
        }
        is SavedResult.Error -> SavedState.Error(result.message)
        is SavedEvent.NavigateToProductDetail -> this
    }
}