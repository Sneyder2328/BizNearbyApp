package com.sneyder.biznearby.ui.pick_city

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.model.CityLocation
import kotlinx.android.synthetic.main.city_location_item.view.*

class PickCityAdapter(
    private val onCitySelected: (city: CityLocation) -> Unit
) : RecyclerView.Adapter<PickCityAdapter.CityViewHolder>() {

    var cities: List<CityLocation> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.city_location_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.count()

    inner class CityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val cityTextView: TextView by lazy { view.cityTextView }

        fun bind(city: CityLocation) {
            cityTextView.text = "${city.city.name}, ${city.state.name}"
            cityTextView.setOnClickListener { onCitySelected(city) }
        }
    }
}