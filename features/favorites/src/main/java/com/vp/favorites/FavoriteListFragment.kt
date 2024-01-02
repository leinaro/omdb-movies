package com.vp.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewAnimator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.vp.coreui.GridPagingScrollListener
import com.vp.coreui.ListAdapter
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FavoriteListFragment : Fragment() {

    companion object {
        const val TAG = "FavoriteListFragment"
        fun newInstance() = FavoriteListFragment()
    }

//    @Inject
//    var factory: ViewModelProvider.Factory? = null

    lateinit var viewModel: FavoriteListViewModel

    private val gridPagingScrollListener: GridPagingScrollListener? = null
    private val listAdapter: ListAdapter? = null
    private var viewAnimator: ViewAnimator? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var errorTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
//        viewModel = ViewModelProviders.of(this, factory).get<ListViewModel>(ListViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteListViewModel::class.java)
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
    }

    private fun showProgressBar() {
        viewAnimator?.displayedChild = viewAnimator!!.indexOfChild(progressBar)
    }

    private fun showList() {
        viewAnimator?.displayedChild = viewAnimator?.indexOfChild(recyclerView) ?: 1
    }

    private fun showError() {
        viewAnimator?.displayedChild = viewAnimator!!.indexOfChild(errorTextView)
    }

    /*
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
    }*/
}