package com.vp.data.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovieEntity::class], version = 1)
abstract class MoviesVPDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}