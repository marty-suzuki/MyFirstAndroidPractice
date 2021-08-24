package com.martysuzuki.moviesearchsample.util

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.martysuzuki.args.BundleArgs

inline fun <reified T: BundleArgs> NavController.navigate(resId: Int, args: T) {
    val bundle = bundleOf(BundleArgs.KEY_NAME to args)
    navigate(resId, bundle)
}