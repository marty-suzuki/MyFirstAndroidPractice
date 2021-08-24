package com.martysuzuki.viewmodelinterface.search

import com.martysuzuki.viewmodelinterface.AnyViewModel
import com.martysuzuki.viewmodelinterface.UiLogicFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MovieSearchViewModelFactory

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    @MovieSearchViewModelFactory uiLogicFactory: UiLogicFactory<MovieSearchUiLogic, Unit>
) : AnyViewModel<MovieSearchUiLogic, Unit>(uiLogicFactory, Unit)
