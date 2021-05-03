package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.data.models.Camera
import com.miguelzaragozaserrano.dam.v2.data.models.MyCluster
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentMapBinding
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.GOOGLE_MAP_DIR
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.REQUEST_CODE
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.getLastLocation
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.getParameters
import org.koin.android.ext.android.inject

class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

    private val icon: BitmapDescriptor by lazy {
        val color = getColor(requireContext(), R.color.green_500)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_camera, color)
    }

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
        with(binding) {
            iconLocation.apply {
                setOnClickListener {
                    getLocationPermission()
                }
            }
            chipGroup.apply {
                setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        binding.hybridChip.id -> {
                            viewModel.mapViewState.mapType = GoogleMap.MAP_TYPE_HYBRID
                            googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
                        }
                        binding.satelliteChip.id -> {
                            viewModel.mapViewState.mapType = GoogleMap.MAP_TYPE_SATELLITE
                            googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        }
                        binding.normalChip.id -> {
                            viewModel.mapViewState.mapType = GoogleMap.MAP_TYPE_NORMAL
                            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                        }
                        binding.topographicalChip.id -> {
                            viewModel.mapViewState.mapType = GoogleMap.MAP_TYPE_TERRAIN
                            googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        }
                    }
                }
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
        with(viewModel.mapViewState) {
            polyline = null
            urlPolyline = ""
        }
        findNavController().navigate(R.id.action_map_fragment_to_cameras_fragment)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        bindMapView()
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
            if (googleMap?.isMyLocationEnabled == false) {
                googleMap?.isMyLocationEnabled = true
                viewModel.mapViewState.locationEnable = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                binding.iconLocation.setImageResource(R.drawable.ic_marker_on)
                getDirection()
            } else {
                googleMap?.isMyLocationEnabled = false
                viewModel.mapViewState.locationEnable = false
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                viewModel.mapViewState.polyline?.remove()
                viewModel.mapViewState.urlPolyline = null
                binding.iconLocation.setImageResource(R.drawable.ic_marker_off)
            }
        } else {
            askPermissions()
        }
    }

    private fun getDirection() {
        val location = getLastLocation(requireContext())
        location?.let {
            val myLocation = LatLng(location.latitude, location.longitude)
            val urlDirection = GOOGLE_MAP_DIR + getParameters(
                myLocation,
                viewModel
            ) + getString(R.string.google_maps_key)
            with(viewModel.mapViewState) {
                urlPolyline = urlDirection
                loadURL(urlDirection)
            }
        }
    }

    private fun loadURL(url: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val response = StringRequest(Request.Method.GET, url, { response ->
            val coordinates = Utils.getCoordinates(response, requireContext())
            viewModel.mapViewState.polyline = googleMap?.addPolyline(coordinates)
        }, {})
        queue.add(response)
    }

    private fun askPermissions() {
        requestPermissions(
            arrayOf(
                fineLocationPermission,
                coarseLocationPermission
            ), REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun bindMapView() {
        with(viewModel) {
            if (mapViewState.locationEnable) {
                binding.iconLocation.setImageResource(R.drawable.ic_marker_on)
            } else {
                binding.iconLocation.setImageResource(R.drawable.ic_marker_off)
            }
            mapViewState.urlPolyline?.let { url ->
                loadURL(url)
            }
            googleMap?.isMyLocationEnabled = mapViewState.locationEnable
            googleMap?.uiSettings?.isMyLocationButtonEnabled = mapViewState.locationEnable
            googleMap?.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))
            googleMap?.mapType = mapViewState.mapType
            if (settingsMapState.cluster) {
                val clusterManager: ClusterManager<MyCluster> =
                    ClusterManager(requireContext(), googleMap)
                googleMap?.setOnCameraIdleListener(clusterManager)
                googleMap?.setOnMarkerClickListener(clusterManager)
                for (camera in settingsMapState.cameras) {
                    val item =
                        MyCluster(
                            LatLng(camera.latitude.toDouble(), camera.longitude.toDouble()),
                            camera.name,
                            "${camera.latitude}, ${camera.latitude}"
                        )
                    clusterManager.addItem(item)
                    getZoom(camera)
                }
            } else {
                for (camera in settingsMapState.cameras) {
                    val marker = MarkerOptions()
                        .position(LatLng(camera.latitude.toDouble(), camera.longitude.toDouble()))
                        .title(camera.name)
                        .icon(icon)
                    googleMap?.addMarker(marker)?.tag = camera
                    getZoom(camera)
                }
            }
        }
    }

    private fun getZoom(camera: Camera) {
        if (camera.selected) {
            val cameraPosition =
                CameraPosition
                    .Builder()
                    .target(
                        LatLng(
                            camera.latitude.toDouble(),
                            camera.longitude.toDouble()
                        )
                    )
                    .zoom(12F).build()
            googleMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition)
            )
        }
    }

}