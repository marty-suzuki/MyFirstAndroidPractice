package com.martysuzuki.moviesearchsample.module

import com.martysuzuki.uilogic.detail.MovieDetailUiLogicFactoryImpl
import com.martysuzuki.uilogic.search.MovieSearchUiLogicFactoryImpl
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiLogic
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
        uiLogicFactory: MovieSearchUiLogicFactoryImpl
    ): UiLogicFactory<MovieSearchUiLogic, Unit>

    @Binds
    fun bindMovieDetailUiLogicFactory(
        uiLogicFactory: MovieDetailUiLogicFactoryImpl
    ): UiLogicFactory<MovieDetailUiLogic, Int>
}