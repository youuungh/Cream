package com.ninezero.cream.ui.product_detail

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetType
import com.ninezero.cream.ui.component.bottomsheet.PaymentStatus
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class ProductDetailAction : MviAction {
    object Fetch : ProductDetailAction()
    object NavigateToSaved : ProductDetailAction()
    object NavigateToCart : ProductDetailAction()
    data class ToggleSave(val product: Product) : ProductDetailAction()
    data class AddToCart(val product: Product) : ProductDetailAction()
    data class FetchRelatedProducts(val brandId: String) : ProductDetailAction()
    data class ProcessPayment(val product: Product) : ProductDetailAction()
    data class UpdateSavedIds(val savedIds: Set<String>) : ProductDetailAction()
    data class ShowBottomSheet(
        val visible: Boolean = true,
        val type: BottomSheetType = BottomSheetType.DETAIL,
        val status: PaymentStatus = PaymentStatus.NONE
    ) : ProductDetailAction()
}

sealed class ProductDetailResult : MviResult {
    object Fetching : ProductDetailResult()
    object AddToCartSuccess : ProductDetailResult()
    object AlreadyInCart : ProductDetailResult()
    data class ProductContent(
        val product: Product,
        val savedIds: Set<String>
    ) : ProductDetailResult()
    data class SaveToggled(val productId: String, val isSaved: Boolean) : ProductDetailResult()
    data class RelatedProducts(val relatedProducts: List<Product>) : ProductDetailResult()
    data class PaymentResult(val status: PaymentStatus) : ProductDetailResult()
    data class Error(val message: String) : ProductDetailResult()
}

sealed class ProductDetailEvent : MviEvent, ProductDetailResult() {
    object NavigateToLogin : ProductDetailEvent()
    object NavigateToSaved : ProductDetailEvent()
    object NavigateToCart : ProductDetailEvent()
    object NavigateToHome : ProductDetailEvent()
    object PaymentCompleted : ProductDetailEvent()
    object PaymentFailed : ProductDetailEvent()
    data class UpdateBottomSheet(
        val visible: Boolean,
        val type: BottomSheetType,
        val status: PaymentStatus
    ) : ProductDetailEvent()
    data class ShowSnackbar(val message: Message) : ProductDetailEvent()
}

sealed class ProductDetailState : MviViewState {
    object Fetching : ProductDetailState()
    data class Content(
        val product: Product,
        val relatedProducts: List<Product> = emptyList(),
        val savedIds: Set<String> = emptySet(),
        val paymentStatus: PaymentStatus? = null,
        var appBarAlpha: Float = 0f
    ) : ProductDetailState()
    data class Error(val message: String) : ProductDetailState()
}

class ProductDetailReducer @Inject constructor() : MviStateReducer<ProductDetailState, ProductDetailResult> {
    override fun ProductDetailState.reduce(result: ProductDetailResult): ProductDetailState {
        return when (result) {
            is ProductDetailResult.Fetching -> ProductDetailState.Fetching
            is ProductDetailResult.ProductContent -> {
                if (this is ProductDetailState.Content) {
                    this.copy(product = result.product, savedIds = result.savedIds)
                } else {
                    ProductDetailState.Content(product = result.product, savedIds = result.savedIds)
                }
            }
            is ProductDetailResult.RelatedProducts -> {
                if (this is ProductDetailState.Content) {
                    this.copy(relatedProducts = result.relatedProducts)
                } else this
            }
            is ProductDetailResult.PaymentResult -> {
                if (this is ProductDetailState.Content) {
                    this.copy(paymentStatus = result.status)
                } else this
            }
            is ProductDetailResult.Error -> ProductDetailState.Error(result.message)
            is ProductDetailResult.SaveToggled -> {
                if (this is ProductDetailState.Content) {
                    val updatedSavedIds = if (result.isSaved) {
                        savedIds + result.productId
                    } else {
                        savedIds - result.productId
                    }
                    val updatedProduct = product.copy(isSaved = result.isSaved)
                    val updatedRelatedProducts = relatedProducts.map {
                        if (it.productId == result.productId) it.copy(isSaved = result.isSaved) else it
                    }
                    this.copy(
                        product = updatedProduct,
                        relatedProducts = updatedRelatedProducts,
                        savedIds = updatedSavedIds
                    )
                } else this
            }
            is ProductDetailResult.AddToCartSuccess, is ProductDetailResult.AlreadyInCart -> this
            is ProductDetailEvent -> this
        }
    }
}