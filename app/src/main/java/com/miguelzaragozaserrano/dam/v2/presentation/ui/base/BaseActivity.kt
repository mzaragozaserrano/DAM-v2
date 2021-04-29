package com.miguelzaragozaserrano.dam.v2.presentation.ui.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<VB: ViewBinding>: AppCompatActivity() {

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

    private fun setupInit(){
        setup1Toolbar()
        setup2Menu()
        setup3Navigation()
        setup4Vars()
        setup5InitFunctions()
        setup6ClickListeners()
    }

    open fun setup1Toolbar(){}
    open fun setup2Menu(){}
    open fun setup3Navigation(){}
    open fun setup4Vars(){}
    open fun setup5InitFunctions(){}
    open fun setup6ClickListeners(){}

    fun showProgress(show: Boolean, hasShade: Boolean) {
        if (show) disableScreen()
        else enableScreen()
        /*val progress = findViewById<ProgressBar>(R.id.progress_bar)
        val progressContainer = findViewById<View>(R.id.progress_container)
        progressContainer?.isVisible = show && hasShade
        progress?.isVisible = show*/
    }

    fun showSnack(view: View?, text: String, context: Context?, colorBackground: Int, colorText: Int) {
        view?.let {
            context?.let {
                val snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                snack.view.setBackgroundColor(ContextCompat.getColor(context, colorBackground))
                snack.setTextColor(ContextCompat.getColor(context, colorText))
                snack.show()
            }
        }
    }

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
        if(icon != null){
            builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText){ _, _ ->
                    functionPositiveButton()
                }
                .setNegativeButton(negativeText){ _, _ ->
                    functionNegativeButton()
                }
        }else{
            builder.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText){ _, _ ->
                    functionPositiveButton()
                }
                .setNegativeButton(negativeText){ _, _ ->
                    functionNegativeButton()
                }
        }
        val alert = builder.create()
        //alert.window?.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dialog_fragment))
        alert.show()
    }

    fun showDialogMessageSimple(
        title: String,
        message: String,
        positiveText: String,
        icon: Int? = null,
        cancelable: Boolean = false,
        functionPositiveButton: (_positiveDialog: AlertDialog) -> Unit
    ) {
        val builder = AlertDialog.Builder(this)
        if(icon != null){
            builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText){ _, _ ->
                    functionPositiveButton(dialog)
                }
        }else{
            builder.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText){ _, _ ->
                    functionPositiveButton(dialog)
                }
        }
        val alert = builder.create()
        //alert.window?.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dialog_fragment))
        alert.show()
    }

    fun showToast(text: String, context: Context?) {
        context?.let {
            Toast.makeText(it, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun hideKeyboard(context: Context?, view: View?) {
        context?.let {
            val imm =
                context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            view?.let {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun disableScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        currentFocus?.clearFocus()
    }

    private fun enableScreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}