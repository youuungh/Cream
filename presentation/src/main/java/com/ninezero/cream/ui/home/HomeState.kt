package com.ninezero.cream.ui.home

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.cream.model.Message
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class HomeAction : MviAction {
    object Fetch : HomeAction()
    object NavigateToSaved : HomeAction()
    data class ToggleSave(val product: Product) : HomeAction()
    data class UpdateSavedIds(val savedIds: Set<String>) : HomeAction()
    data class ProductClicked(val productId: String) : HomeAction()
}

sealed class HomeResult : MviResult {
    object Fetching : HomeResult()
    data class HomeContent(val homeData: HomeData, val savedIds: Set<String>) : HomeResult()
    data class Error(val message: String) : HomeResult()
    data class SaveToggled(val productId: String, val isSaved: Boolean) : HomeResult()
}

sealed class HomeEvent : MviEvent, HomeResult() {
    object NavigateToSaved : HomeEvent()
    data class ShowSnackbar(val message: Message) : HomeEvent()
    data class NavigateToProductDetail(val productId: String) : HomeEvent()
}

sealed class HomeState : MviViewState {
    object Fetching : HomeState()
    data class Content(val homeData: HomeData, val savedIds: Set<String>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeReducer @Inject constructor() : MviStateReducer<HomeState, HomeResult> {
    override fun HomeState.reduce(result: HomeResult): HomeState {
        return when (result) {
            is HomeResult.Fetching -> HomeState.Fetching
            is HomeResult.HomeContent -> HomeState.Content(result.homeData, result.savedIds)
            is HomeResult.Error -> HomeState.Error(result.message)
            is HomeResult.SaveToggled -> {
                if (this is HomeState.Content) {
                    val updatedSavedIds = if (result.isSaved) {
                        savedIds + result.productId
                    } else {
                        savedIds - result.productId
                    }
                    val updatedHomeData = homeData.copy(
                        justDropped = homeData.justDropped.map { if (it.productId == result.productId) it.copy(isSaved = result.isSaved) else it },
                        mostPopular = homeData.mostPopular.map { if (it.productId == result.productId) it.copy(isSaved = result.isSaved) else it },
                        forYou = homeData.forYou.map { if (it.productId == result.productId) it.copy(isSaved = result.isSaved) else it }
                    )
                    HomeState.Content(updatedHomeData, updatedSavedIds)
                } else this
            }
            is HomeEvent -> this
        }
    }
}