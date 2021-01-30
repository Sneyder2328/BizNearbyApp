package com.sneyder.biznearby.ui.reports

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.Report
import kotlinx.android.synthetic.main.activity_reports_item.view.*

class ReportsAdapter(
    private val onReportSelected: (reportId: String) -> Unit,
    private val onReportDeleted: (reportId: String) -> Unit
) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    var reports = ArrayList<Report>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        return ReportViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_reports_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount(): Int = reports.count()

    inner class ReportViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(report: Report) {
            if (report.reviewedAt != null) {
                view.reportContainer.setBackgroundColor(Color.rgb(239, 239, 239))
            } else {
                view.reportContainer.setBackgroundColor(Color.rgb(255, 255, 255))
            }
            view.titleTextView.text = report.title
            view.descriptionTextView.text = report.description
            view.deleteButton.setOnClickListener { onReportDeleted(report.id) }
            view.setOnClickListener { onReportSelected(report.id) }
        }

    }
}