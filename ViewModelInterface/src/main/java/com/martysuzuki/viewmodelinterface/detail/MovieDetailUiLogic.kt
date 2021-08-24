package com.martysuzuki.viewmodelinterface.detail

import com.martysuzuki.viewmodelinterface.UiLogic
import kotlinx.coroutines.flow.Flow

interface MovieDetailUiLogic: UiLogic {
    val update: Flow<MovieDetailUiState>
    val navigateToMovieDetail: Flow<Int>
    fun onViewCreated()
    fun onDestroyView()
    fun onItemClicked(position: Int)
}