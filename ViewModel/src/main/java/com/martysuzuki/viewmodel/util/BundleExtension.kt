package com.martysuzuki.viewmodel.util

import androidx.core.os.bundleOf
import com.martysuzuki.args.BundleArgs

fun BundleArgs.toBundle() = bundleOf(BundleArgs.KEY_NAME to this)