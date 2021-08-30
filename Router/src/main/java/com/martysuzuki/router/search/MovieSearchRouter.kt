package com.martysuzuki.router.search

import androidx.fragment.app.Fragment
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.router.Router

interface MovieSearchRouter : Router {
    fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs)
    fun routeUnauthorized(fragment: Fragment)
}