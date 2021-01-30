package com.sneyder.biznearby.ui.my_businesses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.Business
import kotlinx.android.synthetic.main.activity_my_businesses_item.view.*

class MyBusinessesAdapter(
    private val onBusinessSelected: (businessId: String) -> Unit
) : RecyclerView.Adapter<MyBusinessesAdapter.MyBusinessViewHolder>() {

    var myBusinesses: List<Business> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBusinessViewHolder {
        return MyBusinessViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_my_businesses_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyBusinessViewHolder, position: Int) {
        holder.bind(myBusinesses[position])
    }

    override fun getItemCount(): Int = myBusinesses.count()

    inner class MyBusinessViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(business: Business) {
            view.nameTextView.text = business.name
            view.descriptionTextView.text = business.description
            view.setOnClickListener {
                onBusinessSelected(business.id)
            }
        }

    }
}