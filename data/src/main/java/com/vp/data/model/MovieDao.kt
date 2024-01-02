package com.vp.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movie")
    fun getAll(): List<FavoriteMovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg favoriteMovieEntity: FavoriteMovieEntity)

    @Delete
    fun delete(favoriteMovieEntity: FavoriteMovieEntity)

    @Query("SELECT * FROM favorite_movie WHERE imdbID = :imdbID")
    suspend fun getByMovieId(imdbID: String) : FavoriteMovieEntity?

    @Query("DELETE FROM favorite_movie WHERE imdbID = :imdbID")
    fun deleteByUserId(imdbID: String)
}

