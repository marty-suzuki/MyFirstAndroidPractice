package com.martysuzuki.moviesearchsample.module

import com.martysuzuki.repository.movie.MovieRepositoryImpl
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppBindModule {
    @Binds
    fun bindMovieRepository(
        movieRepository: MovieRepositoryImpl
    ): MovieRepository
}