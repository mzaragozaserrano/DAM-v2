package com.miguelzaragozaserrano.dam.v2.presentation.utils

import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun Camera.toCameraEntity(): CameraEntity {
    return CameraEntity(
        id = id,
        name = name,
        url = url,
        latitude = latitude,
        longitude = longitude
    )
}

fun List<CameraEntity>.toListCamera(): List<Camera> {
    return mapTo(mutableListOf()) {
        it.toCamera()
    }
}

fun CameraEntity.toCamera(): Camera {
    return Camera(
        id = this.id,
        name = this.name,
        url = this.url,
        latitude = this.latitude,
        longitude = this.longitude,
        selected = false
    )
}

fun LocalDateTime.toDateWithoutTime(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    return this.format(formatter)
}