package com.vp.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vp.coreui.SearchResult
import com.vp.coreui.model.ListItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteListViewModel : ViewModel() {
    private val liveData = MutableLiveData<SearchResult>()

    private val aggregatedItems: MutableList<ListItem> = ArrayList()

    fun observeMovies(): LiveData<SearchResult?>? {
        return liveData
    }

    fun getFavoriteMovies(
        page: Int,
    ) {
        if (page == 1) {
            aggregatedItems.clear()
            liveData.setValue(SearchResult.inProgress())
        }
    }
}