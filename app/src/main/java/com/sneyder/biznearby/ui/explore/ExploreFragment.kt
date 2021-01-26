package com.sneyder.biznearby.ui.explore

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.explore.results.ResultsAdapter
import com.sneyder.biznearby.utils.debug
import com.sneyder.biznearby.utils.getLocationProvider
import com.sneyder.biznearby.utils.locationManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.fragment_explore.*
import java.lang.Exception

class ExploreFragment : DaggerFragment(), OnMapReadyCallback, LocationListener {

    private val viewModel: ExploreViewModel by viewModels { viewModelFactory }
    private var googleMap: GoogleMap? = null
    private var currentLatLng: LatLng? = null
        set(value) {
            field = value
            if (value != null) queryEditText.visibility = View.VISIBLE
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    private val resultsAdapter by lazy {
        ResultsAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        setUpRecyclerView()
        observeResultsBusinesses()
        queryEditText.addTextChangedListener { text ->
            debug("after text changed $text")
            currentLatLng?.let {
                viewModel.searchBusinesses(text.toString(), it.latitude, it.longitude, 2000)
            }
        }
    }

    private fun setUpRecyclerView() {
        with(resultsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = resultsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun observeResultsBusinesses() {
        viewModel.resultBusinesses.observe(viewLifecycleOwner) {
            debug("observe results = $it")
            it.success?.let { results ->
                resultsAdapter.results = results
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        try {
            googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json))
        } catch (e: Exception){}
        ifHasPermission(
            permissionsToAskFor = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 101, {
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                googleMap?.isMyLocationEnabled = true
                displayUserLocation()
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun displayUserLocation() {
        val locManager = requireContext().locationManager()

        val location = locManager.getLastKnownLocation(
            getLocationProvider(locManager) ?: return
        )
        if (location != null) {
            debug("my location= $location")
            currentLatLng = LatLng(location.latitude, location.longitude)
            moveMapCamera()
        } else {
            registerLocationListener(locManager)
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener(locationManager: LocationManager?) {
        locationManager?.requestSingleUpdate(
            getLocationProvider(locationManager) ?: return,
            this,
            null
        )
    }

    override fun onLocationChanged(location: Location?) {
        debug("ExploreFr onLocationChanged $location")
        if (location == null) return
        currentLatLng = LatLng(location.latitude, location.longitude)
        moveMapCamera()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}

    private fun moveMapCamera() {
        if (currentLatLng == null) return
        val cameraPosition = CameraPosition.Builder()
            .target(currentLatLng) // Sets the center of the map to the user's location
            .zoom(17f)            // Sets the zoom
            .build()              // Creates a CameraPosition from the builder
        googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}