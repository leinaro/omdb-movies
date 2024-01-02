package com.vp.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vp.coreui.SearchResult
import com.vp.coreui.model.ListItem
import com.vp.data.model.FavoriteMovieDao
import com.vp.data.model.FavoriteMovieEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteListViewModel @Inject constructor(
    private val favoriteMovieDao: FavoriteMovieDao,
) : ViewModel() {
    private val liveData = MutableLiveData<SearchResult>()

    private val aggregatedItems: MutableList<ListItem> = ArrayList()

    fun observeMovies(): LiveData<SearchResult?> {
        return liveData
    }

    fun getFavoriteMovies(
        page: Int,
    ) {
        if (page == 1) {
            aggregatedItems.clear()
            liveData.setValue(SearchResult.inProgress())
        }
        // TODO: use viewModelScope
        GlobalScope.launch {
            val favoriteMovies = favoriteMovieDao.getAll()
            aggregatedItems.addAll(favoriteMovies.map {  it.toListItem()})
            liveData.postValue(SearchResult.success(aggregatedItems, aggregatedItems.size))

        }
    }

    fun FavoriteMovieEntity.toListItem(): ListItem {
        return ListItem(
            title = title,
            year = year,
            poster = poster,
            imdbID = imdbID,
        )
    }
}