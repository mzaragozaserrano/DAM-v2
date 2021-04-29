package com.miguelzaragozaserrano.dam.v2.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseFragment<VB : ViewBinding> : Fragment(), CoroutineScope by MainScope() {

    lateinit var binding: VB

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

}