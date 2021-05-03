package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.view.Menu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.data.dto.response.GoogleMapsResponse
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime


object Utils {

    fun isNextDay(currentDay: LocalDateTime, lastDay: LocalDateTime?): Boolean? =
        lastDay?.plusDays(1)?.isBefore(
            currentDay
        )

    fun setItemsVisibility(menu: Menu, visible: Boolean) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.itemId != R.id.search_icon) item.isVisible = visible
        }
    }

    fun getLastLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            var gpsLocationTime: Long = 0
            if (null != locationGPS) {
                gpsLocationTime = locationGPS.time
            }
            var netLocationTime: Long = 0
            if (null != locationNet) {
                netLocationTime = locationNet.time
            }
            return if (0 < gpsLocationTime - netLocationTime) {
                locationGPS
            } else {
                locationNet ?: locationGPS
            }
        } else {
            return null
        }
    }

    fun getCoordinates(response: String, context: Context): PolylineOptions? {
        val obj = Gson().fromJson(response, GoogleMapsResponse::class.java)
        val coordinates = PolylineOptions()
        return if(obj.routes?.isNotEmpty() == true) {
            val steps = obj.routes?.get(0)?.legs?.get(0)?.steps
            for (step in steps.orEmpty()) {
                decodePoly(step.polyline?.points.toString(), coordinates)
            }
            coordinates.color(ContextCompat.getColor(context, R.color.indigo_900_dark)).width(15f)
        }else{
            null
        }
    }

    private fun decodePoly(encoded: String, coordinates: PolylineOptions) {
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            coordinates.add(p)
        }
    }

    fun getParameters(myLocation: LatLng, viewModel: MainViewModel): String {
        val origin = "origin=" + myLocation.latitude + "," + myLocation.longitude + "&"
        val destination = "destination=" +
                viewModel.adapterState.camera?.latitude +
                "," + viewModel.adapterState.camera?.longitude + "&"
        return origin +
                destination +
                "sensor=false&mode=driving&key="
    }

}