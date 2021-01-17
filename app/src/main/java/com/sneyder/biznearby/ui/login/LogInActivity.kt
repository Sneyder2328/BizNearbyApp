package com.sneyder.biznearby.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.auth.TypeLogin
import com.sneyder.biznearby.ui.home.HomeActivity
import com.sneyder.biznearby.ui.signup.SignUpActivity
import com.sneyder.biznearby.utils.FieldsValidator
import com.sneyder.biznearby.utils.InputValidator
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            val starter = Intent(context, LogInActivity::class.java)
            //starter.putExtra(EXTRA_, )
            return starter
        }

    }

    private val viewModel: LogInViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        registerTextView.setOnClickListener {
            startActivity(SignUpActivity.starterIntent(this))
            finish()
        }
        logInButton.setOnClickListener { logInWithEmail() }
        observeUserLoggedIn()
    }

    private fun observeUserLoggedIn() {
        viewModel.userLoggedIn.observe(this) {
            debug("observeUserLoggedIn $it")
            when {
                it.isLoading -> {
//                    linealProgressIndicator.visibility = View.VISIBLE
//                    creatingAccountTextView.visibility = View.VISIBLE
                }
                it.success != null -> {
                    startActivity(HomeActivity.starterIntent(this))
                    finish()
                }
                it.error != null -> {
                    it.error.printStackTrace()
//                    logInLayout.displayLongTextSnackBar(it.error.message ?: return@observe)
//                    linealProgressIndicator.visibility = View.INVISIBLE
//                    creatingAccountTextView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun logInWithEmail() {
        val fieldsValidator = FieldsValidator()
        val email = InputValidator.Builder(emailEditText)
            .maxLength(255)
            .required(message = "Por favor ingresar su correo electronico")
            .build(fieldsValidator)
        val password = InputValidator.Builder(passwordEditText)
            .required(message = "Por favor ingresar su contraseña")
            .build(fieldsValidator)
        if (!fieldsValidator.allInputsValid()) return
        viewModel.logIn(
            email = email,
            password = password,
            typeLogin = TypeLogin.EMAIL
        )
    }

}