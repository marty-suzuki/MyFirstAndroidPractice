package com.martysuzuki.viewmodel.search

import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.viewmodel.AnyViewModel
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.search.MovieSearchUiLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    uiLogicFactory: UiLogicFactory<MovieSearchUiLogic, Unit>,
    router: MovieSearchRouter
) : AnyViewModel<MovieSearchUiLogic, MovieSearchRouter, Unit>(uiLogicFactory, router, Unit)