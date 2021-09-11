package com.martysuzuki.viewmodel.detail

import com.github.marty_suzuki.unio.UnioFactory
import com.github.marty_suzuki.unio.UnioViewModel
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.uilogicinterface.detail.MovieDetailInput
import com.martysuzuki.uilogicinterface.detail.MovieDetailOutput
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    unioFactory: UnioFactory<MovieDetailInput, MovieDetailOutput>,
    val router: MovieDetailRouter,
) : UnioViewModel<MovieDetailInput, MovieDetailOutput>(unioFactory)