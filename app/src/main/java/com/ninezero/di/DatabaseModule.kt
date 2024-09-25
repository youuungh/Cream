package com.ninezero.di

import android.content.Context
import androidx.room.Room
import com.ninezero.data.db.AppDatabase
import com.ninezero.data.db.dao.SaveDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideSaveDao(database: AppDatabase): SaveDao {
        return database.saveDao()
    }
}