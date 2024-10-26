package com.ninezero.cream.ui.component.bottomsheet

import com.ninezero.cream.utils.SavedSortOption
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.domain.model.Product

enum class BottomSheetType {
    NONE, DETAIL, PAYMENT, PAYMENT_PROGRESS
}

enum class PaymentStatus {
    NONE, PROCESSING, SUCCESS, FAILED
}

sealed interface BottomSheetState {
    data class Detail(
        val productImageUrl: String,
        val productName: String,
        val productKo: String,
        val onAddToCart: () -> Unit,
        val onBuyClick: () -> Unit
    ) : BottomSheetState

    data class SearchSort(
        val selectedOption: SearchSortOption,
        val onOptionSelected: (SearchSortOption) -> Unit
    ) : BottomSheetState

    data class SavedSort(
        val selectedOption: SavedSortOption,
        val onOptionSelected: (SavedSortOption) -> Unit
    ) : BottomSheetState

    data class Payment(
        val products: List<Product>,
        val onPaymentClick: () -> Unit
    ) : BottomSheetState

    data class PaymentProgress(
        val status: PaymentStatus,
        val onNavigateToHome: () -> Unit
    ) : BottomSheetState

    object None : BottomSheetState
}