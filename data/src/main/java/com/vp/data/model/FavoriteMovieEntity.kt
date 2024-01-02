package com.vp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movie")
data class FavoriteMovieEntity(
    @PrimaryKey val imdbID: String,
    @ColumnInfo("Title") val title: String,
    @ColumnInfo("Year") val year: String,
    @ColumnInfo("Poster") val poster: String,
)