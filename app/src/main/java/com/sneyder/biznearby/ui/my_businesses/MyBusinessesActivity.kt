package com.sneyder.biznearby.ui.my_businesses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.add_business.AddBusinessActivity
import com.sneyder.biznearby.ui.business_details.BusinessDetailsActivity
import com.sneyder.biznearby.utils.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_my_businesses.*

class MyBusinessesActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, MyBusinessesActivity::class.java)
        }

    }

    private val viewModel by viewModels<MyBusinessesViewModel> { viewModelFactory }
    private val myBusinessesAdapter by lazy {
        MyBusinessesAdapter(onBusinessSelected = { businessId ->
            startActivity(BusinessDetailsActivity.starterIntent(this, businessId))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_businesses)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpRecyclerView()
        addBusinessButton.setOnClickListener { startActivity(AddBusinessActivity.starterIntent(this)) }
        myBusinessesRefreshLayout.setOnRefreshListener { viewModel.loadMyBusinesses() }
        observeMyBusinesses()
    }

    private fun setUpRecyclerView() {
        with(myBusinessesRecyclerView) {
            layoutManager = LinearLayoutManager(this@MyBusinessesActivity)
            adapter = myBusinessesAdapter
        }
    }

    private fun observeMyBusinesses() {
        viewModel.myBusinesses.observe(this) {
            when {
                it.isLoading -> {
                    myBusinessesRefreshLayout.isRefreshing = true
                }
                it.success != null -> {
                    myBusinessesRefreshLayout.isRefreshing = false
                    myBusinessesAdapter.myBusinesses = it.success
                }
                else -> {
                    myBusinessesRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMyBusinesses()
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