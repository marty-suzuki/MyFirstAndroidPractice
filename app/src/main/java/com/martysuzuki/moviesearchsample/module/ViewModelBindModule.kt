package com.martysuzuki.moviesearchsample.module

import com.github.marty_suzuki.unio.UnioFactory
import com.martysuzuki.uilogic.search.MovieSearchUnio
import com.martysuzuki.uilogicinterface.search.MovieSearchInput
import com.martysuzuki.uilogicinterface.search.MovieSearchOutput
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module()
@InstallIn(ViewModelComponent::class)
interface ViewModelBindModule {
    @Binds
    fun bindMovieSearchUnioFactory(
        unioFactory: MovieSearchUnio.Factory
    ): UnioFactory<MovieSearchInput, MovieSearchOutput>
}