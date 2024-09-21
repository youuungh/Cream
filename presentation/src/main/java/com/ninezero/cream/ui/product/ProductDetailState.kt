package com.ninezero.cream.ui.product

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class ProductDetailAction : MviAction {
    data object Fetch : ProductDetailAction()
    data object Refresh : ProductDetailAction()
    data object ToggleSave : ProductDetailAction()
}

sealed class ProductDetailResult : MviResult {
    data object Loading : ProductDetailResult()
    data class ProductContent(val product: Product) : ProductDetailResult()
    data class Error(val message: String) : ProductDetailResult()
    data class SaveToggled(val isSaved: Boolean) : ProductDetailResult()
}

sealed class ProductDetailEvent : MviEvent {}

sealed class ProductDetailState : MviViewState {
    data object Loading : ProductDetailState()
    data class Content(val product: Product, var appBarAlpha: Float = 0f) : ProductDetailState()
    data class Error(val message: String) : ProductDetailState()
}

class ProductDetailReducer @Inject constructor() : MviStateReducer<ProductDetailState, ProductDetailResult> {
    override fun ProductDetailState.reduce(result: ProductDetailResult): ProductDetailState {
        return when (result) {
            is ProductDetailResult.Loading -> ProductDetailState.Loading
            is ProductDetailResult.ProductContent -> ProductDetailState.Content(result.product)
            is ProductDetailResult.Error -> ProductDetailState.Error(result.message)
            is ProductDetailResult.SaveToggled -> {
                if (this is ProductDetailState.Content) {
                    this.copy(product = product.copy(isSaved = result.isSaved))
                } else
                    this
            }
        }
    }
}