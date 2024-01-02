package com.vp.favorites.di

import com.vp.favorites.FavoriteActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FavoriteListActivityModule {

    @ContributesAndroidInjector(modules = [FavoriteListFragmentModule::class, FavoriteListViewModelsModule::class])
    abstract fun bindFavoriteListActivity(): FavoriteActivity
}