package com.vp.data.di

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.vp.data.model.MoviesVPDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {
    private val DB_NAME = "vp_db"

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): MoviesVPDatabase {
        return databaseBuilder(
            application.applicationContext,
            MoviesVPDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideFavoriteMovieDao(database: MoviesVPDatabase) = database.favoriteMovieDao()
}