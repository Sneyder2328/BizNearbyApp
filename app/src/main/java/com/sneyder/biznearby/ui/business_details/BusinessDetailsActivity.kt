package com.sneyder.biznearby.ui.business_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.add_report.AddReportActivity
import com.sneyder.biznearby.utils.GridSpacingItemDecoration
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import com.sneyder.biznearby.utils.px
import kotlinx.android.synthetic.main.activity_business_details.*

class BusinessDetailsActivity : DaggerActivity() {

    companion object {
        private const val EXTRA_BUSINESS_ID = "businessId"

        fun starterIntent(context: Context, businessId: String): Intent {
            val starter = Intent(context, BusinessDetailsActivity::class.java)
            starter.putExtra(EXTRA_BUSINESS_ID, businessId)
            return starter
        }

    }

    private val businessId: String? by lazy { intent.getStringExtra(EXTRA_BUSINESS_ID) }
    private val viewModel by viewModels<BusinessDetailsViewModel> { viewModelFactory }
    private val imagesAdapter by lazy {
        BusinessDetailsImageAdapter {
            debug("img clicked $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpRecyclerView()
        observeBusiness()
        businessId?.let { viewModel.loadBusinessById(it) }
    }

    private fun setUpRecyclerView() {
        with(imagesRecyclerView) {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridSpacingItemDecoration(3, 16.px, false))
            setHasFixedSize(true)
            adapter = imagesAdapter
        }
    }

    private fun observeBusiness() {
        viewModel.business.observe(this) { bizResult ->
            bizResult?.success?.let {
                supportActionBar?.title = it.name
                nameTextView.text = it.name
                descriptionTextView.text = it.description
                imagesAdapter.images = it.images
                if (it.bannerUrl.isNotEmpty()) {
                    Glide.with(this).load(it.bannerUrl).centerCrop().into(bannerImageView)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_details_business_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_report -> {
                val business = viewModel.business.value?.success ?: return true
                startActivity(
                    AddReportActivity.starterIntent(
                        this,
                        businessId = business.id,
                        businessName = business.name
                    )
                )
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}