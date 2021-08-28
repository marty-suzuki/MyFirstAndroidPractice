package com.martysuzuki.repository

import com.martysuzuki.remotedatasourceinterface.TheMovieDatabaseService
import com.martysuzuki.repository.movie.MovieRepositoryImpl
import com.martysuzuki.repositoryinterface.movie.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.lang.IllegalArgumentException

@ExperimentalCoroutinesApi
class MovieRepositoryImplTest {

    private class ServiceMock: TheMovieDatabaseService {
        var moviesResponse: Result<TheMovieDatabaseService.ListResponse<TheMovieDatabaseService.Movie>>? = null
        override suspend fun movies(query: String, page: Int) = moviesResponse
            ?.getOrThrow()
            ?: throw IllegalArgumentException("moviesResponse is null")

        var movieDetailResponse: Result<TheMovieDatabaseService.MovieDetail>? = null
        override suspend fun movieDetail(movieId: Int) = movieDetailResponse
            ?.getOrThrow()
            ?: throw IllegalArgumentException("movieDetailResponse is null")
    }

    @Test
    fun test_fetchMovies_isSuccess_pageNext() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)
        val movie = TheMovieDatabaseService.Movie(
            id = 1,
            title = "test",
            poster_path = null
        )
        val response = TheMovieDatabaseService.ListResponse(
            page = 1,
            results = listOf(movie),
            total_results = 1,
            total_pages = 2
        )
        val expectedItem = Movie(
            id = 1,
            posterPath = null,
            title = "test"
        )

        runBlockingTest {
            service.moviesResponse = Result.success(response)

            when (val result = repository.fetchMovies("test-query", 1)) {
                is MoviesResult.Success -> {
                    assertEquals(expectedItem, result.items.firstOrNull())
                    assertEquals(Page.Next(2), result.page)
                }
                is MoviesResult.Failure -> {
                    fail("Response mut be MoviesResult.Success, but it is $result")
                }
            }
        }
    }

    @Test
    fun test_fetchMovies_isSuccess_pageFinished() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)
        val movie = TheMovieDatabaseService.Movie(
            id = 1,
            title = "test",
            poster_path = null
        )
        val response = TheMovieDatabaseService.ListResponse(
            page = 1,
            results = listOf(movie),
            total_results = 1,
            total_pages = 1
        )
        val expectedItem = Movie(
            id = 1,
            posterPath = null,
            title = "test"
        )

        runBlockingTest {
            service.moviesResponse = Result.success(response)

            when (val result = repository.fetchMovies("test-query", 1)) {
                is MoviesResult.Success -> {
                    assertEquals(expectedItem, result.items.firstOrNull())
                    assertEquals(Page.Finish, result.page)
                }
                is MoviesResult.Failure -> {
                    fail("Response mut be MoviesResult.Success, but it is $result")
                }
            }
        }
    }

    @Test
    fun test_fetchMovies_isEmptyQuery() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        runBlockingTest {
            when (val result = repository.fetchMovies("", 1)) {
                is MoviesResult.Success -> {
                    fail("Response mut be MoviesResult.Failure.Reason.EmptyQuery, but it is $result")
                }
                is MoviesResult.Failure -> {
                    assertEquals(MoviesResult.Failure.Reason.EmptyQuery, result.reason)
                }
            }
        }
    }

    @Test
    fun test_fetchMovies_isInvalidPage() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        runBlockingTest {
            when (val result = repository.fetchMovies("test-query", 0)) {
                is MoviesResult.Success -> {
                    fail("Response mut be MoviesResult.Failure.Reason.InvalidPage, but it is $result")
                }
                is MoviesResult.Failure -> {
                    assertEquals(MoviesResult.Failure.Reason.InvalidPage, result.reason)
                }
            }
        }
    }

    @Test
    fun test_fetchMovies_isUnauthorized() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        runBlockingTest {
            val body = ResponseBody.create(null, "")
            val response: Response<TheMovieDatabaseService.ListResponse<TheMovieDatabaseService.Movie>> = Response.error(401, body)
            service.moviesResponse = Result.failure(retrofit2.HttpException(response))

            when (val result = repository.fetchMovies("test-query", 1)) {
                is MoviesResult.Success -> {
                    fail("Response mut be MoviesResult.Failure.Reason.Unauthorized, but it is $result")
                }
                is MoviesResult.Failure -> {
                    assertEquals(MoviesResult.Failure.Reason.Unauthorized, result.reason)
                }
            }
        }
    }

    @Test
    fun test_fetchMovies_isNotFound() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        runBlockingTest {
            val body = ResponseBody.create(null, "")
            val response: Response<TheMovieDatabaseService.ListResponse<TheMovieDatabaseService.Movie>> = Response.error(404, body)
            service.moviesResponse = Result.failure(retrofit2.HttpException(response))

            when (val result = repository.fetchMovies("test-query", 1)) {
                is MoviesResult.Success -> {
                    fail("Response mut be MoviesResult.Failure.Reason.NotFound, but it is $result")
                }
                is MoviesResult.Failure -> {
                    assertEquals(MoviesResult.Failure.Reason.NotFound, result.reason)
                }
            }
        }
    }

    @Test
    fun test_fetchMovies_isUnknown() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        runBlockingTest {
            service.moviesResponse = Result.failure(IllegalAccessException())

            when (val result = repository.fetchMovies("test-query", 1)) {
                is MoviesResult.Success -> {
                    fail("Response mut be MoviesResult.Failure.Reason.Unknown, but it is $result")
                }
                is MoviesResult.Failure -> {
                    assertEquals(MoviesResult.Failure.Reason.Unknown(IllegalAccessException()).javaClass, result.reason.javaClass)
                }
            }
        }
    }

    @Test
    fun test_fetchMovieDetail_isSuccess() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        val expectedPosterPath = "test-path"
        val expectedMovie = TheMovieDatabaseService.Movie(
            id = 1,
            title = "test-title",
            poster_path = expectedPosterPath
        )
        val expectedImage = TheMovieDatabaseService.Image(
            file_path = "test"
        )
        val expectedCast = TheMovieDatabaseService.Cast(
            id = 1,
            name = "test-name",
            character = "test-character"
        )
        val expectedCrew = TheMovieDatabaseService.Crew(
            id =  2,
            name = "test-name"
        )
        val expectedBackdropPath = "text-backdrop"
        val expectedItem = TheMovieDatabaseService.MovieDetail(
            id = 100,
            title = "test",
            poster_path = null,
            backdrop_path = expectedBackdropPath,
            overview = "test-overview",
            release_date = "2021",
            runtime = 100,
            credits = TheMovieDatabaseService.Credit(
                cast = listOf(expectedCast),
                crew = listOf(expectedCrew)
            ),
            recommendations = TheMovieDatabaseService.ListResponse(
                page = 1,
                results = listOf(expectedMovie),
                total_pages = 1,
                total_results = 1
            ),
            images = TheMovieDatabaseService.MovieImage(
                backdrops = listOf(expectedImage),
                posters = emptyList()
            )
        )

        runBlockingTest {
            service.movieDetailResponse = Result.success(expectedItem)

            when (val result = repository.fetchMovieDetail(1)) {
                is MovieDetailResult.Success -> {
                    val movie = result.movie
                    val backdrop = movie.backdrops.firstOrNull()
                    val cast = movie.casts.firstOrNull()
                    val recommendation = movie.recommendations.firstOrNull()

                    assertEquals(expectedItem.id, movie.id)
                    assertEquals(expectedItem.title, movie.title)
                    assertEquals(expectedItem.overview, movie.overview)
                    assertEquals(expectedItem.runtime, movie.runtime)
                    assertEquals("https://image.tmdb.org/t/p/w500${expectedBackdropPath}", backdrop)
                    assertEquals("${expectedCast.character} / ${expectedCast.name}", cast)
                    assertEquals(expectedMovie.id, recommendation?.id)
                    assertEquals(expectedMovie.title, recommendation?.title)
                    assertEquals("https://image.tmdb.org/t/p/w500$expectedPosterPath", recommendation?.posterPath)
                }
                is MovieDetailResult.Failure -> {
                    fail("Response mut be MovieDetailResult.Success, but it is $result")
                }
            }
        }
    }

    @Test
    fun test_fetchMovieDetail_isFailure() {
        val service = ServiceMock()
        val repository = MovieRepositoryImpl(service)

        runBlockingTest {
            service.moviesResponse = Result.failure(IllegalAccessException())

            when (val result = repository.fetchMovieDetail(1)) {
                is MovieDetailResult.Success -> {
                    fail("Response mut be MoviesResult.Failure.Reason.Unknown, but it is $result")
                }
                is MovieDetailResult.Failure -> {
                    assertEquals(MovieDetailResult.Failure(IllegalAccessException()).javaClass, result.javaClass)
                }
            }
        }
    }
}