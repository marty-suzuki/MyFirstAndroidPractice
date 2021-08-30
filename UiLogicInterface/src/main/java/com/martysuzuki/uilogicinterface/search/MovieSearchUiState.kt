package com.martysuzuki.uilogicinterface.search

import androidx.recyclerview.widget.DiffUtil

data class MovieSearchUiState(val items: List<MovieSearchItem>, val diffResult: DiffUtil.DiffResult)