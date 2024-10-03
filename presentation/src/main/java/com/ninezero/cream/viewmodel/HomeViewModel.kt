package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.home.HomeAction
import com.ninezero.cream.ui.home.HomeEvent
import com.ninezero.cream.ui.home.HomeReducer
import com.ninezero.cream.ui.home.HomeResult
import com.ninezero.cream.ui.home.HomeState
import com.ninezero.cream.ui.saved.SavedState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.cream.utils.SnackbarUtils.showSnack
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.updateSaveStatus
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.HomeUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val saveUseCase: SaveUseCase,
    reducer: HomeReducer,
    networkRepository: NetworkRepository,
) : BaseStateViewModel<HomeAction, HomeResult, HomeEvent, HomeState, HomeReducer>(
    initialState = HomeState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    init {
        action(HomeAction.Fetch)
        action(HomeAction.ObserveSavedIds)
    }

    override fun HomeAction.process(): Flow<HomeResult> = flow {
        when (this@process) {
            is HomeAction.Fetch -> fetchHomeData()
            is HomeAction.ProductClicked -> emit(HomeEvent.NavigateToProductDetail(productId))
            is HomeAction.ToggleSave -> toggleSave(product)
            is HomeAction.UpdateSavedIds -> updateSavedIds(savedIds)
            is HomeAction.NavigateToSaved -> emit(HomeEvent.NavigateToSaved)
            is HomeAction.ObserveSavedIds -> observeSavedIds()
        }
    }

    private suspend fun FlowCollector<HomeResult>.fetchHomeData() {
        emit(HomeResult.Fetching)
        try {
            handleNetworkCallback { homeUseCase() }.collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val savedIds = (state.value as? HomeState.Content)?.savedIds ?: emptySet()
                        val updatedHomeData = it.entity.copy(
                            justDropped = it.entity.justDropped.updateSaveStatus(savedIds),
                            mostPopular = it.entity.mostPopular.updateSaveStatus(savedIds),
                            forYou = it.entity.forYou.updateSaveStatus(savedIds)
                        )
                        emit(HomeResult.HomeContent(updatedHomeData, savedIds))
                    }
                    is EntityWrapper.Fail -> emit(HomeResult.Error(ErrorHandler.getErrorMessage(it.error)))
                }
            }
        } catch (e: Exception) {
            emit(HomeResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private suspend fun FlowCollector<HomeResult>.toggleSave(product: Product) {
        saveUseCase.toggleSave(product)
        emit(HomeResult.SaveToggled(product.productId, !product.isSaved))
        if (!product.isSaved) {
            showSnack(
                messageTextId = R.string.saved_item_added,
                actionLabelId = R.string.view_saved,
                onAction = { action(HomeAction.NavigateToSaved) }
            )
        }
    }

    private suspend fun FlowCollector<HomeResult>.updateSavedIds(savedIds: Set<String>) {
        val currentState = state.value
        if (currentState is HomeState.Content) {
            val updatedHomeData = currentState.homeData.copy(
                justDropped = currentState.homeData.justDropped.updateSaveStatus(savedIds),
                mostPopular = currentState.homeData.mostPopular.updateSaveStatus(savedIds),
                forYou = currentState.homeData.forYou.updateSaveStatus(savedIds)
            )
            emit(HomeResult.HomeContent(updatedHomeData, savedIds))
        }
    }

    private fun observeSavedIds() {
        viewModelScope.launch {
            saveUseCase.fetchProductIds().collect { savedIds ->
                action(HomeAction.UpdateSavedIds(savedIds))
            }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is HomeState.Error
    override fun refreshData() = action(HomeAction.Fetch)
}