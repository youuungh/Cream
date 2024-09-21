package com.ninezero.cream.viewmodel

import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.home.HomeAction
import com.ninezero.cream.ui.home.HomeEvent
import com.ninezero.cream.ui.home.HomeReducer
import com.ninezero.cream.ui.home.HomeResult
import com.ninezero.cream.ui.home.HomeState
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.HomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    reducer: HomeReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<HomeAction, HomeResult, HomeEvent, HomeState, HomeReducer>(
    initialState = HomeState.Loading,
    reducer = reducer
) {
    init {
        setNetworkRepository(networkRepository)
        action(HomeAction.Fetch)
    }

    override fun HomeAction.process(): Flow<HomeResult> {
        return when (this) {
            HomeAction.Fetch, HomeAction.Refresh -> fetchHomeData()
            is HomeAction.ProductClicked -> flow {
                emit(HomeEvent.NavigateToProductDetail(productId))
            }
        }
    }

    private fun fetchHomeData(): Flow<HomeResult> = flow {
        emit(HomeResult.Loading)
        if (!networkState.value) {
            delay(3000)
            emit(HomeResult.Error("No internet connection"))
        } else {
            homeUseCase().collect {
                emit(
                    when (it) {
                        is EntityWrapper.Success -> HomeResult.HomeContent(it.entity)
                        is EntityWrapper.Fail -> HomeResult.Error(it.error.message ?: "Unknown error occurred")
                    }
                )
            }
        }
    }

    override fun refreshData() { action(HomeAction.Refresh) }
}