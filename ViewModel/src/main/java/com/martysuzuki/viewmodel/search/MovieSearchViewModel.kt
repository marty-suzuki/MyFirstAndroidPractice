package com.martysuzuki.viewmodel.search

import com.github.marty_suzuki.unio.UnioFactory
import com.github.marty_suzuki.unio.UnioViewModel
import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.uilogicinterface.search.MovieSearchInput
import com.martysuzuki.uilogicinterface.search.MovieSearchOutput
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    unioFactory: UnioFactory<MovieSearchInput, MovieSearchOutput>,
    val router: MovieSearchRouter
) : UnioViewModel<MovieSearchInput, MovieSearchOutput>(unioFactory)