package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseActivity

fun Fragment.showProgress(show: Boolean, hasShade: Boolean) {
    (activity as BaseActivity<*>).showProgress(show, hasShade)
}

fun Fragment.showToastShort(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToastLong(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showDialogMessageComplete(
    title: String,
    message: String,
    positiveText: String,
    negativeText: String,
    icon: Int? = null,
    cancelable: Boolean = false,
    functionPositiveButton: () -> Unit,
    functionNegativeButton: () -> Unit
) {
    (activity as BaseActivity<*>).showDialogMessageComplete(
        title,
        message,
        positiveText,
        negativeText,
        icon,
        cancelable,
        functionPositiveButton,
        functionNegativeButton
    )
}