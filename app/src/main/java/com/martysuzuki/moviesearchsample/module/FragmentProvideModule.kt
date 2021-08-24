package com.martysuzuki.moviesearchsample.module

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.moviesearchsample.R
import com.martysuzuki.moviesearchsample.util.navigate
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.uicomponent.common.UnauthorizedDialogFragment
import com.martysuzuki.viewmodel.detail.MovieDetailUiLogicFactoryImpl
import com.martysuzuki.viewmodelinterface.UiLogicFactory
import com.martysuzuki.viewmodelinterface.detail.MovieDetailUiLogic
import com.martysuzuki.viewmodelinterface.detail.MovieDetailViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object FragmentProvideModule {

    @Provides
    fun provideMovieDetailRouter(): MovieDetailRouter {
        return object : MovieDetailRouter {
            override fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs) {
                fragment.findNavController().navigate(R.id.MovieDetailFragment, args)
            }
        }
    }

    @Provides
    fun provideMovieSearchRouter(): MovieSearchRouter {
        return object : MovieSearchRouter {
            override fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs) {
                fragment.findNavController().navigate(R.id.MovieDetailFragment, args)
            }

            override fun routeUnauthorized(fragment: Fragment) {
                fragment.activity?.apply {
                    val dialog = UnauthorizedDialogFragment()
                    dialog.show(supportFragmentManager, "UNAUTHORIZED_DIALOG_FRAGMENT")
                }
            }
        }
    }
}