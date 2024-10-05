package com.ninezero.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ninezero.data.db.converter.CartConverter
import com.ninezero.data.db.converter.SaveConverter
import com.ninezero.data.db.dao.CartDao
import com.ninezero.data.db.dao.SaveDao
import com.ninezero.data.db.entity.CartProductEntity
import com.ninezero.data.db.entity.SavedProductEntity

@Database(
    entities = [SavedProductEntity::class, CartProductEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun saveDao(): SaveDao
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "cream_db"
    }
}