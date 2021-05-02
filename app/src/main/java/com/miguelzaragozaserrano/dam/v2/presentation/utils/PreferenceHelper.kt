package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.DATE
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.NEW_APP

object PreferenceHelper {

    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.date
        get() = getString(DATE, NEW_APP)
        set(value) {
            editMe {
                it.putString(DATE, value)
            }
        }

}

