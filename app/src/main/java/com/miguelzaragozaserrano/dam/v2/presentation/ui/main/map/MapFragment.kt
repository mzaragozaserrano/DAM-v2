package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.data.dto.response.GoogleMapsResponse
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentMapBinding
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.REQUEST_CODE
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.getLastLocation
import com.miguelzaragozaserrano.dam.v2.presentation.utils.ViewModelFactory
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindMapView
import org.koin.android.ext.android.inject

class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {

    private var mapView: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private var polyline: Polyline? = null


    private val factory: ViewModelFactory by inject()
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_main) {
        factory
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)

    override fun setup2Listeners() {
        super.setup2Listeners()
        binding.iconLocation.apply {
            setOnClickListener {
                getLocationPermission()
            }
        }
    }

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        setupToolbar(
            toolbar = binding.toolbarComponent.toolbar,
            titleId = R.string.map_fragment_title,
            menuId = null,
            navigationIdIcon = R.drawable.ic_arrow_back
        )
        initMap()
    }

    override fun onBackPressedFun() {
        super.onBackPressedFun()
        findNavController().navigate(R.id.action_map_fragment_to_cameras_fragment)
    }

    override fun onMapReady(mapView: GoogleMap?) {
        this.mapView = mapView
        binding.bindMapView(
            context = requireContext(),
            mapView = mapView,
            cameras = viewModel.settingsMapState.cameras,
            cluster = viewModel.settingsMapState.cluster
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getLocationPermission()
                }
            }
        }
    }

    private fun initMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (mapView?.isMyLocationEnabled == false) {
                /*if(hasConnection(getApplication<Application>())){
                    *//* Si hay conexión, obtenemos los permisos y cambiamos el icono *//*
                getLocationPermission()
                binding.iconLocation.setImageResource(R.drawable.ic_marker_on)
            }else
            *//* Si no hay conexión, mostramos un mensaje *//*
                Toast
                    .makeText(context, context.getString(R.string.check_connection), Toast.LENGTH_LONG)
                    .show()*/
                mapView?.isMyLocationEnabled = true
                mapView?.uiSettings?.isMyLocationButtonEnabled = true
                getLocation()
                binding.iconLocation.setImageResource(R.drawable.ic_marker_on)
            } else {
                mapView?.isMyLocationEnabled = false
                mapView?.uiSettings?.isMyLocationButtonEnabled = false
                polyline?.remove()
                binding.iconLocation.setImageResource(R.drawable.ic_marker_off)
            }
        } else {
            askPermissions()
        }
    }

    private fun getLocation() {
        val location = getLastLocation(requireContext())
        location?.let {
            val myLocation = LatLng(location.latitude, location.longitude)
            mapView?.uiSettings?.isMyLocationButtonEnabled = true
            val origin = "origin=" + myLocation.latitude + "," + myLocation.longitude + "&"
            val destination = "destination=" +
                    viewModel.adapterState.camera?.latitude +
                    "," + viewModel.adapterState.camera?.longitude + "&"
            val parameters = origin +
                    destination +
                    "sensor=false&mode=driving&key=" +
                    getString(R.string.google_maps_key)
            loadURL("https://maps.googleapis.com/maps/api/directions/json?$parameters")
        }
    }

    private fun askPermissions() {
        requestPermissions(
            arrayOf(
                fineLocationPermission
            ), REQUEST_CODE
        )
    }

    private fun loadURL(url: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val request = StringRequest(Request.Method.GET, url, { response ->
            val coordinates = getCoordinates(response)
            polyline = mapView?.addPolyline(coordinates)
        }, {})
        queue.add(request)
    }

    private fun getCoordinates(response: String): PolylineOptions {
        val obj = Gson().fromJson(response, GoogleMapsResponse::class.java)
        val steps = obj.routes?.get(0)?.legs?.get(0)?.steps
        val coordinates = PolylineOptions()
        for (step in steps.orEmpty()) {
            decodePoly(step.polyline?.points.toString(), coordinates)
        }
        coordinates.color(getColor(requireContext(), R.color.indigo_900_dark)).width(15f)
        return coordinates
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

}