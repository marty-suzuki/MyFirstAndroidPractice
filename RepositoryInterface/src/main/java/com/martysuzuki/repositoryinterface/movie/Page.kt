package com.martysuzuki.repositoryinterface.movie

sealed class Page {
    object Finish : Page()
    data class Next(val page: Int) : Page()
}