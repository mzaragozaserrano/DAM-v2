package com.miguelzaragozaserrano.dam.v2.domain.models

data class Camera(
    var id: String,
    var name: String,
    var favorite: Boolean,
    var url: String,
    var latitude: String,
    var longitude: String,
    var selected: Boolean
)