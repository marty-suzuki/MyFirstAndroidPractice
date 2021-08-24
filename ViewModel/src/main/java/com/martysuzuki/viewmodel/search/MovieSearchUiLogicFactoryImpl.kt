package com.martysuzuki.viewmodel.search

import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.viewmodelinterface.UiLogicFactory
import com.martysuzuki.viewmodelinterface.search.MovieSearchUiLogic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class MovieSearchUiLogicFactoryImpl @Inject constructor(
    private val movieRepository: MovieRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : UiLogicFactory<MovieSearchUiLogic, Unit> {
    override fun create(
        viewModelScope: CoroutineScope,
        dependency: Unit
    ) = MovieSearchUiLogicImpl(
        viewModelScope = viewModelScope,
        movieRepository = movieRepository,
        defaultDispatcher = defaultDispatcher
    )
}
