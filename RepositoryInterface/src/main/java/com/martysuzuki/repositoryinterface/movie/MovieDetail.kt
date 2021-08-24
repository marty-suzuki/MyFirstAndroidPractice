package com.martysuzuki.repositoryinterface.movie

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?,
    val releaseDate: String,
    val runtime: Int,
    val backdrops: List<String>,
    val casts: List<String>,
    val recommendations: List<Movie>
)