package com.martysuzuki.moviesearchsample.module

import com.martysuzuki.viewmodel.detail.MovieDetailUiLogicFactoryImpl
import com.martysuzuki.viewmodelinterface.UiLogicFactory
import com.martysuzuki.viewmodelinterface.detail.MovieDetailUiLogic
import com.martysuzuki.viewmodelinterface.detail.MovieDetailViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
interface FragmentBindModule {
    @Binds
    @MovieDetailViewModelFactory
    fun bindMovieDetailUiLogicFactory(
        uiLogicFactory: MovieDetailUiLogicFactoryImpl
    ): UiLogicFactory<MovieDetailUiLogic, Int>
}

