package com.vp.list

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vp.coreui.GridPagingScrollListener
import com.vp.coreui.ListAdapter
import com.vp.coreui.ListState
import com.vp.coreui.SearchResult
import com.vp.list.viewmodel.ListViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ListFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var listViewModel: ListViewModel? = null
    private var currentQuery: String = "Interview"

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
        listViewModel = ViewModelProviders.of(this, factory).get(ListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        viewAnimator = view.findViewById(R.id.viewAnimator)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorText)
        mSwipeRefreshLayout = view.findViewById<View>(R.id.swiperefresh) as SwipeRefreshLayout

        if (savedInstanceState != null) {
            currentQuery = savedInstanceState.getString(CURRENT_QUERY, "")
        }

        initBottomNavigation(view)
        initList()
        initSwipeRefreshLayout()
        listViewModel?.observeMovies()
            ?.observe(this.viewLifecycleOwner) { searchResult: SearchResult? ->
                if (searchResult != null) {
                    handleResult(listAdapter, searchResult)
                }
            }
        listViewModel?.searchMoviesByTitle(currentQuery, 1)
        showProgressBar()
    }

    private fun initBottomNavigation(view: View) {
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.favorites) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("app://movies/favorites"))
                intent.setPackage(requireContext().packageName)
                startActivity(intent)
            }
            true
        }
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
        gridPagingScrollListener.setLoadMoreItemsListener(gridListener)
        recyclerView.addOnScrollListener(gridPagingScrollListener)
    }

    private fun showProgressBar() {
        viewAnimator.displayedChild = viewAnimator.indexOfChild(progressBar)
    }

    private fun showList() {
        viewAnimator.displayedChild = viewAnimator.indexOfChild(recyclerView)
        mSwipeRefreshLayout.isRefreshing = false
    }

    private fun showError() {
        viewAnimator.displayedChild = viewAnimator.indexOfChild(errorTextView)
    }

    private fun handleResult(
        listAdapter: ListAdapter,
        searchResult: SearchResult
    ) {
        when (searchResult.listState) {
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
        searchResult: SearchResult
    ) {
        listAdapter.setItems(searchResult.items)
        if (searchResult.totalResult <= listAdapter.itemCount) {
            gridPagingScrollListener.markLastPage(true)
        }
    }

    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener { refresh() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_QUERY, currentQuery)
    }

    fun submitSearchQuery(query: String) {
        currentQuery = query
        listAdapter.clearItems()
        listViewModel?.searchMoviesByTitle(query, 1)
        showProgressBar()
    }

    fun refresh() {
        if (!mSwipeRefreshLayout.isRefreshing) {
            mSwipeRefreshLayout.isRefreshing = true
        }
        listViewModel?.searchMoviesByTitle(currentQuery, 1)
    }

    private var listListener = ListAdapter.OnItemClickListener { imdbID: String ->
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
            "app://movies/details?imdbID=$imdbID"
        )
        )
        startActivity(intent)
    }
    private var gridListener = GridPagingScrollListener.LoadMoreItemsListener { page: Int ->
        gridPagingScrollListener.markLoading(true)
        listViewModel?.searchMoviesByTitle(currentQuery, page)
    }

    companion object {
        const val TAG = "ListFragment"
        private const val CURRENT_QUERY = "current_query"
    }
}