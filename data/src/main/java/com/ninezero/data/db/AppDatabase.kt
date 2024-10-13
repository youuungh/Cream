package com.ninezero.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ninezero.data.db.dao.CartDao
import com.ninezero.data.db.dao.SaveDao
import com.ninezero.data.db.dao.SearchDao
import com.ninezero.data.db.entity.CartProductEntity
import com.ninezero.data.db.entity.SavedProductEntity
import com.ninezero.data.db.entity.SearchHistoryEntity

@Database(
    entities = [
        SavedProductEntity::class,
        CartProductEntity::class,
        SearchHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun saveDao(): SaveDao
    abstract fun cartDao(): CartDao
    abstract fun searchDao(): SearchDao

    companion object {
        const val DATABASE_NAME = "cream_db"
    }
}