package com.sneyder.biznearby.ui.add_report

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.Report
import com.sneyder.biznearby.utils.FieldsValidator
import com.sneyder.biznearby.utils.InputValidator
import com.sneyder.biznearby.utils.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_add_report.*
import kotlinx.android.synthetic.main.activity_add_report.descriptionEditText

class AddReportActivity : DaggerActivity() {

    companion object {

        private const val EXTRA_BUSINESS_ID = "businessId"
        private const val EXTRA_BUSINESS_NAME = "businessName"

        fun starterIntent(context: Context, businessId: String, businessName: String): Intent {
            val starter = Intent(context, AddReportActivity::class.java)
            starter.putExtra(EXTRA_BUSINESS_ID, businessId)
            starter.putExtra(EXTRA_BUSINESS_NAME, businessName)
            return starter
        }

    }

    private val viewModel: AddReportViewModel by viewModels { viewModelFactory }
    private val businessId: String? by lazy { intent.getStringExtra(EXTRA_BUSINESS_ID) }
    private val businessName: String? by lazy { intent.getStringExtra(EXTRA_BUSINESS_NAME) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_report)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nameTextView.text = "Negocio: $businessName"
        reportButton.setOnClickListener { saveReport() }
        observeReportCreated()
    }

    private fun saveReport() {
        val fieldsValidator = FieldsValidator()
        val title = InputValidator.Builder(titleEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        val description = InputValidator.Builder(descriptionEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        if (!fieldsValidator.allInputsValid()) return
        viewModel.addReport(
            title = title,
            description = description,
            businessId = businessId ?: return
        )
    }

    private var progressDialog: ProgressDialog? = null

    private fun observeReportCreated() {
        viewModel.reportCreated.observe(this) {
            when {
                it.isLoading -> {
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setCancelable(false)
                    progressDialog?.setMessage("Creando reporte...")
                    progressDialog?.show()
                }
                it.success != null -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                    Toast.makeText(this, "Reporte creado exitosamente!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                    Toast.makeText(this, "Hubo un error creando el reporte", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}