package com.martysuzuki.moviesearchsample.module

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.moviesearchsample.R
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.uicomponent.common.UnauthorizedDialogFragment
import com.martysuzuki.uilogic.detail.MovieDetailUiLogicImpl
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiLogic
import com.martysuzuki.viewmodel.util.toBundle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

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
    fun provideMovieDetailUiLogicFactory(
        movieRepository: MovieRepository,
        defaultDispatcher: CoroutineDispatcher,
    ) = object : UiLogicFactory<MovieDetailUiLogic, Int> {

        override fun create(
            viewModelScope: CoroutineScope,
            dependency: Int
        ) = MovieDetailUiLogicImpl(
            movieRepository = movieRepository,
            defaultDispatcher = defaultDispatcher,
            viewModelScope = viewModelScope,
            movieId = dependency
        )
    }
}