package com.ninezero.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ninezero.data.db.converter.SaveConverter
import com.ninezero.data.db.dao.SaveDao
import com.ninezero.data.db.entity.SavedProductEntity

@Database(entities = [SavedProductEntity::class], version = 1, exportSchema = false)
@TypeConverters(SaveConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun saveDao(): SaveDao

    companion object {
        const val DATABASE_NAME = "cream_db"
    }
}