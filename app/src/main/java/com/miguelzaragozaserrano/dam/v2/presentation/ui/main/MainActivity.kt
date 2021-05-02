package com.miguelzaragozaserrano.dam.v2.presentation.ui.main

import android.os.Bundle
import com.miguelzaragozaserrano.dam.v2.databinding.ActivityMainBinding
import com.miguelzaragozaserrano.dam.v2.di.appModule
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseActivity
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin

class
MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    appModule
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        KoinContextHandler.stop()
    }

}