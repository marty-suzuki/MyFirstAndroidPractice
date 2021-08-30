package com.martysuzuki.uilogicinterface.search

import com.martysuzuki.uilogicinterface.DiffableItem

sealed class MovieSearchItem : DiffableItem {

    data class Loading(val style: Style) : MovieSearchItem() {
        enum class Style {
            MATCH_PARENT, WRAP_CONTENT
        }

        override val identifier = "LOADING_IDENTIFIER-$style"
    }

    object NoResults : MovieSearchItem() {
        override val identifier = "NO_RESULTS"
    }

    data class Movie(val id: Int, val title: String, val postPath: String?) : MovieSearchItem() {
        override val identifier = "MOVIE_$id"
    }
}