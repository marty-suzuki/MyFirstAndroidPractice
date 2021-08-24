package com.martysuzuki.repositoryinterface.movie

sealed class MovieDetailResult {
    data class Success(val movie: MovieDetail) : MovieDetailResult()
    data class Failure(val exception: Exception): MovieDetailResult()
}