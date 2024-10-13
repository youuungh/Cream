package com.ninezero.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ninezero.domain.model.SearchHistory

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val keyword: String,
    val timestamp: Long
) {
    fun toDomain(): SearchHistory = SearchHistory(
        keyword = keyword,
        timestamp = timestamp
    )
}

fun SearchHistory.toEntity() = SearchHistoryEntity(
    keyword = keyword,
    timestamp = timestamp
)