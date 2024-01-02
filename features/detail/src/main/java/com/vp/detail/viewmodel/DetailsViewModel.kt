package com.vp.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vp.data.model.FavoriteMovieDao
import com.vp.data.model.FavoriteMovieEntity
import com.vp.detail.DetailActivity
import com.vp.detail.model.MovieDetail
import com.vp.detail.service.DetailService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import javax.security.auth.callback.Callback

class DetailsViewModel @Inject constructor(
    private val detailService: DetailService,
    private val favoriteMovieDao: FavoriteMovieDao,
) : ViewModel() {

    private lateinit var movieId: String

    private val details: MutableLiveData<MovieDetail> = MutableLiveData()
    private val title: MutableLiveData<String> = MutableLiveData()
    private val loadingState: MutableLiveData<LoadingState> = MutableLiveData()
    private val isFavorite: MutableLiveData<Boolean> = MutableLiveData()
    fun title(): LiveData<String> = title

    fun details(): LiveData<MovieDetail> = details

    fun state(): LiveData<LoadingState> = loadingState

    fun favorited(): LiveData<Boolean> = isFavorite

    fun fetchDetails() {
        loadingState.value = LoadingState.IN_PROGRESS
        movieId = DetailActivity.queryProvider.getMovieId()
        detailService.getMovie(movieId).enqueue(object : Callback, retrofit2.Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>?, response: Response<MovieDetail>?) {
                details.postValue(response?.body())

                response?.body()?.title?.let {
                    title.postValue(it)
                }

                loadingState.value = LoadingState.LOADED
            }

            override fun onFailure(call: Call<MovieDetail>?, t: Throwable?) {
                details.postValue(null)
                loadingState.value = LoadingState.ERROR
            }
        })

        // TODO: use viewModelScope
        GlobalScope.launch {
            val favoriteMovie = favoriteMovieDao.getByMovieId(movieId)
            isFavorite.postValue(favoriteMovie != null)
        }
    }

    fun toggleFavorite() {
        if (isFavorite.value == false) {
            addFavorite()
        } else {
            removeFavorite()
        }
    }

    private fun removeFavorite() {
        // TODO: use viewModelScope
        isFavorite.value = false
        GlobalScope.launch {
            favoriteMovieDao.deleteByUserId(movieId)
        }
    }

    private fun addFavorite() {
        // TODO: use viewModelScope
        isFavorite.value = true
        GlobalScope.launch {
            val favoriteMovieEntity = FavoriteMovieEntity(
                imdbID = movieId,
                title = details.value?.title ?: "",
                year = details.value?.year ?: "",
                poster = details.value?.poster ?: "",
            )
            favoriteMovieDao.insert(favoriteMovieEntity)
        }
    }

    enum class LoadingState {
        IN_PROGRESS, LOADED, ERROR
    }
}