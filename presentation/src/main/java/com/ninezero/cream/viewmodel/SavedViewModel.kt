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
import com.ninezero.cream.utils.SavedSortOption
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    private val _sortType = MutableStateFlow(SavedSortOption.SAVED_DATE)
    val sortType: StateFlow<SavedSortOption> = _sortType.asStateFlow()

    private val _savedProducts = MutableStateFlow<List<Product>>(emptyList())

    init {
        observeSavedProducts()
    }

    override fun SavedAction.process(): Flow<SavedResult> = when (this@process) {
        is SavedAction.Fetch -> fetchAll()
        is SavedAction.Remove -> removeSavedProduct(product)
        is SavedAction.RemoveAll -> removeAll()
        is SavedAction.UpdateSortType -> updateSortType(sortOption)
        is SavedAction.UpdateProducts -> updateProducts(products)
        is SavedAction.Error -> emitResult(SavedResult.Error(message))
    }

    private fun fetchAll(): Flow<SavedResult> = flow {
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

    private fun removeSavedProduct(product: Product): Flow<SavedResult> = flow {
        saveUseCase.toggleSave(product)
        val updatedProducts = _savedProducts.value.filter { it.productId != product.productId }
        _savedProducts.value = updatedProducts
        emit(SavedResult.Remove(product.productId))
    }

    private fun removeAll(): Flow<SavedResult> = flow {
        saveUseCase.removeAll()
        _savedProducts.value = emptyList()
        emit(SavedResult.RemoveAll)
    }

    private fun updateSortType(sortOption: SavedSortOption): Flow<SavedResult> = flow {
        _sortType.value = sortOption
        val sortedProducts = sortProducts(_savedProducts.value)
        emit(SavedResult.FetchSuccess(sortedProducts))
    }

    private fun updateProducts(products: List<Product>): Flow<SavedResult> = flow {
        val sortedProducts = sortProducts(products)
        _savedProducts.value = sortedProducts
        emit(SavedResult.FetchSuccess(sortedProducts))
    }

    private fun sortProducts(products: List<Product>): List<Product> {
        return when (_sortType.value) {
            SavedSortOption.SAVED_DATE -> products.sortedByDescending { it.savedAt }
            SavedSortOption.PRICE -> products.sortedByDescending { it.price.instantBuyPrice }
        }
    }

    private fun observeSavedProducts() {
        viewModelScope.launch {
            saveUseCase.savedProductIds.collect { savedIds ->
                try {
                    val savedProducts = saveUseCase.fetchAll().first().filter { it.productId in savedIds }
                    val sortedProducts = sortProducts(savedProducts)
                    _savedProducts.value = sortedProducts
                    if (networkState.value) {
                        action(SavedAction.UpdateProducts(sortedProducts))
                    } else {
                        delay(NETWORK_DELAY)
                        action(SavedAction.Error(NO_INTERNET_CONNECTION))
                    }
                } catch (e: Exception) {
                    action(SavedAction.Error(ErrorHandler.getErrorMessage(e)))
                }
            }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is SavedState.Error
    override fun refreshData() = action(SavedAction.Fetch)
}