package com.miguelzaragozaserrano.dam.v2.presentation.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.miguelzaragozaserrano.dam.v2.R

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        onInject()
        setupInit()
    }

    abstract fun getViewBinding(): VB

    open fun onInject() {}

    private fun setupInit() {
        setup1Toolbar()
        setup2Menu()
        setup3Navigation()
        setup4Vars()
        setup5InitFunctions()
        setup6ClickListeners()
    }

    open fun setup1Toolbar() {}
    open fun setup2Menu() {}
    open fun setup3Navigation() {}
    open fun setup4Vars() {}
    open fun setup5InitFunctions() {}
    open fun setup6ClickListeners() {}

    fun showDialogMessageComplete(
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        icon: Int? = null,
        cancelable: Boolean = false,
        functionPositiveButton: () -> Unit,
        functionNegativeButton: () -> Unit
    ) {
        val builder = AlertDialog.Builder(this)
        if (icon != null) {
            builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText) { _, _ ->
                    functionPositiveButton()
                }
                .setNegativeButton(negativeText) { _, _ ->
                    functionNegativeButton()
                }
        } else {
            builder.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText) { _, _ ->
                    functionPositiveButton()
                }
                .setNegativeButton(negativeText) { _, _ ->
                    functionNegativeButton()
                }
        }
        val alert = builder.create()
        alert.window?.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dialog_background))
        alert.show()
    }

    fun showDialogMessageSimple(
        title: String,
        message: String,
        positiveText: String,
        icon: Int? = null,
        cancelable: Boolean = false,
        functionPositiveButton: () -> Unit
    ) {
        val builder = AlertDialog.Builder(this)
        if (icon != null) {
            builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText) { _, _ ->
                    functionPositiveButton()
                }
                .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                    dialog.dismiss()
                }
        } else {
            builder.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText) { _, _ ->
                    functionPositiveButton()
                }
                .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                    dialog.dismiss()
                }
        }
        val alert = builder.create()
        alert.window?.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dialog_background))
        alert.show()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null
    }

    fun showSnackLong(view: View?, text: String, context: Context?, colorBackground: Int, colorText: Int) {
        view?.let {
            context?.let {
                val snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                snack.view.setBackgroundColor(ContextCompat.getColor(context, colorBackground))
                snack.setTextColor(ContextCompat.getColor(context, colorText))
                snack.show()
            }
        }
    }

    fun showSnackShort(view: View?, text: String, context: Context?, colorBackground: Int, colorText: Int) {
        view?.let {
            context?.let {
                val snack = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
                snack.view.setBackgroundColor(ContextCompat.getColor(context, colorBackground))
                snack.setTextColor(ContextCompat.getColor(context, colorText))
                snack.show()
            }
        }
    }

}