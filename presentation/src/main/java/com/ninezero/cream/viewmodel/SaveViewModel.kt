package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.saved.SavedAction
import com.ninezero.cream.ui.saved.SavedEvent
import com.ninezero.cream.ui.saved.SavedReducer
import com.ninezero.cream.ui.saved.SavedResult
import com.ninezero.cream.ui.saved.SavedState
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
    init {
        setNetworkStatus(networkRepository)
        viewModelScope.launch {
            saveUseCase.fetchAll().collect { products ->
                action(SavedAction.UpdateProducts(products))
            }
        }
    }

    override fun SavedAction.process(): Flow<SavedResult> = flow {
        when (this@process) {
            is SavedAction.UpdateProducts -> emit(SavedResult.FetchSuccess(products))
            SavedAction.Fetch, SavedAction.Refresh -> emit(fetchAll())
            is SavedAction.Remove -> emit(removeSavedProduct(product))
            is SavedAction.RemoveAll -> emit(removeAll())
            is SavedAction.SortBySavedDate -> emit(sortBySavedDate())
            is SavedAction.SortByPrice -> emit(sortByPrice())
        }
    }

    private suspend fun fetchAll(): SavedResult {
        return if (!networkState.value) {
            delay(3000)
            SavedResult.Error("No internet connection")
        } else {
            try {
                val savedProducts = saveUseCase.fetchAll().first()
                SavedResult.FetchSuccess(savedProducts)
            } catch (e: Exception) {
                SavedResult.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private suspend fun removeSavedProduct(product: Product): SavedResult {
        saveUseCase.toggleSave(product)
        return SavedResult.Remove(product.productId)
    }

    private suspend fun removeAll(): SavedResult {
        saveUseCase.removeAll()
        return SavedResult.RemoveAll
    }

    private fun sortBySavedDate(): SavedResult {
        val currentState = state.value
        return if (currentState is SavedState.Content) {
            SavedResult.Sorted(currentState.savedProducts.sortedByDescending { it.savedAt })
        } else {
            SavedResult.Sorted(emptyList())
        }
    }

    private fun sortByPrice(): SavedResult {
        val currentState = state.value
        return if (currentState is SavedState.Content) {
            SavedResult.Sorted(currentState.savedProducts.sortedByDescending { it.price.instantBuyPrice })
        } else {
            SavedResult.Sorted(emptyList())
        }
    }

    override fun refreshData() = action(SavedAction.Fetch)
}