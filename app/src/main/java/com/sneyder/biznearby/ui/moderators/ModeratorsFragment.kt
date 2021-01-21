package com.sneyder.biznearby.ui.moderators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.utils.dialogs.EditTextDialog
import kotlinx.android.synthetic.main.fragment_moderators.*

class ModeratorsFragment : DaggerFragment(), EditTextDialog.EditTextDialogListener {

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
        moderatorRefreshLayout.setOnRefreshListener { viewModel.fetchModerators() }
        addModeratorButton.setOnClickListener {
            showAddModeratorDialog()
        }
        observeModerators()
        observeAddModerator()
        viewModel.fetchModerators()
    }

    private fun observeAddModerator() {
        viewModel.addModeratorResult.observe(viewLifecycleOwner) {
            if (it.success != null) {
                viewModel.fetchModerators()
            }
        }
    }

    private fun showAddModeratorDialog() {
        val addModeratorDialog =
            EditTextDialog.newInstance("Agregar moderador", "", "Correo electronico")
        addModeratorDialog.show(childFragmentManager, addModeratorDialog.tag)
    }

    override fun onTextEntered(text: String) {
        val email = text.trim()
        viewModel.addModerator(email)
    }

    private fun setUpRecyclerView() {
        with(moderatorsRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moderatorsAdapter
        }
    }

    private fun observeModerators() {
        viewModel.moderators.observe(viewLifecycleOwner) {
            when {
                it.isLoading -> {
                    moderatorRefreshLayout.isRefreshing = true
                }
                it.success != null -> {
                    moderatorRefreshLayout.isRefreshing = false
                    moderatorsAdapter.moderators = it.success
                }
                else -> {
                    moderatorRefreshLayout.isRefreshing = false
                }
            }
        }
    }

}