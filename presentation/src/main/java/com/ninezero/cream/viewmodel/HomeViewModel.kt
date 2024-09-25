package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.home.HomeAction
import com.ninezero.cream.ui.home.HomeEvent
import com.ninezero.cream.ui.home.HomeReducer
import com.ninezero.cream.ui.home.HomeResult
import com.ninezero.cream.ui.home.HomeState
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.HomeUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val saveUseCase: SaveUseCase,
    reducer: HomeReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<HomeAction, HomeResult, HomeEvent, HomeState, HomeReducer>(
    initialState = HomeState.Fetching,
    reducer = reducer
) {
    init {
        setNetworkStatus(networkRepository)
        action(HomeAction.Fetch)
        viewModelScope.launch {
            saveUseCase.fetchProductIds().collect { action(HomeAction.UpdateSavedIds(it)) }
        }
    }

    override fun HomeAction.process(): Flow<HomeResult> = flow {
        when (this@process) {
            HomeAction.Fetch, HomeAction.Refresh -> fetchHomeData()
            is HomeAction.ProductClicked -> emit(HomeEvent.NavigateToProductDetail(productId))
            is HomeAction.ToggleSave -> toggleSave(product)
            is HomeAction.UpdateSavedIds -> updateSavedIds(savedIds)
        }
    }

    private suspend fun FlowCollector<HomeResult>.fetchHomeData()  {
        emit(HomeResult.Fetching)
        if (!networkState.value) {
            delay(3000)
            emit(HomeResult.Error("No internet connection"))
        } else {
            homeUseCase().collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val savedIds = (state.value as? HomeState.Content)?.savedIds ?: emptySet()
                        val updateHomeData = it.entity.copy(
                            justDropped = updateSaveStatus(it.entity.justDropped, savedIds),
                            mostPopular = updateSaveStatus(it.entity.mostPopular, savedIds),
                            forYou = updateSaveStatus(it.entity.forYou, savedIds)
                        )
                        emit(HomeResult.HomeContent(updateHomeData, savedIds))
                    }
                    is EntityWrapper.Fail -> emit(HomeResult.Error(it.error.message ?: "Unknown error occurred"))
                }
            }
        }
    }

    private fun updateSaveStatus(products: List<Product>, saveIds: Set<String>): List<Product> {
        return products.map { it.copy(isSaved = it.productId in saveIds) }
    }

    private suspend fun FlowCollector<HomeResult>.toggleSave(product: Product) {
        saveUseCase.toggleSave(product)
        emit(HomeResult.SaveToggled(product.productId, !product.isSaved))
    }

    private suspend fun FlowCollector<HomeResult>.updateSavedIds(savedIds: Set<String>) {
        val currentState = state.value
        if (currentState is HomeState.Content) {
            val updatedHomeData = currentState.homeData.copy(
                justDropped = updateSaveStatus(currentState.homeData.justDropped, savedIds),
                mostPopular = updateSaveStatus(currentState.homeData.mostPopular, savedIds),
                forYou = updateSaveStatus(currentState.homeData.forYou, savedIds)
            )
            emit(HomeResult.HomeContent(updatedHomeData, savedIds))
        }
    }

    override fun refreshData() = action(HomeAction.Refresh)
}