package com.martysuzuki.uilogicinterface.search

import com.github.marty_suzuki.unio.Computed
import com.github.marty_suzuki.unio.Unio
import kotlinx.coroutines.flow.Flow

class MovieSearchOutput(
    val update: Flow<MovieSearchUiState>,
    val query: Computed<String?>,
    val items: Computed<List<MovieSearchItem>>,
    val navigateToMovieDetail: Flow<Int>,
    val showUnauthorizedDialog: Flow<Unit>,
): Unio.Output