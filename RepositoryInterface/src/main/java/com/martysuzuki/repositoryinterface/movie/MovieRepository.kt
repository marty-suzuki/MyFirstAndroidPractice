package com.martysuzuki.repositoryinterface.movie

interface MovieRepository {
    suspend fun fetchMovies(
        query: String,
        page: Int
    ): MoviesResult

    suspend fun fetchMovieDetail(id: Int): MovieDetailResult
}