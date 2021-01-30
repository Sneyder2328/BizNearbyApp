package com.sneyder.biznearby.ui.add_business

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.AddressForm
import com.sneyder.biznearby.data.model.business.BasicInfo
import com.sneyder.biznearby.data.model.business.toAddress
import com.sneyder.biznearby.ui.add_business.address.AddressFragment
import com.sneyder.biznearby.ui.add_business.basic_info.BasicInfoFragment
import com.sneyder.biznearby.ui.add_business.images.ImagesFragment
import com.sneyder.biznearby.ui.add_business.schedule.ScheduleFragment
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.activity_add_business.*
import java.util.*

class AddBusinessActivity : DaggerActivity() {

    companion object {

        const val NUM_FRAGMENTS = 4

        fun starterIntent(context: Context): Intent {
            return Intent(context, AddBusinessActivity::class.java)
        }

    }

    var currentBasicInfo: BasicInfo = BasicInfo()
    var currentAddress: AddressForm = AddressForm()
    var currentImages: MutableList<String> = mutableListOf()
    private val viewModel: AddBusinessViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_business)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpViewPager()

        backButton.setOnClickListener {
            if (canGoBack()) goBackOnePage()
            else finish()
        }
        nextButton.setOnClickListener {
            if (!currentFragmentHasValidInputs()) return@setOnClickListener
            if (canGoForward()) goForwardOnePage()
            else saveBusiness()
        }

        observeBusinessCreated()
    }

    private var progressDialog: ProgressDialog? = null

    private fun observeBusinessCreated() {
        viewModel.businessCreated.observe(this) {
            when {
                it.isLoading -> {
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setCancelable(false)
                    progressDialog?.setMessage("Guardando registro...")
                    progressDialog?.show()
                }
                it.success != null -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                    Toast.makeText(this, "Negocio registrado exitosamente!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                    Toast.makeText(this, "Hubo un error guardando el registro", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveBusiness() {
        debug("saveBusiness")
        viewModel.addNewBusiness(
            name = currentBasicInfo.name,
            description = currentBasicInfo.description,
            bannerUrl = currentBasicInfo.bannerUrl ?: "",
            categories = currentBasicInfo.categories,
            address = currentAddress.toAddress(),
            businessId = UUID.randomUUID().toString(),
            images = currentImages
        )
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            debug("onPageSelected $position")
            backButton.text = if (canGoBack()) "Atras" else "Cancelar"
            nextButton.text = if (canGoForward()) "Siguiente" else "Guardar"
        }
    }

    private fun setUpViewPager() {
        viewPager.adapter = ScreenSlidePagerAdapter(this)
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager?.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    private fun canGoBack(): Boolean {
        return viewPager.currentItem != 0
    }

    private fun canGoForward(): Boolean {
        return viewPager.currentItem + 1 != NUM_FRAGMENTS
    }

    private fun currentFragmentHasValidInputs(): Boolean {
        val currentFragment =
            (supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}") as? InputsValidation)
        if (currentFragment?.areAllInputsValid() == false) return false
        return true
    }

    private fun goBackOnePage() {
        viewPager.currentItem = viewPager.currentItem - 1
        updateIndicators()
    }

    private fun goForwardOnePage() {
        viewPager.currentItem = viewPager.currentItem + 1
        updateIndicators()
    }

    private fun updateIndicators() {
        when (viewPager.currentItem) {
            0 -> {
                indicator1.setBackgroundResource(R.drawable.circle_light)
                indicator2.setBackgroundResource(R.drawable.circle_dark)
                indicator3.setBackgroundResource(R.drawable.circle_dark)
                indicator4.setBackgroundResource(R.drawable.circle_dark)
            }
            1 -> {
                indicator1.setBackgroundResource(R.drawable.circle_dark)
                indicator2.setBackgroundResource(R.drawable.circle_light)
                indicator3.setBackgroundResource(R.drawable.circle_dark)
                indicator4.setBackgroundResource(R.drawable.circle_dark)
            }
            2 -> {
                indicator1.setBackgroundResource(R.drawable.circle_dark)
                indicator2.setBackgroundResource(R.drawable.circle_dark)
                indicator3.setBackgroundResource(R.drawable.circle_light)
                indicator4.setBackgroundResource(R.drawable.circle_dark)
            }
            else -> {
                indicator1.setBackgroundResource(R.drawable.circle_dark)
                indicator2.setBackgroundResource(R.drawable.circle_dark)
                indicator3.setBackgroundResource(R.drawable.circle_dark)
                indicator4.setBackgroundResource(R.drawable.circle_light)
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

    override fun onBackPressed() {
        if (canGoBack()) return goBackOnePage()
        // If the user is currently looking at the first step, allow the system to handle the
        // Back button. This calls finish() on this activity and pops the back stack.
        super.onBackPressed()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = NUM_FRAGMENTS

        override fun createFragment(position: Int): Fragment {
            debug("createFragment $position")
            return when (position) {
                0 -> BasicInfoFragment.newInstance()
                1 -> AddressFragment.newInstance()
                2 -> ScheduleFragment.newInstance()
                else -> ImagesFragment.newInstance()
            }
        }
    }

    interface InputsValidation {
        fun areAllInputsValid(): Boolean
    }

}