package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.cart.CartAction
import com.ninezero.cream.ui.cart.CartEvent
import com.ninezero.cream.ui.cart.CartReducer
import com.ninezero.cream.ui.cart.CartResult
import com.ninezero.cream.ui.cart.CartState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.cream.utils.NETWORK_DELAY
import com.ninezero.cream.utils.NO_INTERNET_CONNECTION
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.CartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCase: CartUseCase,
    reducer: CartReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<CartAction, CartResult, CartEvent, CartState, CartReducer>(
    initialState = CartState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    private val _cartProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _allSelected = MutableStateFlow(true)
    val allSelected: StateFlow<Boolean> = _allSelected

    init {
        observeCartProducts()
    }

    override fun CartAction.process(): Flow<CartResult> = when (this@process) {
        is CartAction.Fetch -> fetchAll()
        is CartAction.Add -> addToCart(product)
        is CartAction.Remove -> removeFromCart(product)
        is CartAction.RemoveSelected -> removeSelected()
        is CartAction.UpdateSelection -> updateSelection(productId, isSelected)
        is CartAction.UpdateAllSelection -> updateAllSelection(isSelected)
        is CartAction.UpdateProducts -> updateProducts(products)
        is CartAction.Error -> flow { emit(CartResult.Error(message)) }
    }

    private fun fetchAll(): Flow<CartResult> = flow {
        emit(CartResult.Fetching)
        try {
            handleNetworkCallback {
                flow {
                    val products = _cartProducts.value
                    emit(CartResult.FetchSuccess(products))
                }
            }.collect { emit(it) }
        } catch (e: Exception) {
            emit(CartResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private fun addToCart(product: Product): Flow<CartResult> = flow {
        try {
            cartUseCase.addToCart(product)
            emit(CartResult.Add(product))
            updateAllSelectedState()
        } catch (e: Exception) {
            emit(CartResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private fun removeFromCart(product: Product): Flow<CartResult> = flow {
        cartUseCase.removeFromCart(product.productId)
        emit(CartResult.Remove(product.productId))
        updateAllSelectedState()
    }

    private fun removeSelected(): Flow<CartResult> = flow {
        val selectedProducts = _cartProducts.value.filter { it.isSelected }
        cartUseCase.removeSelected(selectedProducts.map { it.productId })
        emit(CartResult.RemoveSelected)
        _allSelected.value = false
    }

    private fun updateSelection(productId: String, isSelected: Boolean): Flow<CartResult> = flow {
        cartUseCase.updateSelection(productId, isSelected)
        emit(CartResult.UpdateSelection(productId, isSelected))
        updateAllSelectedState()
    }

    private fun updateAllSelection(isSelected: Boolean): Flow<CartResult> = flow {
        cartUseCase.updateAllSelection(isSelected)
        _allSelected.value = isSelected
        emit(CartResult.UpdateAllSelection(isSelected))
    }

    private fun updateProducts(products: List<Product>): Flow<CartResult> = flow {
        _cartProducts.value = products
        emit(CartResult.FetchSuccess(products))
        updateAllSelectedState()
    }

    private fun observeCartProducts() {
        viewModelScope.launch {
            cartUseCase.fetchAll().collect { products ->
                _cartProducts.value = products
                if (products.isNotEmpty()) {
                    _allSelected.value = products.all { it.isSelected }
                } else {
                    _allSelected.value = false
                }
                if (networkState.value) {
                    action(CartAction.UpdateProducts(products))
                } else {
                    delay(NETWORK_DELAY)
                    action(CartAction.Error(NO_INTERNET_CONNECTION))
                }
            }
        }
    }

    private fun updateAllSelectedState() {
        _allSelected.value = _cartProducts.value.isNotEmpty() && _cartProducts.value.all { it.isSelected }
    }

    fun calculateTotalPrice(): Int = cartUseCase.calculateTotalPrice(_cartProducts.value.filter { it.isSelected })
    fun calculateTotalFee(): Double = cartUseCase.calculateTotalFee(_cartProducts.value.filter { it.isSelected })

    override fun shouldRefreshOnConnect(): Boolean = state.value is CartState.Error
    override fun refreshData() = action(CartAction.Fetch)
}