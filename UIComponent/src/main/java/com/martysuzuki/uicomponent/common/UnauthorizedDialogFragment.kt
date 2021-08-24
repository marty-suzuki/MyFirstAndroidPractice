package com.martysuzuki.uicomponent.common

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.martysuzuki.uicomponent.R

class UnauthorizedDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = activity?.let {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder(it)
            .setMessage(R.string.unauthorized_message)
            .setPositiveButton(
                getString(R.string.dialog_positive_show),
                DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.introduction_url))
                    )
                    startActivity(intent)
                }
            )
            .setNegativeButton(
                getString(R.string.dialog_negative_close),
                DialogInterface.OnClickListener { _, _ ->
                    // User cancelled the dialog
                }
            )
            .create()
        // Create the AlertDialog object and return it
    } ?: throw IllegalStateException("Activity cannot be null")
}