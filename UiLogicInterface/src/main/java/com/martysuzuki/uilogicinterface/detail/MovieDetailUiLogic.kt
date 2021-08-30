package com.martysuzuki.uilogicinterface.detail

import com.martysuzuki.uilogicinterface.UiLogic
import kotlinx.coroutines.flow.Flow

interface MovieDetailUiLogic: UiLogic {
    val update: Flow<MovieDetailUiState>
    val navigateToMovieDetail: Flow<Int>
    fun onViewCreated()
    fun onDestroyView()
    fun onItemClicked(position: Int)
}