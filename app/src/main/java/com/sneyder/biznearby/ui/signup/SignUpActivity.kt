package com.sneyder.biznearby.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.auth.TypeLogin
import com.sneyder.biznearby.utils.ImageSelectorUtils
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import com.sneyder.biznearby.utils.dialogs.SelectImageDialog
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


data class Country(val name: String, val code: String, @DrawableRes val icon: Int)

val countries = arrayOf(
    Country("Venezuela", "58", R.drawable.ve),
    Country("Colombia", "57", R.drawable.co),
    Country("Estados unidos", "1", R.drawable.us),
)

class SignUpActivity : DaggerActivity(), SelectImageDialog.SelectImageListener {

    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    companion object {

        const val REQUEST_SIGN_IN = 1
        const val REQUEST_PICK_PHOTO = 100
        const val REQUEST_TAKE_PHOTO = 101

        fun starterIntent(context: Context): Intent {
            val starter = Intent(context, SignUpActivity::class.java)
            //starter.putExtra(EXTRA_, )
            return starter
        }

    }

    private var countrySelected = 0
        set(value) {
            codeTextView?.text = countries[value].code
            flagImageView?.setImageResource(countries[value].icon)
            field = value
        }
    private lateinit var googleSignInClient: GoogleSignInClient

    //private val callbackManager by lazy { CallbackManager.Factory.create() }
    private var imageProfilePath: String? = null
        set(value) {
            field = value
            debug("set imageProfilePath = $value")
            Glide.with(this).load(value).placeholder(R.drawable.person_placeholder).into(photoImageView)
        }
    private val viewModel: SignUpViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        countryPhoneCodeView.setOnClickListener {
            showCountryCodeDialog()
        }
        cameraButton.setOnClickListener {
            showPickImageBottomSheet()
        }
        signUpButton.setOnClickListener { signUpWithEmail() }
        setUpGoogleLogin()
        //setUpFacebookLogin()
    }

    private fun signUpWithEmail() {
        val fullname = fullnameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val repeatPassword = passwordRepeatEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()

        viewModel.signUp(fullname = fullname, email = email, password = password, typeLogin = TypeLogin.Email, imageProfilePath = imageProfilePath)
    }

    private fun showPickImageBottomSheet() {
        val selectImageDialog = SelectImageDialog.newInstance()
        selectImageDialog.show(supportFragmentManager, selectImageDialog.tag)
    }

    override fun onTakePicture() {
        launchTakePictureIntent()
    }

    override fun onPickImage() {
        launchImageSelectorIntent()
    }

    private fun launchTakePictureIntent() {
        ifHasPermission(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            ), REQUEST_TAKE_PHOTO, {
                // If the app got the permission, dispatch the capture image intent
//            startActivityForResult(TakePictureActivity.starterIntent(this), REQUEST_TAKE_PHOTO)

                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(packageManager) == null)
                        return@ifHasPermission
                    // Create the File where the photo should go
                    val photoFile: File = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Toast.makeText(
                            this,
                            "Error occurred while launching the camera",
                            Toast.LENGTH_LONG
                        ).show()
                        return@ifHasPermission
                    } catch (ex: Exception) {
                        return@ifHasPermission
                    }
                    // Continue only if the File was successfully created

                    debug("photofile=$photoFile")
                    val photoURI: Uri = FileProvider.getUriForFile(
                        applicationContext,
                        "com.sneyder.biznearby.android.fileprovider",
                        photoFile
                    )
//                    imageProfilePath = photoFile.path
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            })
    }

    private fun launchImageSelectorIntent() {
        ifHasPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PICK_PHOTO, {
            val intent = ImageSelectorUtils.getImageSelectionIntent()
            startActivityForResult(intent, REQUEST_PICK_PHOTO)
        })
    }

    override fun onRemoveImage() {
        debug("onRemoveImage")
        imageProfilePath = null
    }

    /*private fun setUpFacebookLogin() {
        // Callback registration
        signUpFacebookButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
        }

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("SignUpActivity", "onSuccess: loginRsult=$loginResult")
                handleFacebookSignInResult(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("SignUpActivity", "onCancel: onCancel FacebookCallback")
            }

            override fun onError(exception: FacebookException) {
                Log.e("SignUpActivity", "onError: FacebookCallback ${exception.message}")
                exception.printStackTrace()
            }
        })
    }*/
/*
    private fun handleFacebookSignInResult(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(currentAccessToken) { jsonObject, response ->
            try {
                val picture: JSONObject? = jsonObject.getJSONObject("picture")
                val url = picture?.getJSONObject("data")?.get("url")?.toString() ?: ""
                val name = jsonObject.getString("groupName")
                val email = jsonObject.getString("email")
                val id = jsonObject.getString("id")
                Log.d("SignUpActivity", "handleFacebookSignInResult: ${jsonObject}")
                Log.d("SignUpActivity", "handleFacebookSignInResult: response=${response}")
//                debug("onSuccess FacebookCallback token=${currentAccessToken.token}")
//                debug("newMeRequest groupName=$name email=$email id=$id picture=$picture  url = $url")
                LoginManager.getInstance()
                    .logOut() // Signed in successfully, logOut and open RegisterActivity.
//                startActivity(RegisterActivity.starterIntent(this@SignUpActivity, TypeLogin.FACEBOOK.data, email = email,
//                    userId = id, username = name, photoUrl = url, accessToken = currentAccessToken.token))
//                finish()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,groupName,email,picture.width(512)")
        request.parameters = parameters
        request.executeAsync()
    }*/


    private fun setUpGoogleLogin() {
        // Configure sign-in to request the myUserInfo's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signUpGoogleButton.setSize(SignInButton.SIZE_ICON_ONLY)
        signUpGoogleButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_SIGN_IN)
        }
    }

    private fun handleGoogleSignInResult(result: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount? = result?.getResult(ApiException::class.java)
            Log.d(
                "SignUpActivity",
                "handleGoogleSignInResult2 idToken = ${account?.idToken} userId = ${account?.id} ${account?.email} ${account?.serverAuthCode} ${account?.displayName} ${account?.photoUrl}"
            )
            googleSignInClient.signOut() // Signed in successfully, logOut and open RegisterActivity.
//            startActivity(RegisterActivity.starterIntent(this@SignUpActivity, TypeLogin.GOOGLE.data, email = account.email,
//                userId = account.id, username = account.displayName, photoUrl = account.photoUrl.toString(), accessToken = account.idToken))
//            finish()
        } catch (e: ApiException) {
            // The ApiException syncStatus code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            toast(R.string.signup_message_unable_google_login)
            Log.e("SignUpActivity", "handleGoogleSignInResult: code = ${e.statusCode}")
            e.printStackTrace()
        } catch (e: Exception) {
//            toast(R.string.signup_message_unable_google_login)
            Log.e("SignUpActivity", "handleGoogleSignInResult: e = ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        debug("onActivityResult $requestCode")
        when (requestCode) {
            REQUEST_SIGN_IN -> {
                val result = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSignInResult(result)
            }
            REQUEST_PICK_PHOTO -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val selectedImage: Uri? = data.data
                val path = ImageSelectorUtils.getFilePathFromUri(this, selectedImage)
                debug("REQUEST_PICK_PHOTO: $path")
                imageProfilePath = path
//                File(path) // get file pointing to it
            }
            REQUEST_TAKE_PHOTO -> {// a existing image has been selected
                if (resultCode != Activity.RESULT_OK) return
                imageProfilePath = currentPhotoPath
                debug("REQUEST_TAKE_PHOTO: ${data}")
            }
        }
        /*else {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }*/
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