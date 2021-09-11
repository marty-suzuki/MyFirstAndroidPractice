package com.martysuzuki.moviesearchsample.module

import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.github.marty_suzuki.unio.UnidirectionalIO
import com.github.marty_suzuki.unio.UnioFactory
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.moviesearchsample.R
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.uicomponent.common.UnauthorizedDialogFragment
import com.martysuzuki.uilogic.detail.MovieDetailUnio
import com.martysuzuki.uilogic.search.MovieSearchUnio
import com.martysuzuki.uilogicinterface.detail.MovieDetailInput
import com.martysuzuki.uilogicinterface.detail.MovieDetailOutput
import com.martysuzuki.uilogicinterface.search.MovieSearchInput
import com.martysuzuki.viewmodel.util.getOrThrow
import com.martysuzuki.viewmodel.util.toBundle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Module()
@InstallIn(ViewModelComponent::class)
object ViewModelProvideModule {
    @Provides
    fun provideMovieDetailRouter(): MovieDetailRouter {
        return object : MovieDetailRouter {
            override fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs) {
                fragment.findNavController().navigate(R.id.MovieDetailFragment, args.toBundle())
            }
        }
    }

    @Provides
    fun provideMovieSearchRouter(): MovieSearchRouter {
        return object : MovieSearchRouter {
            override fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs) {
                fragment.findNavController().navigate(R.id.MovieDetailFragment, args.toBundle())
            }

            override fun routeUnauthorized(fragment: Fragment) {
                fragment.activity?.apply {
                    val dialog = UnauthorizedDialogFragment()
                    dialog.show(supportFragmentManager, "UNAUTHORIZED_DIALOG_FRAGMENT")
                }
            }
        }
    }

    @Provides
    fun provideMovieSearchUnioState() = MovieSearchUnio.State()

    @Provides
    fun provideMovieSearchInput() = MovieSearchInput()

    @Provides
    fun provideMovieDetailUnioState() = MovieDetailUnio.State()

    @Provides
    fun provideMovieDetailInput() = MovieDetailInput()

    @Provides
    fun provideMovieDetailUnioFactory(
        input: MovieDetailInput,
        state: MovieDetailUnio.State,
        movieRepository: MovieRepository,
        defaultDispatcher: CoroutineDispatcher,
        savedStateHandle: SavedStateHandle
    ) = object : UnioFactory<MovieDetailInput, MovieDetailOutput> {

        override fun create(
            viewModelScope: CoroutineScope,
            onCleared: Flow<Unit>
        ): UnidirectionalIO<MovieDetailInput, MovieDetailOutput> {
            return MovieDetailUnio(
                input = input,
                extra = MovieDetailUnio.Extra(
                    movieRepository = movieRepository,
                    defaultDispatcher = defaultDispatcher,
                    onCleared = onCleared,
                    movieId = savedStateHandle.getOrThrow<MovieDetailArgs>().movieId
                ),
                state = state,
                viewModelScope = viewModelScope
            )
        }
    }
}