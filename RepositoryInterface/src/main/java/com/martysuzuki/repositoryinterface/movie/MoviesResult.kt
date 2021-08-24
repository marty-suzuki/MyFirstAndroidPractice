package com.martysuzuki.repositoryinterface.movie

sealed class MoviesResult {
    data class Success(val items: List<Movie>, val page: Page) : MoviesResult()

    data class Failure(val reason: Reason) : MoviesResult() {
        sealed class Reason {
            object EmptyQuery : Reason()
            object InvalidPage : Reason()
            object Unauthorized : Reason()
            object NotFound : Reason()
            data class Unknown(val exception: Exception) : Reason()
        }
    }
}