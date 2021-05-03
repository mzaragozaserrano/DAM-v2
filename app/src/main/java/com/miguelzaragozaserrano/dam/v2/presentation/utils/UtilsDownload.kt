package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.miguelzaragozaserrano.dam.v2.data.models.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object UtilsDownload {

    var numberCameras: Int? = -1
    lateinit var onCameraDownload: ((camera: Camera) -> Unit)
    lateinit var onImageDownload: ((image: BitmapDrawable) -> Unit)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun downloadFile() {
        this.coroutineScope.launch {
            runCatching {
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
                                data.substringAfter("<Data name=\"Nombre\">")
                                    .substringBefore("</Data>")
                            val coordinateAux =
                                data.substringAfter("<coordinates>")
                                    .substringBefore("</coordinates>")
                            val latitude = coordinateAux.substringAfter(",").substringBefore(",10")
                            val longitude = coordinateAux.substringBefore(",")
                            onCameraDownload.invoke(
                                Camera(
                                    id = UUID.randomUUID().toString(),
                                    name = nameAux.substringAfter("<Value>")
                                        .substringBefore("</Value>"),
                                    favorite = false,
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

    @Throws(IOException::class)
    fun drawableFromUrl(url: String?) {
        this.coroutineScope.launch {
            runCatching {
                val x: Bitmap
                val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val input: InputStream = connection.inputStream
                x = BitmapFactory.decodeStream(input)
                onImageDownload.invoke(BitmapDrawable(Resources.getSystem(), x))
            }
        }
    }
}