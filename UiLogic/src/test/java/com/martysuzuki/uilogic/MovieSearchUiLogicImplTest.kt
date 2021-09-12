package com.martysuzuki.uilogic

import com.martysuzuki.repositoryinterface.movie.*
import com.martysuzuki.uilogic.search.MovieSearchUiLogicImpl
import com.martysuzuki.uilogic.util.MovieRepositoryMock
import com.martysuzuki.uilogicinterface.search.MovieSearchItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieSearchUiLogicImplTest {

    private class Dependency {
        val movieRepository = MovieRepositoryMock()
        val coroutineScope = TestCoroutineScope()
        val coroutineDispatcher = TestCoroutineDispatcher()

        val testTarget = MovieSearchUiLogicImpl(
            movieRepository,
            coroutineDispatcher,
            coroutineScope,
            Unit
        )
    }

    @Test
    fun test_search_sequence_isSuccess() = runBlockingTest {
        val dependency = Dependency()

        val query = "test-query"
        val movie = Movie(id = 1, posterPath = "test-post", title = "test-title")
        val movieItem = MovieSearchItem.Movie(movie.id, movie.title, movie.posterPath)
        val matchParentLoadingItem = MovieSearchItem.Loading(MovieSearchItem.Loading.Style.MATCH_PARENT)
        val wrapContentLoadingItem = MovieSearchItem.Loading(MovieSearchItem.Loading.Style.WRAP_CONTENT)

        var actualItems: List<MovieSearchItem>? = null
        val job1 = dependency.testTarget.update
            .onEach { actualItems = it.items }
            .launchIn(this)

        var actualNavigateToMovieDetail: Int? = null
        val job2 = dependency.testTarget.navigateToMovieDetail
            .onEach { actualNavigateToMovieDetail = it }
            .launchIn(this)

        val clear: () -> Unit = {
            dependency.movieRepository.fetchMoviesSpy.clear()
            actualItems = null
            actualNavigateToMovieDetail = null
        }

        // 1st TEST immediately after when search method called
        dependency.testTarget.search(query)
        assertEquals(listOf(matchParentLoadingItem), actualItems)
        assertEquals(Pair(query, 1), dependency.movieRepository.fetchMoviesSpy.params)
        clear()

        // 2nd TEST when API response received
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Success(listOf(movie), Page.Next(2))
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(listOf(movieItem, wrapContentLoadingItem), actualItems)
        clear()

        // 3rd TEST when fetch more pages
        dependency.testTarget.reachBottom()
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Success(listOf(movie), Page.Finish)
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(Pair(query, 2), dependency.movieRepository.fetchMoviesSpy.params)
        assertEquals(listOf(movieItem, movieItem), actualItems)
        clear()

        // 4th If page is Pages.Finish, no more fetch API called
        dependency.testTarget.reachBottom()
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertNull(dependency.movieRepository.fetchMoviesSpy.params)
        assertNull(actualItems)
        clear()

        // 5th TEST when onItemClicked called, navigateToMovieDetail emits movieId
        dependency.testTarget.onItemClicked(0)
        assertEquals(movieItem.id, actualNavigateToMovieDetail)
        clear()

        job1.cancel()
        job2.cancel()
    }

    @Test
    fun test_search_isSuccess_but_noResult() = runBlockingTest {
        val dependency = Dependency()

        val query = "test-query"
        val matchParentLoadingItem = MovieSearchItem.Loading(MovieSearchItem.Loading.Style.MATCH_PARENT)

        var actualItems: List<MovieSearchItem>? = null
        val job = dependency.testTarget.update
            .onEach { actualItems = it.items }
            .launchIn(this)

        // 1st TEST immediately after when search method called
        dependency.testTarget.search(query)
        assertEquals(listOf(matchParentLoadingItem), actualItems)

        // 2nd TEST when API response received
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Success(emptyList(), Page.Finish)
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(Pair(query, 1), dependency.movieRepository.fetchMoviesSpy.params)
        assertEquals(listOf(MovieSearchItem.NoResults), actualItems)

        job.cancel()
    }

    @Test
    fun test_search_load_more_prevented_when_searching() = runBlockingTest {
        val dependency = Dependency()

        val query = "test-query"
        val movie = Movie(id = 1, posterPath = "test-post", title = "test-title")

        // 1st TEST immediately after when search method called
        dependency.testTarget.search(query)
        dependency.testTarget.reachBottom()
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Success(emptyList(), Page.Finish)
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(Pair(query, 1), dependency.movieRepository.fetchMoviesSpy.params)
        assertEquals(1, dependency.movieRepository.fetchMoviesSpy.calledCount)

        dependency.testTarget.search(query)
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Success(listOf(movie), Page.Next(2))
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(Pair(query, 1), dependency.movieRepository.fetchMoviesSpy.params)
        assertEquals(2, dependency.movieRepository.fetchMoviesSpy.calledCount)

        // 2nd TEST immediately after when load more called
        dependency.testTarget.reachBottom()
        dependency.testTarget.reachBottom()
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Success(listOf(movie), Page.Finish)
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(Pair(query, 2), dependency.movieRepository.fetchMoviesSpy.params)
        assertEquals(3, dependency.movieRepository.fetchMoviesSpy.calledCount)
    }

    @Test
    fun test_search_isUnauthorized() = runBlockingTest {
        val dependency = Dependency()

        val query = "test-query"
        val matchParentLoadingItem = MovieSearchItem.Loading(MovieSearchItem.Loading.Style.MATCH_PARENT)

        var actualItems: List<MovieSearchItem>? = null
        val job1 = dependency.testTarget.update
            .onEach { actualItems = it.items }
            .launchIn(this)

        var actualShowUnauthorizedDialog: Unit? = null
        val job2 = dependency.testTarget.showUnauthorizedDialog
            .onEach { actualShowUnauthorizedDialog = it }
            .launchIn(this)

        dependency.testTarget.search(query)

        // 1st TEST immediately after when search method called
        dependency.testTarget.search(query)
        assertEquals(listOf(matchParentLoadingItem), actualItems)

        // 2nd TEST when Unauthorized response received
        dependency.movieRepository.fetchMoviesSpy.result = MoviesResult.Failure(MoviesResult.Failure.Reason.Unauthorized)
        dependency.movieRepository.fetchMoviesSpy.advanceTimeBy(100)
        assertEquals(Pair(query, 1), dependency.movieRepository.fetchMoviesSpy.params)
        assertEquals(emptyList<MovieSearchItem>(), actualItems)
        assertNotNull(actualShowUnauthorizedDialog)

        job1.cancel()
        job2.cancel()
    }
}