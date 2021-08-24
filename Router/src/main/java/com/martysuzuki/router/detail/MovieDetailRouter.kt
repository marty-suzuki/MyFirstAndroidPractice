package com.martysuzuki.router.detail

import androidx.fragment.app.Fragment
import com.martysuzuki.args.detail.MovieDetailArgs

interface MovieDetailRouter {
    fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs)
}