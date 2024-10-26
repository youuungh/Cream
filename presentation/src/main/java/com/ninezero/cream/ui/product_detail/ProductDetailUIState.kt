package com.ninezero.cream.ui.product_detail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.ninezero.cream.ui.component.DetailTabState
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetType
import com.ninezero.cream.ui.component.bottomsheet.PaymentStatus
import com.ninezero.domain.model.Product

data class ProductDetailUIState(
    val product: Product,
    val relatedProducts: List<Product>,
    val isSaved: Boolean,
    val appBarHeight: Dp,
    val tabVisible: Boolean
)

data class BottomSheetUIState(
    val isVisible: Boolean,
    val type: BottomSheetType,
    val paymentStatus: PaymentStatus
)

data class ProductDetailHandlers(
    val onProductClick: (String) -> Unit,
    val onNavigateToHome: () -> Unit,
    val onSaveToggle: (Product) -> Unit,
    val onAddToCart: () -> Unit,
    val onBuyClick: () -> Unit,
    val onProcessPayment: (Product) -> Unit
)

data class BottomSheetHandlers(
    val onTypeChange: (BottomSheetType) -> Unit,
    val onPaymentStatusChange: (PaymentStatus) -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun rememberTabStateHolder(
    density: Density = LocalDensity.current,
    lazyListState: LazyListState,
    appBarHeight: Dp,
) : DetailTabState {
    return remember(density, lazyListState) {
        DetailTabState(density, lazyListState, appBarHeight)
    }
}