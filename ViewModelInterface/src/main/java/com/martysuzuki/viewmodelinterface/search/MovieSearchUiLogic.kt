package com.martysuzuki.viewmodelinterface.search

import com.martysuzuki.viewmodelinterface.UiLogic
import kotlinx.coroutines.flow.Flow

interface MovieSearchUiLogic: UiLogic {
    val update: Flow<MovieSearchUiState>
    val query: String?
    val items: List<MovieSearchItem>
    val navigateToMovieDetail: Flow<Int>
    val showUnauthorizedDialog: Flow<Unit>
    fun search(text: String)
    fun reachBottom()
    fun onItemClicked(position: Int)
}
