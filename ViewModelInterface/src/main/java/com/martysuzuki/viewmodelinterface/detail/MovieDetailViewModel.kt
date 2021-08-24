package com.martysuzuki.viewmodelinterface.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martysuzuki.viewmodelinterface.AnyViewModel
import com.martysuzuki.viewmodelinterface.UiLogicFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MovieDetailViewModelFactory

class MovieDetailViewModel @AssistedInject constructor(
    @MovieDetailViewModelFactory uiLogicFactory: UiLogicFactory<MovieDetailUiLogic, Int>,
    @Assisted movieId: Int
) : AnyViewModel<MovieDetailUiLogic, Int>(uiLogicFactory, movieId) {

    @AssistedFactory
    interface Factory {
        fun create(movieId: Int): MovieDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            movieId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(movieId) as T
            }
        }
    }
}