@file:Suppress("unused")

package com.sneyder.biznearby.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import java.io.UnsupportedEncodingException
import com.sneyder.biznearby.data.model.Result
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun Context.createImageFile(): Pair<File, String> {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
    return file to file.absolutePath
}

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}

fun <T> throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}

fun <T> debounce(
    waitMs: Long = 500L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun getLocationProvider(locationManager: LocationManager?): String? {
    return when {
        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true -> LocationManager.GPS_PROVIDER
        locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true -> LocationManager.NETWORK_PROVIDER
        else -> null
    }
}

fun genRequestBody(content: String): RequestBody = RequestBody.create(MultipartBody.FORM, content)

/**
 * More info at: https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android/39923767#39923767
 */
fun getMimeType(url: String?): String {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ROOT))
    }
    if (type == null) {
        type = "image/*" // fallback type. You might set it to */*
    }
    return type
}

fun View.displayLongTextSnackBar(message: String) {
    val snackbar: Snackbar = Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    )
    val snackbarView = snackbar.view
    val snackTextView =
        snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as? TextView
    snackTextView?.maxLines = 3
    snackbar.show()
}

fun View.displayLongTextSnackBarWithOkBtn(message: String) {
    val snackbar: Snackbar = Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_INDEFINITE
    )
    val snackbarView = snackbar.view
    val snackTextView =
        snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as? TextView
    snackTextView?.maxLines = 3
    snackbar.setAction(android.R.string.ok) { snackbar.dismiss() }
    snackbar.show()
}

/**
 * Checks for network availability
 * NOTE: Don't forget to add android.permission.ACCESS_NETWORK_STATE permission to manifest
 */
@Suppress("DEPRECATION")
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (connectivityManager != null) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        } else {
            try {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return false
}

fun Context.screenWidth(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun Context.screenHeight(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

fun Context.notificationManager(): NotificationManager =
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.locationManager(): LocationManager =
    getSystemService(Context.LOCATION_SERVICE) as LocationManager

@Throws(UnsupportedEncodingException::class)
private fun getJson(strEncoded: String): String {
    val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
    return String(decodedBytes, Charsets.UTF_8)
}

// Keyboard utils
fun Activity.hideSoftInput() {
    var view = currentFocus
    if (view == null) view = View(this)
    hideSoftInput(view)
}

fun Context.hideSoftInput(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showSoftInput(editText: EditText? = null) {
    if (editText != null) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, 0)
    } else {
        toggleSoftInput()
    }
}

fun Context.toggleSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}


suspend fun <T> safeApiCall(sth: suspend () -> T?, onFinally: () -> Unit = {}): Result<T> {
    return try {
        Result(sth()).also {
            onFinally()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onFinally()
        Result(error = e)
    }
}

/**
 * Verify Google Play Services is available and in case it is not show an ErrorDialog
 */
fun Activity.isGooglePlayServicesAvailable(): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val status = googleApiAvailability.isGooglePlayServicesAvailable(applicationContext)
    if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status)) {
            googleApiAvailability.getErrorDialog(this, status, 10).show()
        }
        return false
    }
    return true
}