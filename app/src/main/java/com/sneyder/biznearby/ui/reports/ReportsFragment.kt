package com.sneyder.biznearby.ui.reports

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report.*

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportsFragment : DaggerFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ReportsFragment()
    }

    private val viewModel by viewModels<ReportsViewModel> { viewModelFactory }
    private val reportsAdapter by lazy { ReportsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpRecyclerView()
        viewModel.fetchReports("All")
        observeReports()
    }

    private fun setUpRecyclerView() {
        with(reportsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun observeReports() {
        viewModel.reports.observe(viewLifecycleOwner) {
            it?.success?.let { list ->
                reportsAdapter.reports = list
            }
        }
    }

}