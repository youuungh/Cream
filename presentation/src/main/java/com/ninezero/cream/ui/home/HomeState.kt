package com.ninezero.cream.ui.home

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class HomeAction : MviAction {
    data object Fetch : HomeAction()
    data object Refresh : HomeAction()
    data class ProductClicked(val productId: String) : HomeAction()
    data class ToggleSave(val product: Product) : HomeAction()
    data class UpdateSavedIds(val savedIds: Set<String>) : HomeAction()
    data object NavigateToSaved : HomeAction()
}

sealed class HomeResult : MviResult {
    data object Fetching : HomeResult()
    data class HomeContent(val homeData: HomeData, val savedIds: Set<String>) : HomeResult()
    data class Error(val message: String) : HomeResult()
    data class SaveToggled(val productId: String, val isSaved: Boolean) : HomeResult()
}

sealed class HomeEvent : MviEvent, HomeResult() {
    data class NavigateToProductDetail(val productId: String) : HomeEvent()
    data object NavigateToSaved : HomeEvent()
}

sealed class HomeState : MviViewState {
    data object Fetching : HomeState()
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
                        justDropped = updateSaveStatus(homeData.justDropped, updatedSavedIds),
                        mostPopular = updateSaveStatus(homeData.mostPopular, updatedSavedIds),
                        forYou = updateSaveStatus(homeData.forYou, updatedSavedIds)
                    )
                    HomeState.Content(updatedHomeData, updatedSavedIds)
                } else this
            }
            is HomeEvent -> this
        }
    }

    private fun updateSaveStatus(products: List<Product>, savedIds: Set<String>): List<Product> {
        return products.map { it.copy(isSaved = it.productId in savedIds) }
    }
}