package com.sneyder.biznearby.ui.moderators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerFragment
import kotlinx.android.synthetic.main.fragment_moderators.*

class ModeratorsFragment : DaggerFragment() {

    companion object {
        fun newInstance() = ModeratorsFragment()
    }

    private val viewModel: ModeratorsViewModel by viewModels { viewModelFactory }
    private val moderatorsAdapter by lazy {
        ModeratorsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moderators, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpRecyclerView()
        observeModerators()
        viewModel.fetchModerators()
    }

    private fun setUpRecyclerView() {
        with(moderatorsRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moderatorsAdapter
        }
    }

    private fun observeModerators() {
        viewModel.moderators.observe(viewLifecycleOwner) {
            it.success?.let { profiles ->
                moderatorsAdapter.moderators = profiles
            }
        }
    }

}