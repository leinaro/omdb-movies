package com.vp.favorites.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vp.daggeraddons.DaggerViewModelFactory
import com.vp.daggeraddons.ViewModelKey
import com.vp.favorites.FavoriteListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FavoriteListViewModelsModule {

    @Binds
    abstract fun bindDaggerViewModelFactory(daggerViewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteListViewModel::class)
    abstract fun bindFavoriteListViewModel(listViewModel: FavoriteListViewModel): ViewModel
}