package com.martysuzuki.viewmodel.util

import androidx.lifecycle.SavedStateHandle
import com.martysuzuki.args.BundleArgs

inline fun <reified T: BundleArgs> SavedStateHandle.getOrThrow(): T {
    return get(BundleArgs.KEY_NAME) ?: throw IllegalArgumentException("SavedStateHandle doesn't have ${T::class}")
}