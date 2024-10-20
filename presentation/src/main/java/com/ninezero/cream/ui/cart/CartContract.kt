package com.ninezero.cream.ui.cart

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class CartAction : MviAction {
    object Fetch : CartAction()
    data class Add(val product: Product) : CartAction()
    data class Remove(val product: Product) : CartAction()
    object RemoveSelected : CartAction()
    data class UpdateSelection(val productId: String, val isSelected: Boolean) : CartAction()
    data class UpdateAllSelection(val isSelected: Boolean) : CartAction()
    data class UpdateProducts(val products: List<Product>) : CartAction()
    data class Error(val message: String) : CartAction()
}

sealed class CartResult : MviResult {
    object Fetching : CartResult()
    data class FetchSuccess(val products: List<Product>) : CartResult()
    data class Add(val product: Product) : CartResult()
    data class Remove(val productId: String) : CartResult()
    object RemoveSelected : CartResult()
    data class UpdateSelection(val productId: String, val isSelected: Boolean) : CartResult()
    data class UpdateAllSelection(val isSelected: Boolean) : CartResult()
    data class Error(val message: String) : CartResult()
}

sealed class CartEvent : MviEvent {
    data class NavigateToProductDetail(val productId: String) : CartEvent()
}

sealed class CartState : MviViewState {
    object Fetching : CartState()
    data class Content(val products: List<Product>) : CartState()
    data class Error(val message: String) : CartState()
}

class CartReducer @Inject constructor() : MviStateReducer<CartState, CartResult> {
    override fun CartState.reduce(result: CartResult): CartState {
        return when (result) {
            is CartResult.Fetching -> CartState.Fetching
            is CartResult.FetchSuccess -> CartState.Content(result.products)
            is CartResult.Add -> {
                if (this is CartState.Content) {
                    CartState.Content(products + result.product)
                } else this
            }
            is CartResult.Remove -> {
                if (this is CartState.Content) {
                    CartState.Content(products.filter { it.productId != result.productId })
                } else this
            }
            is CartResult.RemoveSelected -> {
                if (this is CartState.Content) {
                    CartState.Content(products.filter { !it.isSelected })
                } else this
            }
            is CartResult.UpdateSelection -> {
                if (this is CartState.Content) {
                    CartState.Content(products.map {
                        if (it.productId == result.productId) it.copy(isSelected = result.isSelected) else it
                    })
                } else this
            }
            is CartResult.UpdateAllSelection -> {
                if (this is CartState.Content) {
                    CartState.Content(products.map { it.copy(isSelected = result.isSelected) })
                } else this
            }
            is CartResult.Error -> CartState.Error(result.message)
        }
    }
}