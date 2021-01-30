package com.sneyder.biznearby.ui.add_business.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.add_business.AddBusinessActivity
import com.sneyder.biznearby.ui.add_business.AddBusinessFragment
import com.sneyder.biznearby.ui.pick_city.PickCityActivity
import com.sneyder.biznearby.ui.pick_location.PickLocationActivity
import com.sneyder.biznearby.utils.FieldsValidator
import com.sneyder.biznearby.utils.InputValidator
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.fragment_address.*

class AddressFragment : DaggerFragment(), AddBusinessActivity.InputsValidation, OnMapReadyCallback {

    companion object {
        const val REQUEST_LOCATION_DETAILS = 10002
        const val REQUEST_CITY = 10003

        fun newInstance() = AddressFragment()
    }

    private var googleMap: GoogleMap? = null

    private val currentBasicInfo by lazy {
        (activity as? AddBusinessActivity)?.currentBasicInfo
    }
    private val currentAddress by lazy {
        (activity as? AddBusinessActivity)?.currentAddress
    }
    private var locationSelected: LatLng? = null
        set(value) {
            field = value
            pickLocationTextView?.text =
                if (value != null) "Editar ubicación exacta" else "Seleccionar ubicacián exacta"
        }
    private var cityCodeSelected: Int? = null
    private var cityDescSelected: String? = null
        set(value) {
            field = value
            value?.let { cityEditText?.setText(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        pickLocationTextView.setOnClickListener {
            startActivityForResult(
                PickLocationActivity.starterIntent(
                    requireContext(),
                    currentBasicInfo?.name ?: "",
                    locationSelected?.latitude, locationSelected?.longitude
                ),
                REQUEST_LOCATION_DETAILS
            )
        }
        cityEditText.setOnClickListener {
            startActivityForResult(PickCityActivity.starterIntent(requireContext()), REQUEST_CITY)
        }
    }

    override fun areAllInputsValid(): Boolean {
        val fieldsValidator = FieldsValidator()
        val addressLine = InputValidator.Builder(addressEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)

        currentAddress?.address = addressLine
        if (locationSelected == null) {
            Snackbar.make(
                addressContainer,
                "Por favor seleccione la ubicacion exacta del negocio",
                Snackbar.LENGTH_LONG
            ).show()
        }
        currentAddress?.latitude = locationSelected?.latitude ?: return false
        currentAddress?.longitude = locationSelected?.longitude ?: return false

        if (cityCodeSelected == null) {
            Snackbar.make(
                addressContainer,
                "Por favor seleccione la ciudad donde se encuentra el negocio",
                Snackbar.LENGTH_LONG
            ).show()
        }
        currentAddress?.cityCode = cityCodeSelected ?: return false

        return fieldsValidator.allInputsValid()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(10.08, -69.32)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug("AddByzFr onActivityResult $requestCode $resultCode $data")
        when (requestCode) {
            REQUEST_LOCATION_DETAILS -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val latitude =
                    data.getDoubleExtra(
                        PickLocationActivity.EXTRA_LATITUDE,
                        AddBusinessFragment.UNREACHABLE_GRADE
                    )
                        .let { if (it != AddBusinessFragment.UNREACHABLE_GRADE) it else null }
                val longitude =
                    data.getDoubleExtra(
                        PickLocationActivity.EXTRA_LONGITUDE,
                        AddBusinessFragment.UNREACHABLE_GRADE
                    )
                        .let { if (it != AddBusinessFragment.UNREACHABLE_GRADE) it else null }
                locationSelected = LatLng(latitude ?: return, longitude ?: return)
                debug("onActivityResult success $latitude $longitude")
                addMarkerToMap(locationSelected ?: return)
            }
            REQUEST_CITY -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                cityCodeSelected =
                    data.getIntExtra(
                        PickCityActivity.EXTRA_CITY_CODE,
                        AddBusinessFragment.UNREACHABLE_CITY_CODE
                    )
                        .let { if (it != AddBusinessFragment.UNREACHABLE_CITY_CODE) it else null }
                cityDescSelected = data.getStringExtra(PickCityActivity.EXTRA_CITY_NAME)
            }
        }
    }

    private fun addMarkerToMap(locationChosen: LatLng) {
        googleMap?.clear()
        googleMap?.addMarker(
            MarkerOptions()
                .position(locationChosen)
                .title("Ubicación de ${currentBasicInfo?.name}")
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(locationChosen))
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