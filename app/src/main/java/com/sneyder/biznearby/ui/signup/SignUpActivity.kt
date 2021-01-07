package com.sneyder.biznearby.ui.signup

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sneyder.biznearby.R
import kotlinx.android.synthetic.main.activity_sign_up.*

data class Country(val name: String, val code: String, @DrawableRes val icon: Int)

val countries = arrayOf(
    Country("Venezuela", "58", R.drawable.ve),
    Country("Colombia", "57", R.drawable.co),
    Country("Estados unidos", "1", R.drawable.us),
)

class SignUpActivity : AppCompatActivity() {

    private var countrySelected = 0
        set(value) {
            codeTextView?.text = countries[value].code
            flagImageView?.setImageResource(countries[value].icon)
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        countryPhoneCodeView.setOnClickListener {
            showCountryCodeDialog()
        }
    }

    private fun showCountryCodeDialog() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("Selecciona un país")
        dialog.setSingleChoiceItems(
            countries.map { it.name }.toTypedArray(), countrySelected
        ) { d, item ->
            countrySelected = item
            d.dismiss()
        }
        val alert: AlertDialog = dialog.create()
        alert.show()
    }
}