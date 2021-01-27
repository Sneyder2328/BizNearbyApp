package com.sneyder.biznearby.ui.explore.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.BizResult
import kotlinx.android.synthetic.main.fragment_explore_result_item.view.*
import kotlin.math.roundToInt

class ResultsAdapter(
    private val onBusinessSelected: (businessId: String) -> Unit
) : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    var results: ArrayList<BizResult> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_explore_result_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int = results.count()

    inner class ResultViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(bizResult: BizResult) {
            view.titleTextView.text = bizResult.name
            view.subtitleTextView.text = bizResult.address.address
            view.distanceTextView.text = "${bizResult.distance.roundToInt()}m"
            view.setOnClickListener {
                onBusinessSelected(bizResult.id)
            }
        }
    }

}