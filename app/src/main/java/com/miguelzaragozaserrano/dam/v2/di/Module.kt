package com.miguelzaragozaserrano.dam.v2.di

import com.miguelzaragozaserrano.dam.v2.presentation.utils.ViewModelFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { ViewModelFactory(androidContext()) }
}