package com.martysuzuki.moviesearchsample.module

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.moviesearchsample.R
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.uicomponent.common.UnauthorizedDialogFragment
import com.martysuzuki.viewmodel.util.toBundle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

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
}