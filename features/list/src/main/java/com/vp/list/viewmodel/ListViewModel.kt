package com.vp.list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vp.coreui.SearchResult
import com.vp.coreui.model.ListItem
import com.vp.list.model.SearchResponse
import com.vp.list.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ListViewModel @Inject constructor(
    private val searchService: SearchService
) : ViewModel() {
    private val liveData = MutableLiveData<SearchResult>()
    private var currentTitle = ""
    private val aggregatedItems: MutableList<ListItem> = ArrayList()
    fun observeMovies(): LiveData<SearchResult> {
        return liveData
    }

    fun searchMoviesByTitle(
        title: String,
        page: Int
    ) {
        if (page == 1 && title != currentTitle) {
            aggregatedItems.clear()
            currentTitle = title
            liveData.value = SearchResult.inProgress()
        }
        searchService.search(title, page).enqueue(object : Callback<SearchResponse?> {
            override fun onResponse(
                call: Call<SearchResponse?>,
                response: Response<SearchResponse?>,
            ) {
                val result = response.body()
                if (result != null) {
                    aggregatedItems.addAll(result.getSearch())
                    liveData.value = SearchResult.success(aggregatedItems, result.totalResults)
                }
            }

            override fun onFailure(
                call: Call<SearchResponse?>,
                t: Throwable,
            ) {
                liveData.value = SearchResult.error()
            }
        })
    }
}


/*
public class ListViewModel extends ViewModel {
    private MutableLiveData<SearchResult> liveData = new MutableLiveData<>();
    private SearchService searchService;

    private String currentTitle = "";
    private List<ListItem> aggregatedItems = new ArrayList<>();

    @Inject
    ListViewModel(@NonNull SearchService searchService) {
        this.searchService = searchService;
    }

    public LiveData<SearchResult> observeMovies() {
        return liveData;
    }

    public void searchMoviesByTitle(@NonNull String title, int page) {
        if (page == 1 && !title.equals(currentTitle)) {
            aggregatedItems.clear();
            currentTitle = title;
            liveData.setValue(SearchResult.inProgress());
        }
        searchService.search(title, page).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {

                SearchResponse result = response.body();

                if (result != null) {
                    aggregatedItems.addAll(result.getSearch());
                    liveData.setValue(SearchResult.success(aggregatedItems, result.totalResults));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                liveData.setValue(SearchResult.error());
            }
        });
    }
}
*/