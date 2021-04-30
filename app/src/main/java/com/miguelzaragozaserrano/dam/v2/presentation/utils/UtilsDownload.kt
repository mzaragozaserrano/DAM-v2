package com.miguelzaragozaserrano.dam.v2.presentation.utils

import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import java.util.*

object UtilsDownload {

    var numberCameras: Int? = -1
    lateinit var onCameraDownload: ((camera: Camera) -> Unit)

    fun downloadFile() {
        doAsync {
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .url(Constants.URL_KML)
                .build()
            client.newCall(request).execute().use { response ->
                val allData = response.body?.string()?.split("<Placemark>")
                numberCameras = allData?.size
                for (data in allData.orEmpty()) {
                    if (allData?.get(0) != data) {
                        val nameAux =
                            data.substringAfter("<Data name=\"Nombre\">").substringBefore("</Data>")
                        val coordinateAux =
                            data.substringAfter("<coordinates>").substringBefore("</coordinates>")
                        val latitude = coordinateAux.substringAfter(",").substringBefore(",10")
                        val longitude = coordinateAux.substringBefore(",")
                        onCameraDownload.invoke(
                            Camera(
                                id = UUID.randomUUID().toString(),
                                name = nameAux.substringAfter("<Value>")
                                    .substringBefore("</Value>"),
                                url = data.substringAfter("src=").substringBefore("  width"),
                                longitude = longitude,
                                latitude = latitude,
                                selected = false
                            )
                        )
                    }
                }
            }
        }
    }

}