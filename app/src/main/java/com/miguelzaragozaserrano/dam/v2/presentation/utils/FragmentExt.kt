package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.content.Context
import android.view.View
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

fun Fragment.showDialogMessageSimple(
    title: String,
    message: String,
    positiveText: String,
    icon: Int? = null,
    cancelable: Boolean = false,
    functionPositiveButton: () -> Unit
) {
    (activity as BaseActivity<*>).showDialogMessageSimple(
        title,
        message,
        positiveText,
        icon,
        cancelable,
        functionPositiveButton
    )
}

fun Fragment.showSnackLong(
    view: View?,
    text: String,
    context: Context?,
    colorBackground: Int,
    colorText: Int
) {
    (activity as BaseActivity<*>).showSnackLong(
        view,
        text,
        context,
        colorBackground,
        colorText
    )
}

fun Fragment.showSnackShort(
    view: View?,
    text: String,
    context: Context?,
    colorBackground: Int,
    colorText: Int
) {
    (activity as BaseActivity<*>).showSnackShort(
        view,
        text,
        context,
        colorBackground,
        colorText
    )
}

fun Fragment.setSupportActionBar(toolbar: Toolbar? = null) {
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    setHasOptionsMenu(true)
}

fun Fragment.isNetworkAvailable(): Boolean {
    return (activity as BaseActivity<*>).isNetworkAvailable()
}