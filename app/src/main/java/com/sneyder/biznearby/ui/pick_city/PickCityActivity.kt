package com.sneyder.biznearby.ui.pick_city

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.model.CityLocation
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.activity_pick_city.*
import java.util.*

class PickCityActivity : DaggerActivity() {

    companion object {

        const val EXTRA_CITY_CODE = "cityCode"
        const val EXTRA_CITY_NAME = "cityName"

        fun starterIntent(context: Context): Intent {
            val starter = Intent(context, PickCityActivity::class.java)
            //starter.putExtra(EXTRA_, )
            return starter
        }

    }

    private val viewModel: PickCityViewModel by viewModels { viewModelFactory }
    private val citiesAdapter by lazy {
        PickCityAdapter(onCitySelected = { city ->
            respondWithCityCode(city)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_city)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setUpRecyclerView()
//        saveButton.setOnClickListener {
//            respondWithCityCode()
//        }
//        cancelButton.setOnClickListener {
//            respondWithCancel()
//        }
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                debug("onTextChanged $s")
            }

            override fun afterTextChanged(s: Editable) {
                debug("afterTextChanged $s")
                viewModel.getLocations(s.toString())
            }
        })
        observeCityLocations()
    }

    private fun setUpRecyclerView() {
        with(citiesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
        }
    }

    private fun respondWithCityCode(city: CityLocation) {
        val data = Intent()
        data.putExtra(EXTRA_CITY_CODE, city.city.code)
        data.putExtra(EXTRA_CITY_NAME, "${city.city.name}, ${city.state.name}")
        setResult(RESULT_OK, data)
        finish()
    }

    private fun observeCityLocations() {
        viewModel.locations.observe(this) {
            it.success?.let { cities -> updateAdapter(cities) }
        }
    }

    private fun updateAdapter(cities: ArrayList<CityLocation>) {
        citiesAdapter.cities = cities
    }
}