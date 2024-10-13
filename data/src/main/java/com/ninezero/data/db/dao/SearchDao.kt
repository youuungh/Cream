package com.ninezero.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ninezero.data.db.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE keyword = :keyword")
    suspend fun delete(keyword: String)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}