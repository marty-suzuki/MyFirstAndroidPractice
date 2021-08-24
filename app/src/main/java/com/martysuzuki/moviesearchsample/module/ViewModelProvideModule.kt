package com.martysuzuki.moviesearchsample.module

import com.martysuzuki.viewmodel.search.MovieSearchUiLogicFactoryImpl
import com.martysuzuki.viewmodelinterface.UiLogicFactory
import com.martysuzuki.viewmodelinterface.search.MovieSearchUiLogic
import com.martysuzuki.viewmodelinterface.search.MovieSearchViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module()
@InstallIn(ViewModelComponent::class)
interface ViewModelProvideModule {
    @Binds
    @MovieSearchViewModelFactory
    fun bindMovieSearchUiLogicFactory(
        uiLogicFactory: MovieSearchUiLogicFactoryImpl
    ): UiLogicFactory<MovieSearchUiLogic, Unit>
}