package com.ninezero.cream.ui.component.bottomsheet

import com.ninezero.cream.utils.SavedSortOption
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.domain.model.Product

enum class BottomSheetType {
    None, Detail, Payment
}

sealed interface BottomSheetState {
    data class Detail(
        val productImageUrl: String,
        val productName: String,
        val productKo: String,
        val onAddToCart: () -> Unit,
        val onBuyNow: () -> Unit
    ) : BottomSheetState

    data class Payment(
        val products: List<Product>,
        val onPaymentClick: () -> Unit
    ) : BottomSheetState

    data class SearchSort(
        val selectedOption: SearchSortOption,
        val onOptionSelected: (SearchSortOption) -> Unit
    ) : BottomSheetState

    data class SavedSort(
        val selectedOption: SavedSortOption,
        val onOptionSelected: (SavedSortOption) -> Unit
    ) : BottomSheetState

    object None : BottomSheetState
}

data class DetailBottomSheetState(
    val isVisible: Boolean = false,
    val type: BottomSheetType = BottomSheetType.None
)