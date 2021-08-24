package com.martysuzuki.router.search

import androidx.fragment.app.Fragment
import com.martysuzuki.args.detail.MovieDetailArgs

interface MovieSearchRouter {
    fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs)
    fun routeUnauthorized(fragment: Fragment)
}