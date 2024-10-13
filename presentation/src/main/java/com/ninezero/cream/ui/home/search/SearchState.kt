package com.ninezero.cream.ui.home.search

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.cream.model.Message
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.domain.model.Product
import javax.inject.Inject

sealed class SearchAction : MviAction {
    object ClearHistory : SearchAction()
    object ClearSearch : SearchAction()
    object NavigateToSaved : SearchAction()
    data class UpdateQuery(val query: String) : SearchAction()
    data class Search(val query: String) : SearchAction()
    data class RemoveHistory(val keyword: String) : SearchAction()
    data class UpdateHistory(val history: List<String>) : SearchAction()
    data class UpdateHistoryVisibility(val isHidden: Boolean) : SearchAction()
    data class ChangeSort(val sortOption: SearchSortOption) : SearchAction()
    data class ToggleSave(val product: Product) : SearchAction()
    data class UpdateSavedIds(val savedIds: Set<String>) : SearchAction()
}

sealed class SearchResult : MviResult {
    data class Init(val query: String) : SearchResult()
    data class Suggesting(val query: String) : SearchResult()
    data class SuggestionsReady(val suggestions: List<String>, val query: String) : SearchResult()
    data class Searching(val query: String) : SearchResult()
    data class SearchComplete(
        val products: List<Product>,
        val query: String,
        val sortOption: SearchSortOption = SearchSortOption.RECOMMENDED,
        val savedIds: Set<String>
    ) : SearchResult()
    data class Error(val message: String) : SearchResult()
    data class HistoryUpdated(val history: List<String>) : SearchResult()
    data class HistoryVisibilityUpdated(val isHidden: Boolean) : SearchResult()
    object HistoryCleared : SearchResult()
    data class HistoryItemRemoved(val keyword: String) : SearchResult()
    data class ClearSearch(val history: List<String>) : SearchResult()
    data class SaveToggled(val productId: String, val isSaved: Boolean) : SearchResult()
}

sealed class SearchEvent : MviEvent, SearchResult() {
    object NavigateToSaved : SearchEvent()
    data class ShowSnackbar(val message: Message) : SearchEvent()
}

sealed class SearchState : MviViewState {
    data class Init(
        val history: List<String>,
        val isHistoryHidden: Boolean,
        val query: String
    ) : SearchState()
    data class Suggesting(val suggestions: List<String>, val query: String) : SearchState()
    data class Searching(val query: String) : SearchState()
    data class Results(
        val products: List<Product>,
        val query: String,
        val sortOption: SearchSortOption = SearchSortOption.RECOMMENDED,
        val savedIds: Set<String>
    ) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchReducer @Inject constructor() : MviStateReducer<SearchState, SearchResult> {
    override fun SearchState.reduce(result: SearchResult): SearchState = when (result) {
        is SearchResult.Init -> SearchState.Init(
            history = (this as? SearchState.Init)?.history ?: emptyList(),
            isHistoryHidden = (this as? SearchState.Init)?.isHistoryHidden ?: false,
            query = result.query
        )
        is SearchResult.Suggesting -> SearchState.Suggesting(emptyList(), result.query)
        is SearchResult.SuggestionsReady -> SearchState.Suggesting(
            result.suggestions,
            result.query
        )
        is SearchResult.Searching -> SearchState.Searching(result.query)
        is SearchResult.SearchComplete -> SearchState.Results(
            result.products,
            result.query,
            result.sortOption,
            result.savedIds
        )
        is SearchResult.Error -> SearchState.Error(result.message)
        is SearchResult.HistoryUpdated -> when (this) {
            is SearchState.Init -> copy(history = result.history)
            else -> this
        }
        is SearchResult.HistoryVisibilityUpdated -> when (this) {
            is SearchState.Init -> copy(isHistoryHidden = result.isHidden)
            else -> this
        }
        is SearchResult.HistoryCleared -> SearchState.Init(emptyList(), true, "")
        is SearchResult.HistoryItemRemoved -> when (this) {
            is SearchState.Init -> copy(history = history.filter { it != result.keyword })
            else -> this
        }
        is SearchResult.ClearSearch -> SearchState.Init(
            history = result.history,
            isHistoryHidden = (this as? SearchState.Init)?.isHistoryHidden ?: false,
            query = ""
        )
        is SearchResult.SaveToggled -> when (this) {
            is SearchState.Results -> {
                val updatedProducts = products.map {
                    if (it.productId == result.productId) it.copy(isSaved = result.isSaved) else it
                }
                val updatedSavedIds = if (result.isSaved) {
                    savedIds + result.productId
                } else {
                    savedIds - result.productId
                }
                copy(products = updatedProducts, savedIds = updatedSavedIds)
            }
            else -> this
        }
        is SearchEvent -> this
    }
}