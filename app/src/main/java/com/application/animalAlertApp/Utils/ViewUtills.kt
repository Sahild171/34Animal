package com.application.animalAlertApp.Utils

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.util.Patterns
import android.widget.Toast
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import kotlin.collections.ArrayList
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

var PACKAGE_NAME = "com.application.animalAlertApp"
var RECEIVER = "$PACKAGE_NAME.RECEIVER"
var RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
var LOCATION_LAT_EXTRA = "$PACKAGE_NAME.LOCATION_lAT_EXTRA"
var LOCATION_LNG_EXTRA = "$PACKAGE_NAME.LOCATION_LNG_EXTRA"
var SELECTED_ADDRESS = "$PACKAGE_NAME.SELECTED_ADDRESS"
var LANGUAGE = "$PACKAGE_NAME.LANGUAGE"
var API_KEY = "$PACKAGE_NAME.API_KEY"
var COUNTRY = "$PACKAGE_NAME.COUNTRY"
var SUPPORTED_AREAS = "$PACKAGE_NAME.SUPPORTED_AREAS"
var SUCCESS_RESULT = 0
var FAILURE_RESULT = 1
var SELECT_LOCATION_REQUEST_CODE = 22


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun isValidMobile(phone: String): Boolean {
    return Patterns.PHONE.matcher(phone).matches()
}

fun sKeyboard(context: Context) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager = activity.getSystemService(
        AppCompatActivity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    val view: View? = activity.currentFocus
    if (view != null) {
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }
}


        /// Convert timestamp to date
public fun getDateTime(s: String): String? {
    try {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(s.toLong() * 1000)
        return sdf.format(netDate)
    } catch (e: Exception) {
        return e.toString()
    }
}


fun selectpettype(array: ArrayList<String>, item: String): Int {
    for (i in 0..array.size - 1) {
        if (array[i].equals(item)) {
            return i
        }
    }
    return 0
}

fun SelectmyPet(array: ArrayList<String>, item: String): Int {
    for (i in 0..array.size - 1) {
        if (array[i].equals(item)) {
            return i
        }
    }
    return 0
}


fun SelectPriority(array: ArrayList<String>, item: String): Int {
    for (i in 0..array.size - 1) {
        if (array[i].equals(item)) {
            return i
        }
    }
    return 0
}


fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("hh:mm a")
    return format.format(date)
}


fun imageToString(bitmap: Bitmap): String {
    var bitmap = bitmap
    val maxSize = 800
    val outWidth: Int
    val outHeight: Int
    val inWidth = bitmap.width
    val inHeight = bitmap.height
    if (inWidth > inHeight) {
        outWidth = maxSize
        outHeight = inHeight * maxSize / inWidth
    } else {
        outHeight = maxSize
        outWidth = inWidth * maxSize / inHeight
    }
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val imgBytes = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(imgBytes, Base64.DEFAULT)
}


fun changeformat(date: String): String {
    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val output = SimpleDateFormat("dd MMMM yyyy, hh:mm a")
    var d: Date? = null
    try {
        input.timeZone = TimeZone.getTimeZone("UTC")
        d = input.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    output.timeZone = TimeZone.getDefault()
    val formatted = output.format(d)
    Log.i("DATE", "" + formatted)
    return formatted
}


//call the extension function on a date object


fun isDeviceLocked(context: Context): Boolean {
    var isLocked = false

    // First we check the locked state
    val keyguardManager =
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    val inKeyguardRestrictedInputMode =
        keyguardManager.inKeyguardRestrictedInputMode()
    isLocked = if (inKeyguardRestrictedInputMode) {
        true
    } else {
        // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
        // so we need to check if screen on for this case
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            !powerManager.isInteractive
        } else {
            !powerManager.isScreenOn
        }
    }
    return isLocked
}


fun checkForInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    // if the android version is equal to M
    // or greater we need to use the
    // NetworkCapabilities to check what type of
    // network has the internet connection
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false
        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            // else return false
            else -> false
        }
    } else {
        // if the android version is below M
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}

