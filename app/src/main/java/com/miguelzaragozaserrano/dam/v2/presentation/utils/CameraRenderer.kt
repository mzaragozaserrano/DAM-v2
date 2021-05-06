package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.miguelzaragozaserrano.dam.v2.R

class CameraRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MyCluster>
) : DefaultClusterRenderer<MyCluster>(context, map, clusterManager) {

    private val icon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(context, R.color.indigo_900)
        BitmapHelper.vectorToBitmap(context, R.drawable.ic_camera, color)
    }

    override fun onBeforeClusterItemRendered(
        item: MyCluster,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.title)
            .position(item.position)
            .icon(icon)
    }

    override fun onClusterItemRendered(clusterItem: MyCluster, marker: Marker) {
        marker.tag = clusterItem
    }

}