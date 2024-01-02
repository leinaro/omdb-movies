package com.vp.favorites.di

import com.vp.favorites.FavoriteListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FavoriteListFragmentModule {

    @ContributesAndroidInjector
    abstract fun bindFavoriteListFragment(): FavoriteListFragment
}