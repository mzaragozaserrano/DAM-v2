package com.miguelzaragozaserrano.dam.v2.presentation.utils

import com.miguelzaragozaserrano.dam.v2.data.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Camera.toCameraEntity(): CameraEntity {
    return CameraEntity(
        id = id,
        name = name,
        url = url,
        favorite = favorite,
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
        favorite = this.favorite,
        latitude = this.latitude,
        longitude = this.longitude,
        selected = false
    )
}

fun LocalDateTime.toDateString(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    return this.format(formatter)
}

fun String.toDate(): LocalDateTime {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    return LocalDateTime.parse(this, formatter)
}