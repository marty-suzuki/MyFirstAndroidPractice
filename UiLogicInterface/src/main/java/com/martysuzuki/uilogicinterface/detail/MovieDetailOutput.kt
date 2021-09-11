package com.martysuzuki.uilogicinterface.detail

import com.github.marty_suzuki.unio.Unio
import kotlinx.coroutines.flow.Flow

class MovieDetailOutput(
    val update: Flow<MovieDetailUiState>,
    val navigateToMovieDetail: Flow<Int>,
): Unio.Output