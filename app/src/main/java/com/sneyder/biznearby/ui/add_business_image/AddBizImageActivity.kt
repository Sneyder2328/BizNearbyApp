package com.sneyder.biznearby.ui.add_business_image

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_add_biz_image.*


class AddBizImageActivity : DaggerActivity() {

    companion object {

        const val EXTRA_IMAGE_URL = "imageUrl"
        const val EXTRA_IMAGE_PATH = "imagePath"

        fun starterIntent(context: Context, imagePath: String): Intent {
            val starter = Intent(context, AddBizImageActivity::class.java)
            starter.putExtra(EXTRA_IMAGE_PATH, imagePath)
            return starter
        }

    }

    private val viewModel by viewModels<AddBizImageViewModel> { viewModelFactory }

    private val imagePath: String? by lazy {
        intent.getStringExtra(EXTRA_IMAGE_PATH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_biz_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Glide.with(this).load(imagePath).centerCrop().into(businessImgImageView)
        observeUploadImage()
    }


    private var progressDialog: ProgressDialog? = null

    private fun observeUploadImage() {
        viewModel.imageUploadResult.observe(this) {
            when {
                it.isLoading -> {
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setCancelable(false)
                    progressDialog?.setMessage("Subiendo imagen...")
                    progressDialog?.show()
                }
                it.success != null -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                    respondWithImageUrl(it.success)
                }
                it.error != null -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                    it.error.printStackTrace()
                    Toast.makeText(
                        this,
                        "Hubo un error en la subida de la imagen",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun respondWithImageUrl(imgUrl: String) {
        val data = Intent()
        data.putExtra(EXTRA_IMAGE_URL, imgUrl)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_add_biz_image_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> viewModel.uploadBusinessImage(imagePath ?: return true)
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}