package com.martysuzuki.remotedatasource

import com.martysuzuki.remotedatasourceinterface.TheMovieDatabaseService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun TheMovieDatabaseService(
    dependency: TheMovieDatabaseService.Dependency
): TheMovieDatabaseService {
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().run {
                val url = url()
                    .newBuilder()
                    .addQueryParameter("api_key", dependency.apiKey)
                    .build()

                newBuilder()
                    .addHeader("Authorization", "Bearer ${dependency.accessToken}")
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .url(url)
                    .build()
            }
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    return retrofit.create(TheMovieDatabaseService::class.java)
}