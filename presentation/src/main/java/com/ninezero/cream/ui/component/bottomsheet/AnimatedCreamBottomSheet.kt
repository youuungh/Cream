package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope

@Composable
fun AnimatedCreamBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    state: BottomSheetState,
    onDismiss: () -> Unit,
    coroutineScope: CoroutineScope
) {
    BaseBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismiss = onDismiss,
        coroutineScope = coroutineScope,
        skipPartiallyExpanded = true
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                val spring = spring<IntOffset>(stiffness = Spring.StiffnessMedium)
                val tween = tween<Float>(durationMillis = 300)
                slideInHorizontally(spring) { width -> width } + fadeIn(tween) togetherWith
                        slideOutHorizontally(spring) { width -> -width } + fadeOut(tween)
            },
            label = "bottom_sheet"
        ) { targetState ->
            when (targetState) {
                is BottomSheetState.Detail -> DetailBottomSheetContent(
                    onDismiss = onDismiss,
                    productImageUrl = targetState.productImageUrl,
                    productName = targetState.productName,
                    productKo = targetState.productKo,
                    onAddToCart = targetState.onAddToCart,
                    onBuyNow = targetState.onBuyNow
                )
                is BottomSheetState.Payment -> PaymentBottomSheetContent(
                    onDismiss = onDismiss,
                    products = targetState.products,
                    onPaymentClick = targetState.onPaymentClick
                )
                else -> {}
            }
        }
    }
}