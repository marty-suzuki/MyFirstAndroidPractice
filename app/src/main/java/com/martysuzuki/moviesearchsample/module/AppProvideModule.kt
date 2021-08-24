package com.martysuzuki.moviesearchsample.module

import com.martysuzuki.moviesearchsample.BuildConfig
import com.martysuzuki.remotedatasource.TheMovieDatabaseService
import com.martysuzuki.remotedatasourceinterface.TheMovieDatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object AppProvideModule {
    @Provides
    fun provideCoroutineDispatcher() : CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun provideTheMovieDatabaseService(): TheMovieDatabaseService = TheMovieDatabaseService(
        object : TheMovieDatabaseService.Dependency {
            override val apiKey = BuildConfig.TMDB_API_KEY
            override val accessToken = BuildConfig.TMDB_ACCESS_TOKEN
        }
    )
}