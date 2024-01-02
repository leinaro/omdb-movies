package com.vp.favorites

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewAnimator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vp.coreui.GridPagingScrollListener
import com.vp.coreui.ListAdapter
import com.vp.coreui.ListState
import com.vp.coreui.SearchResult
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FavoriteListFragment : Fragment() {

    companion object {
        const val TAG = "FavoriteListFragment"
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewModel: FavoriteListViewModel

    private lateinit var gridPagingScrollListener: GridPagingScrollListener
    private lateinit var listAdapter: ListAdapter
    private lateinit var viewAnimator: ViewAnimator
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get<FavoriteListViewModel>(FavoriteListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_list, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        viewAnimator = view.findViewById(R.id.viewAnimator)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorText)
        mSwipeRefreshLayout = view.findViewById<View>(R.id.swiperefresh) as SwipeRefreshLayout

        initList()
        initSwipeRefreshLayout()

        viewModel.observeMovies().observe(this.viewLifecycleOwner,
            Observer<SearchResult?> { searchResult: SearchResult? ->
                searchResult?.let { handleResult(listAdapter, it) }
            })
        viewModel.getFavoriteMovies(1)

        showProgressBar()
    }

    private fun initList() {
        listAdapter = ListAdapter()
        listAdapter.setOnItemClickListener(listListener)
        recyclerView.adapter = listAdapter
        recyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(
            context,
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        )
        recyclerView.layoutManager = layoutManager

        // Pagination
        gridPagingScrollListener = GridPagingScrollListener(layoutManager)
       // gridPagingScrollListener.setLoadMoreItemsListener(gridListener)
        recyclerView.addOnScrollListener(gridPagingScrollListener)
    }

    private fun showProgressBar() {
        viewAnimator.displayedChild = viewAnimator.indexOfChild(progressBar)
    }

    private fun showList() {
        viewAnimator.displayedChild = viewAnimator.indexOfChild(recyclerView) ?: 1
        mSwipeRefreshLayout.isRefreshing = false
    }

    private fun showError() {
        viewAnimator.displayedChild = viewAnimator.indexOfChild(errorTextView)
    }


    private fun handleResult(
        listAdapter: ListAdapter,
        searchResult: SearchResult,
    ) {
        when (searchResult.getListState()) {
            ListState.LOADED -> {
                setItemsData(listAdapter, searchResult)
                showList()
            }

            ListState.IN_PROGRESS -> {
                showProgressBar()
            }

            else -> {
                showError()
            }
        }
        gridPagingScrollListener.markLoading(false)
    }

    private fun setItemsData(
        listAdapter: ListAdapter,
        searchResult: SearchResult,
    ) {
        listAdapter.setItems(searchResult.items)
        if (searchResult.totalResult <= listAdapter.itemCount) {
            gridPagingScrollListener.markLastPage(true)
        }
    }

    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener { refresh() }
    }

    private var listListener = ListAdapter.OnItemClickListener { imdbID: String ->
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse("app://movies/details?imdbID=$imdbID")
        )
        startActivity(intent)
    }

    fun refresh() {
        if (!mSwipeRefreshLayout.isRefreshing) {
            mSwipeRefreshLayout.isRefreshing = true
        }
        viewModel.getFavoriteMovies(1)
    }
}