package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel

class ViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass){
            when{
                isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(context)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class you must add it")
            }
        } as T
    }
}