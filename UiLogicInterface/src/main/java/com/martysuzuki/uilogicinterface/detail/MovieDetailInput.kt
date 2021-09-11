package com.martysuzuki.uilogicinterface.detail

import com.github.marty_suzuki.unio.Unio
import kotlinx.coroutines.flow.MutableSharedFlow

class MovieDetailInput: Unio.Input {
    val onViewCreated = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onDestroyView = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onItemClicked = MutableSharedFlow<Int>(extraBufferCapacity = 1)
}