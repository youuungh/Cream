package com.ninezero.data.repository

import com.ninezero.data.db.dao.SearchDao
import com.ninezero.data.db.entity.toEntity
import com.ninezero.domain.model.SearchHistory
import com.ninezero.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao
) : SearchRepository {
    private val _isSearchHistoryHidden = MutableStateFlow(false)

    override fun getSearchHistory(): Flow<List<SearchHistory>> =
        searchDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addSearchHistory(keyword: String) =
        searchDao.insert(SearchHistory(keyword, System.currentTimeMillis()).toEntity())

    override suspend fun removeSearchHistory(keyword: String) = searchDao.delete(keyword)

    override suspend fun clearSearchHistory() = searchDao.deleteAll()

    override suspend fun hideSearchHistory() { _isSearchHistoryHidden.value = true }

    override fun isSearchHistoryHidden(): Flow<Boolean> = _isSearchHistoryHidden
}