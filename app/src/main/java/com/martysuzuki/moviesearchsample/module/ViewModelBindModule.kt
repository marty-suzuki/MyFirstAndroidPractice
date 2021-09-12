package com.martysuzuki.moviesearchsample.module

import com.martysuzuki.uilogic.search.MovieSearchUiLogicImpl
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.search.MovieSearchUiLogic
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module()
@InstallIn(ViewModelComponent::class)
interface ViewModelBindModule {
    @Binds
    fun bindMovieSearchUiLogicFactory(
        uiLogicFactory: MovieSearchUiLogicImpl.Factory
    ): UiLogicFactory<MovieSearchUiLogic, Unit>
}