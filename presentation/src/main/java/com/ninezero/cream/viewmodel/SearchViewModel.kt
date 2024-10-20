package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.home.search.SearchAction
import com.ninezero.cream.ui.home.search.SearchEvent
import com.ninezero.cream.ui.home.search.SearchReducer
import com.ninezero.cream.ui.home.search.SearchResult
import com.ninezero.cream.ui.home.search.SearchState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.di.R
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.updateSaveStatus
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.AuthUseCase
import com.ninezero.domain.usecase.SaveUseCase
import com.ninezero.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val authUseCase: AuthUseCase,
    private val saveUseCase: SaveUseCase,
    reducer: SearchReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<SearchAction, SearchResult, SearchEvent, SearchState, SearchReducer>(
    initialState = SearchState.Init(emptyList(), false, ""),
    reducer = reducer,
    networkRepository = networkRepository
) {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode: StateFlow<Boolean> = _isSearchMode.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                searchUseCase.getSearchHistory().collect { history ->
                    action(SearchAction.UpdateHistory(history.map { it.keyword }))
                }
            }
            launch {
                searchUseCase.isSearchHistoryHidden().collect { isHidden ->
                    action(SearchAction.UpdateHistoryVisibility(isHidden))
                }
            }
        }
        observeSavedIds()
    }

    override fun SearchAction.process(): Flow<SearchResult> = when(this@process) {
        is SearchAction.UpdateQuery -> processQuery(query)
        is SearchAction.Search -> performSearch(query)
        is SearchAction.RemoveHistory -> removeHistoryItem(keyword)
        is SearchAction.ChangeSort -> sortResults(sortOption)
        is SearchAction.ClearHistory -> clearHistory()
        is SearchAction.ClearSearch -> resetSearch()
        is SearchAction.UpdateHistory -> emitResult(SearchResult.HistoryUpdated(history))
        is SearchAction.UpdateHistoryVisibility -> emitResult(SearchResult.HistoryVisibilityUpdated(isHidden))
        is SearchAction.ToggleSave -> toggleSave(product)
        is SearchAction.UpdateSavedIds -> updateSavedIds(savedIds)
        is SearchAction.NavigateToSaved -> emitEvent(SearchEvent.NavigateToSaved)
    }

    @OptIn(FlowPreview::class)
    private fun processQuery(query: String): Flow<SearchResult> = flow {
        emit(SearchResult.Init(query = query))
        if (query.isNotBlank()) {
            emit(SearchResult.Suggesting(query))
            searchUseCase.getSuggestedKeywords(query).collect { suggestions ->
                emit(SearchResult.SuggestionsReady(suggestions, query))
            }
        } else {
            fetchHistory()
        }
    }.debounce(300)

    private fun performSearch(query: String): Flow<SearchResult> = flow {
        _query.value = query
        emit(SearchResult.Searching(query))
        searchUseCase.addSearchHistory(query)
        searchUseCase.searchProducts(query).collect {
            when (it) {
                is EntityWrapper.Success -> {
                    val savedIds = saveUseCase.savedProductIds.value
                    val updatedProducts = it.entity.updateSaveStatus(savedIds)
                    val sortedProducts = updatedProducts.sortedByDescending { product -> product.tradingVolume }
                    emit(SearchResult.SearchComplete(sortedProducts, query, SearchSortOption.RECOMMENDED, savedIds))
                }
                is EntityWrapper.Fail -> emit(SearchResult.Error(ErrorHandler.getErrorMessage(it.error)))
            }
        }
    }

    private fun clearHistory(): Flow<SearchResult> = flow {
        searchUseCase.clearSearchHistory()
        searchUseCase.hideSearchHistory()
        emit(SearchResult.HistoryCleared)
    }

    private fun removeHistoryItem(keyword: String): Flow<SearchResult> = flow {
        searchUseCase.removeSearchHistory(keyword)
        emit(SearchResult.HistoryItemRemoved(keyword))
    }

    private fun sortResults(sortOption: SearchSortOption): Flow<SearchResult> = flow {
        val currentState = state.value
        if (currentState is SearchState.Results) {
            val sortedProducts = when (sortOption) {
                SearchSortOption.RECOMMENDED -> currentState.products.sortedByDescending { it.tradingVolume }
                SearchSortOption.PRICE_LOW_TO_HIGH -> currentState.products.sortedBy { it.price.instantBuyPrice }
                SearchSortOption.PRICE_HIGH_TO_LOW -> currentState.products.sortedByDescending { it.price.instantBuyPrice }
            }
            emit(SearchResult.SearchComplete(sortedProducts, currentState.query, sortOption, currentState.savedIds))
        }
    }

    private fun resetSearch(): Flow<SearchResult> = flow {
        _query.value = ""
        emit(SearchResult.Init(query = ""))
        fetchHistory()
    }

    private fun fetchHistory() {
        viewModelScope.launch {
            val history = searchUseCase.getSearchHistory().first()
            action(SearchAction.UpdateHistory(history.map { it.keyword }))
        }
    }

    private fun toggleSave(product: Product): Flow<SearchResult> = flow {
        if (authUseCase.getCurrentUser() != null) {
            saveUseCase.toggleSave(product)
            emit(SearchResult.SaveToggled(product.productId, !product.isSaved))
            if (!product.isSaved) {
                emit(
                    SearchEvent.ShowSnackbar(
                        Message(
                            messageId = R.string.saved_item_added,
                            actionLabelId = R.string.view_saved,
                            onAction = { action(SearchAction.NavigateToSaved) }
                        )
                    )
                )
            }
        } else emit(SearchEvent.NavigateToLogin)
    }

    private fun updateSavedIds(savedIds: Set<String>): Flow<SearchResult> = flow {
        val currentState = state.value
        if (currentState is SearchState.Results) {
            val updatedProducts = currentState.products.updateSaveStatus(savedIds)
            emit(SearchResult.SearchComplete(updatedProducts, currentState.query, currentState.sortOption, savedIds))
        }
    }

    private fun observeSavedIds() {
        viewModelScope.launch {
            saveUseCase.savedProductIds.collect { action(SearchAction.UpdateSavedIds(it)) }
        }
    }

    fun setSearchMode(active: Boolean) { _isSearchMode.value = active }

    fun updateQuery(query: String) { action(SearchAction.UpdateQuery(query)) }

    fun search(query: String) { action(SearchAction.Search(query)) }

    fun clearSearch() { action(SearchAction.ClearSearch) }

    override fun refreshData() {
        val currentQuery = _query.value
        if (currentQuery.isNotBlank()) {
            action(SearchAction.Search(currentQuery))
        } else {
            fetchHistory()
        }
    }
}