package com.sneyder.biznearby.ui.add_business

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.pick_location.PickLocationActivity
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.fragment_add_business.*

class AddBusinessFragment : DaggerFragment() {

    companion object {
        const val REQUEST_LOCATION_DETAILS = 10002
        const val UNREACHABLE_GRADE = 500.0
        fun newInstance() = AddBusinessFragment()
    }

    private val viewModel: AddBusinessViewModel by viewModels { viewModelFactory }
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_business, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addressTextView.setOnClickListener {
            startActivityForResult(
                PickLocationActivity.starterIntent(
                    requireContext(),
                    nameEditText.text.toString(),
                    latitude, longitude
                ),
                REQUEST_LOCATION_DETAILS
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug("AddByzFr onActivityResult $requestCode $resultCode $data")
        if (requestCode == REQUEST_LOCATION_DETAILS && resultCode == Activity.RESULT_OK && data != null) {
            latitude =
                data.getDoubleExtra(PickLocationActivity.EXTRA_LATITUDE, UNREACHABLE_GRADE)
                    .let { if (it != UNREACHABLE_GRADE) it else null }
            longitude =
                data.getDoubleExtra(PickLocationActivity.EXTRA_LONGITUDE, UNREACHABLE_GRADE)
                    .let { if (it != UNREACHABLE_GRADE) it else null }
            debug("onActivityResult success $latitude $longitude")
        }
    }
}