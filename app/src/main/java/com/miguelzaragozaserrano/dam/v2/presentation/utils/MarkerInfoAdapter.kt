package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.miguelzaragozaserrano.dam.v2.databinding.MarkerInfoBinding

class MarkerInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View {
        val layoutInflater = (context as Activity).layoutInflater
        val binding = MarkerInfoBinding.inflate(layoutInflater)
        val camera: MyCluster? = marker?.tag as MyCluster?
        with(binding){
            markerTitle.text = camera?.title
            markerImage.apply {
                bindImageViewMarker(camera?.url, marker)
            }
        }
        return binding.root
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

}