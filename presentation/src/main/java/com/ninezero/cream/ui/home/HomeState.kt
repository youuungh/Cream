package com.ninezero.cream.ui.home

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.HomeData
import javax.inject.Inject

sealed class HomeAction : MviAction {
    data object Fetch : HomeAction()
    data object Refresh : HomeAction()
    data class ProductClicked(val productId: String) : HomeAction()
    //data class SavedClicked(val productId: String) : HomeAction()
}

sealed class HomeResult : MviResult {
    data object Loading : HomeResult()
    data class HomeContent(val homeData: HomeData) : HomeResult()
    data class Error(val message: String) : HomeResult()
}

sealed class HomeEvent : MviEvent, HomeResult() {
    data class NavigateToProductDetail(val productId: String) : HomeEvent()
}

sealed class HomeState : MviViewState {
    data object Loading : HomeState()
    data class Content(val homeData: HomeData) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeReducer @Inject constructor() : MviStateReducer<HomeState, HomeResult> {
    override fun HomeState.reduce(result: HomeResult): HomeState {
        return when (result) {
            is HomeResult.Loading -> HomeState.Loading
            is HomeResult.HomeContent -> HomeState.Content(result.homeData)
            is HomeResult.Error -> HomeState.Error(result.message)
            is HomeEvent.NavigateToProductDetail -> this
        }
    }
}