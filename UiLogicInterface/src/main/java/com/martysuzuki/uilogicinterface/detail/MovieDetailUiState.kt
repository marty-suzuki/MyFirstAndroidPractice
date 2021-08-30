package com.martysuzuki.uilogicinterface.detail

import androidx.recyclerview.widget.DiffUtil

data class MovieDetailUiState(val items: List<MovieDetailItem>, val diffResult: DiffUtil.DiffResult)