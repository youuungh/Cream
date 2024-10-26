package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope

@Composable
fun CreamBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    state: BottomSheetState,
    onDismiss: () -> Unit,
    coroutineScope: CoroutineScope,
    skipPartiallyExpanded: Boolean = true
) {
    BaseBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismiss = onDismiss,
        coroutineScope = coroutineScope,
        skipPartiallyExpanded = skipPartiallyExpanded
    ) {
        when (state) {
            is BottomSheetState.Detail -> DetailBottomSheetContent(
                onDismiss = onDismiss,
                productImageUrl = state.productImageUrl,
                productName = state.productName,
                productKo = state.productKo,
                onAddToCart = state.onAddToCart,
                onBuyClick = state.onBuyClick
            )
            is BottomSheetState.Payment -> PaymentBottomSheetContent(
                onDismiss = onDismiss,
                products = state.products,
                onPaymentClick = state.onPaymentClick
            )
            is BottomSheetState.SearchSort -> SearchSortBottomSheetContent(
                selectedOption = state.selectedOption,
                onOptionSelected = state.onOptionSelected,
                onDismiss = onDismiss
            )
            is BottomSheetState.SavedSort -> SavedSortBottomSheetContent(
                selectedOption = state.selectedOption,
                onOptionSelected = state.onOptionSelected,
                onDismiss = onDismiss
            )
            else -> {}
        }
    }
}