package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.Menu
import androidx.core.app.ActivityCompat
import com.miguelzaragozaserrano.dam.v2.R
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
        }else {
            return null
        }
    }

}