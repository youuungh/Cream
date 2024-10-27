package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.home.HomeAction
import com.ninezero.cream.ui.home.HomeEvent
import com.ninezero.cream.ui.home.HomeReducer
import com.ninezero.cream.ui.home.HomeResult
import com.ninezero.cream.ui.home.HomeState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.updateSaveStatus
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.AuthUseCase
import com.ninezero.domain.usecase.HomeUseCase
import com.ninezero.domain.usecase.SaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val authUseCase: AuthUseCase,
    private val saveUseCase: SaveUseCase,
    reducer: HomeReducer,
    networkRepository: NetworkRepository,
) : BaseStateViewModel<HomeAction, HomeResult, HomeEvent, HomeState, HomeReducer>(
    initialState = HomeState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {
    private val _isRefresh = MutableStateFlow(false)
    val isRefresh = _isRefresh.asStateFlow()

    init {
        action(HomeAction.Fetch)
        observeSavedIds()
    }

    override fun HomeAction.process(): Flow<HomeResult> = when (this@process) {
        is HomeAction.Fetch -> fetchHomeData()
        is HomeAction.ToggleSave -> toggleSave(product)
        is HomeAction.UpdateSavedIds -> updateSavedIds(savedIds)
        is HomeAction.NavigateToSaved -> emitEvent(HomeEvent.NavigateToSaved)
        is HomeAction.ProductClicked -> emitEvent(HomeEvent.NavigateToProductDetail(productId))
    }

    private fun fetchHomeData(): Flow<HomeResult> = flow {
        emit(HomeResult.Fetching)
        try {
            handleNetworkCallback { homeUseCase() }.collect {
                when (it) {
                    is EntityWrapper.Success -> {
                        val savedIds = saveUseCase.savedProductIds.value
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

    private fun toggleSave(product: Product): Flow<HomeResult> = flow {
        if (authUseCase.getCurrentUser() != null) {
            saveUseCase.toggleSave(product)
            emit(HomeResult.SaveToggled(product.productId, !product.isSaved))
            if (!product.isSaved) {
                emit(
                    HomeEvent.ShowSnackbar(
                        Message(
                            messageId = R.string.saved_item_added,
                            actionLabelId = R.string.view_saved,
                            onAction = { action(HomeAction.NavigateToSaved) }
                        )
                    )
                )
            }
        } else emit(HomeEvent.NavigateToLogin)
    }

    private fun updateSavedIds(savedIds: Set<String>): Flow<HomeResult> = flow {
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
            saveUseCase.savedProductIds.collect { savedIds ->
                val updatedSavedIds = authUseCase.getCurrentUser()?.let { savedIds } ?: emptySet()
                action(HomeAction.UpdateSavedIds(updatedSavedIds))
            }
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is HomeState.Error
    override fun refreshData() {
        viewModelScope.launch {
            _isRefresh.value = true
            delay(300)

            launch { action(HomeAction.Fetch) }.join()

            delay(300)
            _isRefresh.value = false
        }
    }
}