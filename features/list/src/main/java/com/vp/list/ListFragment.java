package com.vp.list;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.vp.coreui.GridPagingScrollListener;
import com.vp.coreui.ListAdapter;
import com.vp.coreui.SearchResult;
import com.vp.list.viewmodel.ListViewModel;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class ListFragment extends Fragment {
    public static final String TAG = "ListFragment";
    private static final String CURRENT_QUERY = "current_query";

    @Inject
    ViewModelProvider.Factory factory;

    private ListViewModel listViewModel;
    private GridPagingScrollListener gridPagingScrollListener;
    private ListAdapter listAdapter;
    private ViewAnimator viewAnimator;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private String currentQuery = "Interview";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
        listViewModel = ViewModelProviders.of(this, factory).get(ListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        viewAnimator = view.findViewById(R.id.viewAnimator);
        progressBar = view.findViewById(R.id.progressBar);
        errorTextView = view.findViewById(R.id.errorText);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        if (savedInstanceState != null) {
            currentQuery = savedInstanceState.getString(CURRENT_QUERY);
        }

        initBottomNavigation(view);
        initList();
        initSwipeRefreshLayout();
        listViewModel.observeMovies().observe(this.getViewLifecycleOwner(), searchResult -> {
            if (searchResult != null) {
                handleResult(listAdapter, searchResult);
            }
        });
        listViewModel.searchMoviesByTitle(currentQuery, 1);
        showProgressBar();
    }

    private void initBottomNavigation(@NonNull View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.favorites) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("app://movies/favorites"));
                intent.setPackage(requireContext().getPackageName());
                startActivity(intent);
            }
            return true;
        });
    }

    private void initList() {
        listAdapter = new ListAdapter();
        listAdapter.setOnItemClickListener(listListener);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3);
        recyclerView.setLayoutManager(layoutManager);

        // Pagination
        gridPagingScrollListener = new GridPagingScrollListener(layoutManager);
        gridPagingScrollListener.setLoadMoreItemsListener(gridListener);
        recyclerView.addOnScrollListener(gridPagingScrollListener);
    }

    private void showProgressBar() {
        viewAnimator.setDisplayedChild(viewAnimator.indexOfChild(progressBar));
    }

    private void showList() {
        viewAnimator.setDisplayedChild(viewAnimator.indexOfChild(recyclerView));
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showError() {
        viewAnimator.setDisplayedChild(viewAnimator.indexOfChild(errorTextView));
    }

    private void handleResult(@NonNull ListAdapter listAdapter, @NonNull SearchResult searchResult) {
        switch (searchResult.getListState()) {
            case LOADED: {
                setItemsData(listAdapter, searchResult);
                showList();
                break;
            }
            case IN_PROGRESS: {
                showProgressBar();
                break;
            }
            default: {
                showError();
            }
        }
        gridPagingScrollListener.markLoading(false);
    }

    private void setItemsData(@NonNull ListAdapter listAdapter, @NonNull SearchResult searchResult) {
        listAdapter.setItems(searchResult.getItems());

        if (searchResult.getTotalResult() <= listAdapter.getItemCount()) {
            gridPagingScrollListener.markLastPage(true);
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refresh();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_QUERY, currentQuery);
    }

    public void submitSearchQuery(@NonNull final String query) {
        currentQuery = query;
        listAdapter.clearItems();
        listViewModel.searchMoviesByTitle(query, 1);
        showProgressBar();
    }

    public void refresh(){
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        listViewModel.searchMoviesByTitle(currentQuery, 1);
    }

    ListAdapter.OnItemClickListener listListener = imdbID -> {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("app://movies/details?imdbID=" + imdbID));
        startActivity(intent);
    };

    GridPagingScrollListener.LoadMoreItemsListener gridListener = page -> {
        gridPagingScrollListener.markLoading(true);
        listViewModel.searchMoviesByTitle(currentQuery, page);
    };
}
