package com.ninezero.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ninezero.data.db.entity.SavedProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaveDao {
    @Query("SELECT * FROM saved_products")
    fun fetchAll(): Flow<List<SavedProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: SavedProductEntity)

    @Query("DELETE FROM saved_products WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM saved_products")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM saved_products WHERE productId = :productId)")
    fun isSaved(productId: String): Flow<Boolean>
}