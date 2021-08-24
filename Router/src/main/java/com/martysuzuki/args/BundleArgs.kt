package com.martysuzuki.args

import android.os.Parcelable

interface BundleArgs: Parcelable {
    companion object {
        val KEY_NAME = "BUNDLE_ARGS_KEY"
    }
}