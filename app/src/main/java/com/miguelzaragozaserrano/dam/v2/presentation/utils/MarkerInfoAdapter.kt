package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.data.models.Camera

class MarkerInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View? {
        val camera = marker?.tag as? Camera ?: return null
        val view = LayoutInflater.from(context).inflate(R.layout.marker_info, null)
        view.findViewById<TextView>(
            R.id.marker_title
        ).text = camera.name
        view.findViewById<AppCompatImageView>(
            R.id.marker_image
        ).bindImageViewMarker(camera.url)
        return view
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

}