package com.martysuzuki.uilogicinterface.search

import com.github.marty_suzuki.unio.Unio
import kotlinx.coroutines.flow.MutableSharedFlow

class MovieSearchInput: Unio.Input {
    val search = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val reachBottom = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onItemClicked = MutableSharedFlow<Int>(extraBufferCapacity = 1)
}