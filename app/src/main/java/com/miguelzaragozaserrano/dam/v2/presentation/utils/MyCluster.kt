package com.miguelzaragozaserrano.dam.v2.presentation.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyCluster(
    private val position: LatLng,
    private val title: String,
    private val snippet: String,
    var url: String
) : ClusterItem {

    override fun getPosition(): LatLng = position

    override fun getTitle(): String = title

    override fun getSnippet(): String = snippet

}