package com.sneyder.biznearby.ui.add_business.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerFragment

class ScheduleFragment : DaggerFragment() {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}