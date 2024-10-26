package com.ninezero.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ninezero.data.db.entity.CartProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_products")
    fun fetchAll(): Flow<List<CartProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: CartProductEntity)

    @Query("DELETE FROM cart_products WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM cart_products")
    suspend fun deleteAll()

    @Query("UPDATE cart_products SET isSelected = :isSelected WHERE productId = :productId")
    suspend fun updateSelection(productId: String, isSelected: Boolean)

    @Query("UPDATE cart_products SET isSelected = :isSelected")
    suspend fun updateAllSelection(isSelected: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM cart_products WHERE productId = :productId)")
    fun isInCart(productId: String): Flow<Boolean>
}