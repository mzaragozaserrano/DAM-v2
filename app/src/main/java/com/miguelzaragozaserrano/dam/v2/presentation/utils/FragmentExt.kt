package com.miguelzaragozaserrano.dam.v2.presentation.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseActivity

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

fun Fragment.setSupportActionBar(toolbar: Toolbar? = null) {
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    setHasOptionsMenu(true)
}