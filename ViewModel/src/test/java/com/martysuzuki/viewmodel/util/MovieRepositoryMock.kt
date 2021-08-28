package com.martysuzuki.viewmodel.util

import com.martysuzuki.repositoryinterface.movie.MovieDetailResult
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.repositoryinterface.movie.MoviesResult
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class MovieRepositoryMock: MovieRepository {
    val fetchMoviesSpy = SuspendSpy<MoviesResult, Pair<String, Int>>("fetchMoviesResult is null")
    override suspend fun fetchMovies(query: String, page: Int) = fetchMoviesSpy.respond(Pair(query, page))

    val fetchMovieDetailSpy = SuspendSpy<MovieDetailResult, Int>("fetchMovieDetailResult is null")
    override suspend fun fetchMovieDetail(id: Int) = fetchMovieDetailSpy.respond(id)
}