package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.saved.SavedAction
import com.ninezero.cream.ui.saved.SavedEvent
import com.ninezero.cream.ui.saved.SavedReducer
import com.ninezero.cream.ui.saved.SavedResult
import com.ninezero.cream.ui.saved.SavedState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.cream.utils.NETWORK_DELAY
import com.ninezero.cream.utils.NO_INTERNET_CONNECTION
import com.ninezero.di.R
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val saveUseCase: SaveUseCase,
    reducer: SavedReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<SavedAction, SavedResult, SavedEvent, SavedState, SavedReducer>(
    initialState = SavedState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    private val _sortType = MutableStateFlow(R.string.sort_by_saved_date)
    val sortType: StateFlow<Int> = _sortType.asStateFlow()

    private val _savedProducts = MutableStateFlow<List<Product>>(emptyList())

    init {
        observeSavedProducts()
    }

    override fun SavedAction.process(): Flow<SavedResult> = flow {
        when (this@process) {
            is SavedAction.Fetch -> fetchAll()
            is SavedAction.Remove -> removeSavedProduct(product)
            is SavedAction.RemoveAll -> removeAll()
            is SavedAction.UpdateSortType -> updateSortType(newSortType)
            is SavedAction.UpdateProducts -> updateProducts(products)
            is SavedAction.Error -> emit(SavedResult.Error(message))
        }
    }

    private suspend fun FlowCollector<SavedResult>.fetchAll() {
        emit(SavedResult.Fetching)
        try {
            handleNetworkCallback {
                flow {
                    val products = sortProducts(_savedProducts.value)
                    emit(SavedResult.FetchSuccess(products))
                }
            }.collect { emit(it) }
        } catch (e: Exception) {
            emit(SavedResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private suspend fun FlowCollector<SavedResult>.removeSavedProduct(product: Product) {
        saveUseCase.toggleSave(product)
        val updatedProducts = _savedProducts.value.filter { it.productId != product.productId }
        _savedProducts.value = updatedProducts
        emit(SavedResult.Remove(product.productId))
    }

    private suspend fun FlowCollector<SavedResult>.removeAll() {
        saveUseCase.removeAll()
        _savedProducts.value = emptyList()
        emit(SavedResult.RemoveAll)
    }

    private suspend fun FlowCollector<SavedResult>.updateSortType(newSortType: Int) {
        _sortType.value = newSortType
        val sortedProducts = sortProducts(_savedProducts.value)
        emit(SavedResult.FetchSuccess(sortedProducts))
    }

    private suspend fun FlowCollector<SavedResult>.updateProducts(products: List<Product>) {
        val sortedProducts = sortProducts(products)
        _savedProducts.value = sortedProducts
        emit(SavedResult.FetchSuccess(sortedProducts))
    }

    private fun sortProducts(products: List<Product>): List<Product> {
        return when (_sortType.value) {
            R.string.sort_by_saved_date -> products.sortedByDescending { it.savedAt }
            R.string.sort_by_price -> products.sortedByDescending { it.price.instantBuyPrice }
            else -> products
        }
    }

    private fun observeSavedProducts() {
        viewModelScope.launch {
            saveUseCase.fetchAll()
                .map { products -> sortProducts(products) }
                .collect { sortedProducts ->
                    _savedProducts.value = sortedProducts
                    if (networkState.value) {
                        action(SavedAction.UpdateProducts(sortedProducts))
                    } else {
                        delay(NETWORK_DELAY)
                        action(SavedAction.Error(NO_INTERNET_CONNECTION))
                    }
                }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is SavedState.Error
    override fun refreshData() = action(SavedAction.Fetch)
}