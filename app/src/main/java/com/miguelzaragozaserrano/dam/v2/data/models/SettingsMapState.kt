package com.miguelzaragozaserrano.dam.v2.data.models

data class SettingsMapState(
    var cluster: Boolean = false,
    var cameras: List<Camera>,
    var showAll: Boolean = false
)