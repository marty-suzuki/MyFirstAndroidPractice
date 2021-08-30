package com.martysuzuki.viewmodel.detail

import androidx.lifecycle.SavedStateHandle
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.viewmodel.AnyViewModel
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiLogic
import com.martysuzuki.viewmodel.util.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    uiLogicFactory: UiLogicFactory<MovieDetailUiLogic, Int>,
    router: MovieDetailRouter,
    savedStateHandle: SavedStateHandle
) : AnyViewModel<MovieDetailUiLogic, MovieDetailRouter, Int>(
    uiLogicFactory = uiLogicFactory,
    router = router,
    dependency = savedStateHandle.getOrThrow<MovieDetailArgs>().movieId
)