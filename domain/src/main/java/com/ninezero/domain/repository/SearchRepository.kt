package com.ninezero.domain.repository

import com.ninezero.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun addSearchHistory(keyword: String)
    suspend fun removeSearchHistory(keyword: String)
    suspend fun clearSearchHistory()
    suspend fun hideSearchHistory()
    fun isSearchHistoryHidden(): Flow<Boolean>
}