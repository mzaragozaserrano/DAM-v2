package com.miguelzaragozaserrano.dam.v2.presentation.ui.base

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.miguelzaragozaserrano.dam.v2.presentation.utils.setSupportActionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseFragment<VB : ViewBinding> : Fragment(), CoroutineScope by MainScope() {

    lateinit var binding: VB

    private var menuId: Int = 0
    private var toolbar: Toolbar? = null
    private var functionOnCreateOptionsMenu: (() -> Unit)? = null
    private lateinit var menu: Menu

    var callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedFun()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        if (container != null)
            binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (menuId != 0) {
            inflater.inflate(menuId, menu)
        }
        this.menu = menu
        functionOnCreateOptionsMenu?.invoke()
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup): VB

    private fun setupInit() {
        setup1Observers()
        setup2Listeners()
        setup3TextWatcher()
        setup4Vars()
        setup5InitFunctions()
    }

    open fun setup1Observers() {}
    open fun setup2Listeners() {}
    open fun setup3TextWatcher() {}
    open fun setup4Vars() {}
    open fun setup5InitFunctions() {}

    open fun onBackPressedFun() {}

    fun setupToolbar(toolbar: Toolbar, titleId: Int, menuId: Int?, navigationIdIcon: Int?, functionOnCreateOptionsMenu: ((menu: Menu) -> Unit)? = null) {
        setSupportActionBar(toolbar)
        with(toolbar) {
            if (navigationIdIcon != null) {
                setNavigationIcon(navigationIdIcon)
                setNavigationOnClickListener {
                    onBackPressedFun()
                }
            }
        }
        toolbar.title = getString(titleId)
        this.toolbar = toolbar
        if (menuId != null) {
            this.menuId = menuId
        }
        if(functionOnCreateOptionsMenu != null){
            this.functionOnCreateOptionsMenu = { functionOnCreateOptionsMenu(menu) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toolbarItemSelected(itemSelected = item, menu = menu)
        return true
    }

    open fun toolbarItemSelected(itemSelected: MenuItem, menu: Menu) {}

}