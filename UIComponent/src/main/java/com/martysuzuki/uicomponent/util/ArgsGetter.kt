package com.martysuzuki.uicomponent.util

import androidx.fragment.app.Fragment
import com.martysuzuki.args.BundleArgs

interface ArgsGetter<T: BundleArgs> {
    @Suppress("UNCHECKED_CAST")
    fun args(fragment: Fragment) = lazy {
        fragment.arguments?.let {
            it.get(BundleArgs.KEY_NAME) as? T
        } ?: throw IllegalStateException("Fragment $fragment has null arguments")
    }
}