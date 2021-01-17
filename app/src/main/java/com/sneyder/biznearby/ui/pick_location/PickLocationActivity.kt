package com.sneyder.biznearby.ui.pick_location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug


class PickLocationActivity : DaggerActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var googleMap: GoogleMap

    companion object {

        const val EXTRA_BUSINESS_NAME = "businessName"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val UNREACHABLE_GRADE = 500.0

        fun starterIntent(
            context: Context,
            businessName: String,
            latitude: Double? = null,
            longitude: Double? = null
        ): Intent {
            val starter = Intent(context, PickLocationActivity::class.java)
            businessName.let { starter.putExtra(EXTRA_BUSINESS_NAME, it) }
            latitude?.let { starter.putExtra(EXTRA_LATITUDE, it) }
            longitude?.let { starter.putExtra(EXTRA_LONGITUDE, it) }
            return starter
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getLocationProvider(locationManager: LocationManager?): String? {
        return when {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true -> LocationManager.GPS_PROVIDER
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true -> LocationManager.NETWORK_PROVIDER
            else -> null
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
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        ifHasPermission(
            permissionsToAskFor = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 101, {
                debug("PickLocationAativity onMapReady permission for location  = true")
                googleMap.isMyLocationEnabled = true
                val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, UNREACHABLE_GRADE)
                    .let { if (it != UNREACHABLE_GRADE) it else null }
                val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, UNREACHABLE_GRADE)
                    .let { if (it != UNREACHABLE_GRADE) it else null }
                if (latitude != null && longitude != null) {
                    moveMapCamera(LatLng(latitude, longitude))
                } else {
                    displayUserLocation()
                }
            })
        googleMap.setOnMapClickListener { loc ->
            debug("clicked map at $loc")
            if (loc == null) return@setOnMapClickListener
            addMarkerAtPosition(loc)
            Handler(Looper.getMainLooper()).postDelayed({
                showConfirmLocationDialog(loc)
            }, 1000)
        }
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun addMarkerAtPosition(loc: LatLng) {
        val title = intent?.getStringExtra(EXTRA_BUSINESS_NAME)
            .let { if (it != null) "Ubicación de $it" else "Ubicación" }
        googleMap.addMarker(
            MarkerOptions()
                .position(loc)
                .title(title)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)))
        )
    }

    @SuppressLint("MissingPermission")
    private fun displayUserLocation() {
        val locManager = getSystemService(LOCATION_SERVICE) as? LocationManager

        val location = locManager?.getLastKnownLocation(
            getLocationProvider(locManager) ?: return
        )
        if (location != null) {
            debug("my location= $location")
            moveMapCamera(LatLng(location.latitude, location.longitude))
        } else {
            registerLocationListener(locManager)
        }
    }

    private fun showConfirmLocationDialog(loc: LatLng) {
        debug("showConfirmLocationDialog $loc")
        AlertDialog.Builder(this)
            .setTitle("Confirmar ubicación")
            .setMessage("¿La ubicacion seleccionada es la correcta?")
            .setIcon(android.R.drawable.ic_dialog_map)
            .setCancelable(false)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                googleMap.clear()
                val data = Intent()
                data.putExtra(EXTRA_LATITUDE, loc.latitude)
                data.putExtra(EXTRA_LONGITUDE, loc.longitude)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                googleMap.clear()
            }.show()
    }

    private fun moveMapCamera(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(location) // Sets the center of the map to the user's location
            .zoom(17f)            // Sets the zoom
            .build()              // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
        debug("onLocationChanged $location")
        if (location == null) return
        moveMapCamera(LatLng(location.latitude, location.longitude))
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}