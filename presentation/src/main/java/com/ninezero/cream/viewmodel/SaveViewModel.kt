package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.saved.SavedAction
import com.ninezero.cream.ui.saved.SavedEvent
import com.ninezero.cream.ui.saved.SavedReducer
import com.ninezero.cream.ui.saved.SavedResult
import com.ninezero.cream.ui.saved.SavedState
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
    reducer = reducer
) {
    private val _sortType = MutableStateFlow(R.string.sort_by_saved_date)
    val sortType: StateFlow<Int> = _sortType.asStateFlow()

    init {
        setNetworkRepository(networkRepository)
        action(SavedAction.Fetch)
        updateData()
    }

    private fun updateData() {
        viewModelScope.launch {
            saveUseCase.fetchAll()
                .collect { products ->
                    if (networkState.value) action(SavedAction.UpdateProducts(products))
                }
        }
    }

    override fun SavedAction.process(): Flow<SavedResult> = flow {
        when (this@process) {
            SavedAction.Fetch -> fetchAll()
            is SavedAction.UpdateProducts -> {
                if (networkState.value) emit(SavedResult.FetchSuccess(products))
            }
            is SavedAction.Remove -> removeSavedProduct(product)
            is SavedAction.RemoveAll -> removeAll()
            is SavedAction.SortBySavedDate -> sortProducts { it.sortedByDescending { product -> product.savedAt } }
            is SavedAction.SortByPrice -> sortProducts { it.sortedByDescending { product -> product.price.instantBuyPrice } }
        }
    }

    private suspend fun FlowCollector<SavedResult>.fetchAll() {
        emit(SavedResult.Fetching)
        if (!networkState.value) {
            delay(3000)
            emit(SavedResult.Error("No internet connection"))
        } else {
            try {
                val products = saveUseCase.fetchAll().first()
                emit(SavedResult.FetchSuccess(products))
            } catch (e: Exception) {
                emit(SavedResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    private suspend fun FlowCollector<SavedResult>.removeSavedProduct(product: Product) {
        saveUseCase.toggleSave(product)
        emit(SavedResult.Remove(product.productId))
    }

    private suspend fun FlowCollector<SavedResult>.removeAll() {
        saveUseCase.removeAll()
        emit(SavedResult.RemoveAll)
    }

    private suspend fun FlowCollector<SavedResult>.sortProducts(sorter: (List<Product>) -> List<Product>) {
        val currentState = state.value
        if (currentState is SavedState.Content) {
            emit(SavedResult.Sorted(sorter(currentState.savedProducts)))
        }
    }

    fun updateSortType(newSortType: Int) {
        _sortType.value = newSortType
        when (newSortType) {
            R.string.sort_by_saved_date -> action(SavedAction.SortBySavedDate)
            R.string.sort_by_price -> action(SavedAction.SortByPrice)
        }
    }

    override fun refreshData() = action(SavedAction.Fetch)
}