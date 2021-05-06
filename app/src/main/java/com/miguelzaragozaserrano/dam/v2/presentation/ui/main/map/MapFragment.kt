package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
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
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.presentation.utils.MyCluster
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentMapBinding
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.REQUEST_CODE
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.getCoordinates
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.getParameters
import org.koin.android.ext.android.inject

class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

    private val icon: BitmapDescriptor by lazy {
        val color = getColor(requireContext(), R.color.indigo_900)
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
            chipGroup.apply {
                setOnCheckedChangeListener { _, checkedId ->
                    binding.bindChipGroup(
                        checkedId = checkedId,
                        viewModel = viewModel,
                        googleMap = googleMap
                    )
                }
            }
        }
    }

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        setupToolbar(
            toolbar = binding.toolbarComponent.toolbar,
            titleId = R.string.map_fragment_title,
            menuId = R.menu.menu_map,
            navigationIdIcon = R.drawable.ic_arrow_back,
            functionOnCreateOptionsMenu = { menu -> bindToolbar(menu) }
        )
        initMap()
    }

    private fun bindToolbar(menu: Menu) {
        with(viewModel) {
            if (mapViewState.locationEnable) {
                menu.findItem(R.id.location_icon).icon =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_marker_on)
            } else {
                menu.findItem(R.id.location_icon).icon =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_marker_off)
            }
        }
    }

    override fun toolbarItemSelected(itemSelected: MenuItem, menu: Menu) {
        super.toolbarItemSelected(itemSelected, menu)
        when (itemSelected.itemId) {
            R.id.location_icon -> {
                if (viewModel.mapViewState.locationEnable) {
                    menu.findItem(R.id.location_icon).icon =
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_marker_off)
                } else {
                    menu.findItem(R.id.location_icon).icon =
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_marker_on)
                }
                getLocationPermission()
            }
        }
    }

    override fun onBackPressedFun() {
        super.onBackPressedFun()
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
                showDialogMessageSimple(
                    title = getString(R.string.route_title),
                    message = getString(R.string.route_message),
                    positiveText = getString(R.string.calculate_button),
                    icon = R.drawable.ic_route,
                    functionPositiveButton = {
                        viewModel.mapViewState.route = true
                        getDirection()
                    }
                )
            } else {
                googleMap?.isMyLocationEnabled = false
                viewModel.mapViewState.locationEnable = false
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                viewModel.mapViewState.polyline?.remove()
                viewModel.mapViewState.urlPolyline = null
                viewModel.mapViewState.route = false
            }
        } else {
            askPermissions()
        }
    }

    private fun getDirection() {
        val location = getLastLocation(requireContext())
        location?.let {
            val myLocation = LatLng(location.latitude, location.longitude)
            val urlDirection =
                Constants.GOOGLE_MAP_DIR + getParameters(
                    myLocation,
                    viewModel
                ) + getString(R.string.google_maps_key)
            with(viewModel.mapViewState) {
                urlPolyline = urlDirection
                loadURL(urlDirection, false)
            }
        }
    }

    private fun loadURL(url: String?, rotationScreen: Boolean) {
        val queue = Volley.newRequestQueue(requireContext())
        val response = StringRequest(Request.Method.GET, url, { response ->
            val coordinates = getCoordinates(response, requireContext())
            if (coordinates != null) {
                viewModel.mapViewState.polyline = googleMap?.addPolyline(coordinates)
            } else {
                if (!rotationScreen) {
                    showSnackLong(
                        view,
                        getString(R.string.route_error),
                        context,
                        R.color.red_600,
                        R.color.white_50
                    )
                }
            }
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
            if (mapViewState.route) {
                mapViewState.urlPolyline?.let { url ->
                    loadURL(url, true)
                }
            }
            googleMap?.isMyLocationEnabled = mapViewState.locationEnable
            googleMap?.uiSettings?.isMyLocationButtonEnabled = mapViewState.locationEnable
            googleMap?.mapType = mapViewState.mapType
            if (settingsMapState.cluster) {
                addCamerasWithCluster()
            } else {
                addCamerasWithoutCluster()
            }
            googleMap?.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))
        }
    }

    private fun addCamerasWithCluster() {
        val clusterManager: ClusterManager<MyCluster> =
            ClusterManager(requireContext(), googleMap)
        clusterManager.renderer =
            googleMap?.let { map ->
                CameraRenderer(
                    context = requireContext(),
                    map = map,
                    clusterManager = clusterManager
                )
            }
        clusterManager.markerCollection.setInfoWindowAdapter(
            MarkerInfoAdapter(
                requireContext()
            )
        )
        for (camera in viewModel.settingsMapState.cameras) {
            val item =
                MyCluster(
                    LatLng(camera.latitude.toDouble(), camera.longitude.toDouble()),
                    camera.name,
                    "${camera.latitude}, ${camera.latitude}",
                    camera.url
                )
            clusterManager.addItem(item)
            getZoom(camera, viewModel.settingsMapState.cameras)
        }
        googleMap?.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
        }
    }

    private fun addCamerasWithoutCluster() {
        for (camera in viewModel.settingsMapState.cameras) {
            val marker = MarkerOptions()
                .position(LatLng(camera.latitude.toDouble(), camera.longitude.toDouble()))
                .title(camera.name)
                .icon(icon)
            googleMap?.addMarker(marker)?.tag = MyCluster(
                LatLng(camera.latitude.toDouble(), camera.longitude.toDouble()),
                camera.name,
                "${camera.latitude}, ${camera.latitude}",
                camera.url
            )
            getZoom(camera, viewModel.settingsMapState.cameras)
        }
    }

    private fun getZoom(camera: Camera, cameras: List<Camera>) {
        val zoom = if (cameras.size > 1) {
            12F
        } else {
            20F
        }
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
                    .zoom(zoom).build()
            googleMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition)
            )
        }
    }

    private fun getLastLocation(context: Context): Location? {
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

}