package com.martysuzuki.router.detail

import androidx.fragment.app.Fragment
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.router.Router

interface MovieDetailRouter : Router {
    fun routeMovieDetail(fragment: Fragment, args: MovieDetailArgs)
}