package com.martysuzuki.uilogic.detail

import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiLogic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class MovieDetailUiLogicFactoryImpl @Inject constructor(
    private val movieRepository: MovieRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : UiLogicFactory<MovieDetailUiLogic, Int> {
    override fun create(
        viewModelScope: CoroutineScope,
        dependency: Int
    ) = MovieDetailUiLogicImpl(
        viewModelScope = viewModelScope,
        movieRepository = movieRepository,
        defaultDispatcher = defaultDispatcher,
        movieId = dependency
    )
}
