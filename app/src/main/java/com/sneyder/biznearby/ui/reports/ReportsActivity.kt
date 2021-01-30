package com.sneyder.biznearby.ui.reports

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_report.*

class ReportsActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, ReportsActivity::class.java)
        }

    }

    private val viewModel by viewModels<ReportsViewModel> { viewModelFactory }
    private val reportsAdapter by lazy {
        ReportsAdapter(onReportDeleted = { reportId ->
            viewModel.deleteReport(reportId)
        })
    }
    private var type: String = "All"
        set(value) {
            if (field != value) viewModel.fetchReports(value)
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpRecyclerView()
        observeReports()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchReports(type)
    }

    private fun setUpRecyclerView() {
        with(reportsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun observeReports() {
        viewModel.reports.observe(this) {
            it?.success?.let { list ->
                reportsAdapter.reports = list
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_report_menu, menu)
        val item = menu.findItem(R.id.type_spinner)
        val spinner = item.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.report_type_options, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                type = resources.getStringArray(R.array.report_type_options)[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}