package com.sneyder.biznearby.ui.moderators

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.dialogs.EditTextDialog
import kotlinx.android.synthetic.main.activity_moderators.*

class ModeratorsActivity : DaggerActivity(), EditTextDialog.EditTextDialogListener {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, ModeratorsActivity::class.java)
        }

    }

    private val viewModel: ModeratorsViewModel by viewModels { viewModelFactory }
    private val moderatorsAdapter by lazy {
        ModeratorsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moderators)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpRecyclerView()
        moderatorRefreshLayout.setOnRefreshListener { viewModel.fetchModerators() }
        addModeratorButton.setOnClickListener {
            showAddModeratorDialog()
        }
        observeModerators()
        observeAddModerator()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchModerators()
    }

    private fun observeAddModerator() {
        viewModel.addModeratorResult.observe(this) {
            if (it.success != null) {
                viewModel.fetchModerators()
            }
        }
    }

    private fun showAddModeratorDialog() {
        val addModeratorDialog =
            EditTextDialog.newInstance("Agregar moderador", "", "Correo electronico")
        addModeratorDialog.show(supportFragmentManager, addModeratorDialog.tag)
    }

    override fun onTextEntered(text: String) {
        val email = text.trim()
        viewModel.addModerator(email)
    }

    private fun setUpRecyclerView() {
        with(moderatorsRecyclerView) {
            layoutManager = LinearLayoutManager(this@ModeratorsActivity)
            adapter = moderatorsAdapter
        }
    }

    private fun observeModerators() {
        viewModel.moderators.observe(this) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}