package com.martysuzuki.remotedatasourceinterface

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDatabaseService {

    interface Dependency {
        val apiKey: String
        val accessToken: String
    }

    data class ListResponse<T>(
        val page: Int,
        val results: List<T>,
        val total_results: Int,
        val total_pages: Int
    )

    data class Movie(
        val id: Int,
        val title: String,
        val poster_path: String?
    )

    data class Image(
        val file_path: String
    )

    data class MovieImage(
        val backdrops: List<Image>,
        val posters: List<Image>
    )

    data class Cast(
        val id: Int,
        val name: String,
        val character: String
    )

    data class Crew(
        val id: Int,
        val name: String
    )

    data class Credit(
        val cast: List<Cast>,
        val crew: List<Crew>
    )

    data class MovieDetail(
        val id: Int,
        val title: String,
        val poster_path: String?,
        val backdrop_path: String?,
        val overview: String?,
        val release_date: String,
        val runtime: Int,
        val credits: Credit?,
        val recommendations: ListResponse<Movie>?,
        val images: MovieImage?
    )

    // https://developers.themoviedb.org/3/search/search-movies
    @GET("search/movie")
    suspend fun movies(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ListResponse<Movie>

    // https://developers.themoviedb.org/3/movies/get-movie-details
    // https://developers.themoviedb.org/3/movies/get-movie-credits
    // https://developers.themoviedb.org/3/movies/get-movie-images
    // https://developers.themoviedb.org/3/movies/get-movie-recommendations
    @GET("movie/{movieId}?append_to_response=images,recommendations,credits")
    suspend fun movieDetail(
        @Path("movieId") movieId: Int
    ): MovieDetail
}