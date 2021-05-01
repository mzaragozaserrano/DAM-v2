package com.miguelzaragozaserrano.dam.v2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Camera(
    var id: String,
    var name: String,
    var favorite: Boolean,
    var url: String,
    var latitude: String,
    var longitude: String,
    var selected: Boolean
) : Parcelable