package com.martysuzuki.repository.movie

import com.martysuzuki.remotedatasourceinterface.TheMovieDatabaseService
import com.martysuzuki.repositoryinterface.movie.*
import java.lang.Exception
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val theMovieDatabaseService: TheMovieDatabaseService
) : MovieRepository {

    override suspend fun fetchMovies(query: String, page: Int): MoviesResult {
        when {
            query.isEmpty() -> {
                return MoviesResult.Failure(MoviesResult.Failure.Reason.EmptyQuery)
            }
            page < 1 -> {
                return MoviesResult.Failure(MoviesResult.Failure.Reason.InvalidPage)
            }
            else -> {
                return try {
                    val response = theMovieDatabaseService.movies(query, page)

                    val movies = response.results.map(this::translateMovie)
                    val nextPage = when (response.page) {
                        response.total_pages -> {
                            Page.Finish
                        }
                        else -> {
                            if (response.total_pages > 0) {
                                Page.Next(response.page + 1)
                            } else {
                                Page.Finish
                            }
                        }
                    }

                    MoviesResult.Success(movies, nextPage)
                } catch (e: Exception) {
                    when (e) {
                        is retrofit2.HttpException -> {
                            val reason = when (e.code()) {
                                401 -> MoviesResult.Failure.Reason.Unauthorized
                                404 -> MoviesResult.Failure.Reason.NotFound
                                else -> MoviesResult.Failure.Reason.Unknown(e)
                            }
                            MoviesResult.Failure(reason)
                        }
                        else -> {
                            MoviesResult.Failure(MoviesResult.Failure.Reason.Unknown(e))
                        }
                    }
                }
            }
        }
    }

    override suspend fun fetchMovieDetail(id: Int): MovieDetailResult {
        return try {
            val response = theMovieDatabaseService.movieDetail(id)

            val movie = MovieDetail(
                id = response.id,
                title = response.title,
                overview = response.overview,
                releaseDate = response.release_date,
                runtime = response.runtime,
                backdrops = response.backdrop_path?.let { backdropPath ->
                    response.images
                        ?.let { images ->
                            images.backdrops.map { it.file_path }
                        }
                        ?.let { it + listOf(backdropPath) }
                        ?.map(this::getFullpath)
                } ?: emptyList(),
                casts = response.credits?.let { credit ->
                    credit.cast.map { "${it.character} / ${it.name}" }
                } ?: emptyList(),
                recommendations = response.recommendations?.results?.map(this::translateMovie) ?: emptyList()
            )
            MovieDetailResult.Success(movie)
        } catch (e: Exception) {
            MovieDetailResult.Failure(e)
        }
    }

    private fun getFullpath(path: String) = "https://image.tmdb.org/t/p/w500$path"

    private fun translateMovie(movie: TheMovieDatabaseService.Movie) = Movie(
        id = movie.id,
        posterPath = movie.poster_path?.let(this::getFullpath),
        title = movie.title
    )
}