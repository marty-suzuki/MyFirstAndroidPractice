package com.martysuzuki.uilogic

import com.martysuzuki.repositoryinterface.movie.Movie
import com.martysuzuki.repositoryinterface.movie.MovieDetail
import com.martysuzuki.repositoryinterface.movie.MovieDetailResult
import com.martysuzuki.uilogic.detail.MovieDetailUiLogicImpl
import com.martysuzuki.uilogic.util.MovieRepositoryMock
import com.martysuzuki.uilogicinterface.detail.MovieDetailItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieDetailUiLogicImplTest {

    private class Dependency(movieId: Int = 100) {
        val movieRepository = MovieRepositoryMock()
        val coroutineDispatcher = TestCoroutineDispatcher()
        val coroutineScope = TestCoroutineScope()

        val testTarget = MovieDetailUiLogicImpl(
            movieRepository = movieRepository,
            defaultDispatcher = coroutineDispatcher,
            viewModelScope = coroutineScope,
            movieId = movieId
        )
    }

    @Test
    fun test_fetch_movie_detail_sequence_isSuccess() = runBlockingTest {
        val movieId = 100
        val dependency = Dependency(movieId)

        val expectedBackdrop = "test-backdrop"
        val expectedOverview = "test-overview"
        val expectedCast = "test-cast"
        val expectedRecommendationId = 10
        val expectedMatchParentLoadingItem = MovieDetailItem.Loading(MovieDetailItem.Loading.Style.MATCH_PARENT)

        val recommendation = Movie(
            id = expectedRecommendationId,
            title = "test-title",
            posterPath = "test-poster"
        )
        val movie = MovieDetail(
            id = 1,
            title = "test-title",
            overview = expectedOverview,
            releaseDate = "2021",
            runtime = 60,
            backdrops = listOf(expectedBackdrop),
            casts = listOf(expectedCast),
            recommendations = listOf(recommendation)
        )

        var actualItems: List<MovieDetailItem>? = null
        val job1 = launch {
            dependency.testTarget.update.collect { actualItems = it.items }
        }

        var actualNavigateToMovieDetail: Int? = null
        val job2 = launch {
            dependency.testTarget.navigateToMovieDetail.collect { actualNavigateToMovieDetail = it }
        }

        // 1st TEST immediately after when fetch method called
        assertEquals(movieId, dependency.movieRepository.fetchMovieDetailSpy.params)
        assertEquals(listOf(expectedMatchParentLoadingItem), actualItems)

        // 2nd TEST API response received
        dependency.movieRepository.fetchMovieDetailSpy.result = MovieDetailResult.Success(movie)
        dependency.movieRepository.fetchMovieDetailSpy.advanceTimeBy(100)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Image(expectedBackdrop)),
                MovieDetailItem.Title(movie.title, movie.releaseDate),
                MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW),
                MovieDetailItem.Overview(expectedOverview, false),
                MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.CASTS),
                MovieDetailItem.Cast(expectedCast),
                MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.RECOMMENDATIONS),
                MovieDetailItem.Recommendation(recommendation.id, recommendation.title, recommendation.posterPath)
            ),
            actualItems
        )

        // 3rd TEST click recommendation index
        val recommendationIndex = actualItems?.indexOfFirst { it is MovieDetailItem.Recommendation }
        if (recommendationIndex == null) {
            fail("index must not be null")
        } else {
            dependency.testTarget.onItemClicked(recommendationIndex)
            assertEquals(expectedRecommendationId, actualNavigateToMovieDetail)
        }

        // 4th TEST click overview index, but items not changed
        val overviewIndex = actualItems?.indexOfFirst { it is MovieDetailItem.Overview }
        if (overviewIndex == null) {
            fail("index must not be null")
        } else {
            dependency.testTarget.onItemClicked(overviewIndex)
            assertEquals(
                listOf(
                    MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Image(expectedBackdrop)),
                    MovieDetailItem.Title(movie.title, movie.releaseDate),
                    MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW),
                    MovieDetailItem.Overview(expectedOverview, false),
                    MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.CASTS),
                    MovieDetailItem.Cast(expectedCast),
                    MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.RECOMMENDATIONS),
                    MovieDetailItem.Recommendation(recommendation.id, recommendation.title, recommendation.posterPath)
                ),
                actualItems
            )
        }

        job1.cancel()
        job2.cancel()
    }

    @Test
    fun test_fetch_movie_detail_isSuccess_but_empty() = runBlockingTest {
        val movieId = 100
        val dependency = Dependency(movieId)

        val movie = MovieDetail(
            id = 1,
            title = "test-title",
            overview = null,
            releaseDate = "2021",
            runtime = 60,
            backdrops = emptyList(),
            casts = emptyList(),
            recommendations = emptyList()
        )

        var actualItems: List<MovieDetailItem>? = null
        val job = launch {
            dependency.testTarget.update.collect { actualItems = it.items }
        }

        dependency.movieRepository.fetchMovieDetailSpy.result = MovieDetailResult.Success(movie)
        dependency.movieRepository.fetchMovieDetailSpy.advanceTimeBy(100)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Empty),
                MovieDetailItem.Title(movie.title, movie.releaseDate)
            ),
            actualItems
        )

        job.cancel()
    }

    @Test
    fun test_long_overview_expanding() = runBlockingTest {
        val movieId = 100
        val dependency = Dependency(movieId)
        val expectedFullOverview = "A".repeat(500)
        val expectedTruncatedOverview = expectedFullOverview.substring(0, 400)

        val movie = MovieDetail(
            id = 1,
            title = "test-title",
            overview = expectedFullOverview,
            releaseDate = "2021",
            runtime = 60,
            backdrops = emptyList(),
            casts = emptyList(),
            recommendations = emptyList()
        )

        var actualItems: List<MovieDetailItem>? = null
        val job = launch {
            dependency.testTarget.update.collect { actualItems = it.items }
        }

        // 1st TEST immediately after when fetch method called
        dependency.movieRepository.fetchMovieDetailSpy.result = MovieDetailResult.Success(movie)
        dependency.movieRepository.fetchMovieDetailSpy.advanceTimeBy(100)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Empty),
                MovieDetailItem.Title(movie.title, movie.releaseDate),
                MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW),
                MovieDetailItem.Overview(expectedTruncatedOverview, true)
            ),
            actualItems
        )

        val index = actualItems?.indexOfFirst { it is MovieDetailItem.Overview }
        if (index == null) {
            fail("index must not be null")
        } else {
            // 2nd TEST when click overview index, text is full and visible is false
            dependency.testTarget.onItemClicked(index)
            assertEquals(
                listOf(
                    MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Empty),
                    MovieDetailItem.Title(movie.title, movie.releaseDate),
                    MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW),
                    MovieDetailItem.Overview(expectedFullOverview, false)
                ),
                actualItems
            )

            // 3rd TEST when click overview index, text is truncated and visible is true
            dependency.testTarget.onItemClicked(index)
            assertEquals(
                listOf(
                    MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Empty),
                    MovieDetailItem.Title(movie.title, movie.releaseDate),
                    MovieDetailItem.SectionTitleHeader(MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW),
                    MovieDetailItem.Overview(expectedTruncatedOverview, true)
                ),
                actualItems
            )
        }

        job.cancel()
    }

    @Test
    fun test_backdrops_change() = runBlockingTest {
        val movieId = 100
        val dependency = Dependency(movieId)
        val expectedBackdrop1 = "test-backdrop-1"
        val expectedBackdrop2 = "test-backdrop-2"

        val movie = MovieDetail(
            id = 1,
            title = "test-title",
            overview = null,
            releaseDate = "2021",
            runtime = 60,
            backdrops = listOf(expectedBackdrop1, expectedBackdrop2),
            casts = emptyList(),
            recommendations = emptyList()
        )

        var actualItems: List<MovieDetailItem>? = null
        val job = launch {
            dependency.testTarget.update.collect { actualItems = it.items }
        }

        // 1st TEST immediately after when fetch method called
        dependency.movieRepository.fetchMovieDetailSpy.result = MovieDetailResult.Success(movie)
        dependency.movieRepository.fetchMovieDetailSpy.advanceTimeBy(100)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Image(expectedBackdrop1)),
                MovieDetailItem.Title(movie.title, movie.releaseDate)
            ),
            actualItems
        )

        // 2nd TEST immediately after when onViewCreated method called
        dependency.testTarget.onViewCreated()
        dependency.coroutineScope.advanceTimeBy(5000L)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Image(expectedBackdrop2)),
                MovieDetailItem.Title(movie.title, movie.releaseDate)
            ),
            actualItems
        )

        // 3rd TEST when 5000L millis passed, backdrop changed
        dependency.coroutineScope.advanceTimeBy(5000L)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Image(expectedBackdrop1)),
                MovieDetailItem.Title(movie.title, movie.releaseDate)
            ),
            actualItems
        )

        // 4th TEST after onDestroyView called, backdrop doesn't be changed
        dependency.testTarget.onDestroyView()
        dependency.coroutineScope.advanceTimeBy(5000L)
        assertEquals(
            listOf(
                MovieDetailItem.Thumbnail(MovieDetailItem.Thumbnail.Item.Image(expectedBackdrop1)),
                MovieDetailItem.Title(movie.title, movie.releaseDate)
            ),
            actualItems
        )

        job.cancel()
    }
}