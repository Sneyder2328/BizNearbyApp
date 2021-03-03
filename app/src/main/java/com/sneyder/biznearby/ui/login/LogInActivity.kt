package com.sneyder.biznearby.ui.login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.auth.GoogleAuth
import com.sneyder.biznearby.data.model.auth.TypeLogin
import com.sneyder.biznearby.ui.home.HomeActivity
import com.sneyder.biznearby.ui.signup.SignUpActivity
import com.sneyder.biznearby.utils.FieldsValidator
import com.sneyder.biznearby.utils.InputValidator
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.emailEditText
import kotlinx.android.synthetic.main.activity_log_in.passwordEditText
import kotlinx.android.synthetic.main.activity_log_in.registerTextView

class LogInActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            val starter = Intent(context, LogInActivity::class.java)
            //starter.putExtra(EXTRA_, )
            return starter
        }

    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: LogInViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpGoogleLogin()
        registerTextView.setOnClickListener {
            startActivity(SignUpActivity.starterIntent(this))
            finish()
        }
        logInButton.setOnClickListener { logInWithEmail() }
        observeUserLoggedIn()
    }

    private var progressDialog: ProgressDialog? = null

    private fun observeUserLoggedIn() {
        viewModel.userLoggedIn.observe(this) {
            debug("observeUserLoggedIn $it")
            when {
                it.isLoading -> {
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setCancelable(false)
                    progressDialog?.setMessage("Iniciando sesión...")
                    progressDialog?.show()
                }
                it.success != null -> {
                    if (progressDialog?.isShowing == true) progressDialog?.dismiss()
                    startActivity(HomeActivity.starterIntent(this))
                    finish()
                }
                it.error != null -> {
                    if (progressDialog?.isShowing == true) progressDialog?.dismiss()
                    it.error.printStackTrace()
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

    private fun setUpGoogleLogin() {
        // Configure sign-in to request the myUserInfo's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        logInGoogleButton?.setSize(SignInButton.SIZE_ICON_ONLY)
        logInGoogleButton?.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, SignUpActivity.REQUEST_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        debug("onActivityResult $requestCode")
        when (requestCode) {
            SignUpActivity.REQUEST_SIGN_IN -> {
                val result = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSignInResult(result)
            }
        }
    }

    private fun handleGoogleSignInResult(result: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount? = result?.getResult(ApiException::class.java)
            if (account == null) {
                Toast.makeText(this, "Hubo un error en el registro con Google", Toast.LENGTH_LONG)
                    .show()
                return
            }
            Log.d(
                "LogInActivity",
                "handleGoogleSignInResult2 idToken = ${account?.idToken} userId = ${account?.id} ${account?.email} ${account?.serverAuthCode} ${account?.displayName} ${account?.photoUrl}"
            )
            logInWithGoogle(googleUserId = account.id?:return, idToken = account.idToken ?: return, email = account.email ?: return)
            googleSignInClient.signOut() // Signed in successfully, logOut and open RegisterActivity.
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logInWithGoogle(googleUserId: String, idToken: String, email: String) {
        viewModel.logIn(
            googleAuth = GoogleAuth(googleUserId, idToken),
            email = email,
            typeLogin = TypeLogin.GOOGLE
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}