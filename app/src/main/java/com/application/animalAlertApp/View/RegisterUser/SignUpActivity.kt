package com.application.animalAlertApp.View.RegisterUser

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Auth.LoginActivity
import com.application.animalAlertApp.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import android.content.pm.PackageManager
import android.location.Address
import androidx.core.app.ActivityCompat
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import com.application.animalAlertApp.Locations.MapsActivity
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.View.Main.HomeActivity
import com.application.animalAlertApp.View.Otp.OtpVerificationActivity
import com.application.animalAlertApp.View.Prefrence.SelectPrefrenceActivity
import com.application.animalAlertApp.data.Response.Auth.FindUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.onStart
import java.lang.Exception
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private val viewModel: RegisterUserViewModel by viewModels()
    private lateinit var binding: ActivitySignUpBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var PERMISSION_ID = 44
    private lateinit var auth: FirebaseAuth
    private var lati: String = ""
    private var longi: String = ""
    private var dev_token: String = ""
    private val Req_Code: Int = 123
    private lateinit var pd: Dialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = MyProgressBar.progress(this@SignUpActivity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        registerclick()
        gotologin()
        getLastLocation()
        googlesignin()


        binding.etLoc.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent,3)
            //  getLastLocation()
            //   setupPermissions()

        }

    }

    private fun googlesignin() {
        binding.btSignigoogle.setOnClickListener {
            getFCMToken("SocailLogin")
            // signInGoogle()
        }
    }

    private fun getFCMToken(type: String) {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    toast("Please try again later.")
                    Log.w("ExceptionFCM", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                dev_token = task.result
                Log.e("FCMToken", "==" + dev_token)
                if (type.equals("Simple")) {
                    doregister()
                } else {
                    signInGoogle()
                }

            })
    }


    private fun registerclick() {
        binding.apply {
            btSignup.setOnClickListener {
                getFCMToken("Simple")
            }
        }
    }


    private fun doregister() {
        binding.apply {
            if (validations()) {
                lifecycleScope.launchWhenStarted {
                    viewModel.register(
                        etMobile.text.toString(),
                        etPetemail.text.toString(),
                        etName.text.toString(),
                        etLoc.text.toString(),
                        etPassword.text.toString(), "+91", lati, longi, dev_token
                    ).catch { e ->
                        toast("" + e.message)
                        Log.e("error1", "" + e.message)
                        pd.dismiss()
                    }.onStart {
                        pd.show()
                    }.collect { response ->
                        if (response.status == 200) {
                            pd.dismiss()
                            viewModel.saveCredentials(
                                this@SignUpActivity,
                                response.user._id, response.token, response.user.name, dev_token
                            )
                            viewModel.saveCredentials1(
                                this@SignUpActivity,
                                response.user.name,
                                response.user.email,
                                response.user.phoneNo,
                                response.user.location,
                                ""
                            )
                            val intent =
                                Intent(this@SignUpActivity, OtpVerificationActivity::class.java)
                            intent.putExtra("otp", response.user.otp)
                            startActivity(intent)
                        } else {
                            pd.dismiss()
                            Log.e("error2", "" + response.message)
                            toast("" + response.message)
                        }
                    }
                }
            }
        }
    }


    private fun validations(): Boolean {
        binding.apply {
            val name = etName.text.toString().trim()
            val email = etPetemail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val loc = etLoc.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Enter Name"
                toast("Enter Name")
                return false
            } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etPetemail.error = "Enter Valid Email"
                toast("Enter Valid Email")
                return false
            } else if (mobile.isEmpty()) {
                etMobile.error = "Enter Mobile number"
                toast("Enter Mobile number")
                return false
            } else if (loc.isEmpty()) {
                etLoc.error = "Enter Location"
                toast("Enter Location")
                return false
            } else if (password.length < 5) {
                //  etPassword.error = "Enter Password"
                toast("Password Should be minimum 5 Characters")
                return false
            }
            return true
        }
    }


    fun gotologin() {
        binding.textLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }else if (requestCode==3){
            if (data!=null){
                val address: String = data.getStringExtra("Address").toString()
                val lat: String = data.getStringExtra("Lat").toString()
                val long: String = data.getStringExtra("Long").toString()
                Handler().postDelayed({
                    binding.etLoc.setText(address)
                    lati=lat
                    longi=long
                },500)


            }
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                socailogin(account.email!!, account.displayName!!)
                //   toast("" + account.email + "" + account.displayName+" "+dev_token)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun socailogin(email: String, name: String) {
        binding.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.sociallogin(email, name, dev_token).catch { e ->
                    toast("" + e.message)
                    pd.dismiss()
                }.onStart {
                    pd.show()
                }.collect { response ->
                    if (response.status == 200) {
                        viewModel.saveCredentials(
                            this@SignUpActivity,
                            response.updateToken._id,
                            response.updateToken.jwtToken,
                            response.updateToken.name, response.updateToken.deviceToken
                        )
                        viewModel.saveCredentials1(
                            this@SignUpActivity,
                            response.updateToken.name,
                            response.updateToken.email,
                            response.updateToken.phoneNo,
                            response.updateToken.location,
                            response.updateToken.image
                        )
                        screennavigation(response.updateToken)
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                        toast("" + response.message)
                    }
                }
            }
        }
    }

    private fun screennavigation(data: FindUser) {
        if (!data.isPerferenceSet) {
            val intent = Intent(this@SignUpActivity, SelectPrefrenceActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun registerwithFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    toast("sucess")
                    Log.e("firebase", "signup sucess")
                    // Sign in success, update UI with the signed-in user's information

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        lati = location.latitude.toString()
                        longi = location.longitude.toString()
                        binding.etLoc.setText(
                            getAddressFromLatLng(
                                this@SignUpActivity,
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Looper.myLooper()?.let {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                it
            )
        }
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            lati = mLastLocation!!.latitude.toString()
            longi = mLastLocation.longitude.toString()
            binding.etLoc.setText(
                getAddressFromLatLng(
                    this@SignUpActivity,
                    mLastLocation.latitude,
                    mLastLocation.longitude
                )
            )
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@SignUpActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this@SignUpActivity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    // method to request for permissions
    private fun requestPermissions() {
        requestPermissions(
            this@SignUpActivity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                toast("denied")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }


    fun getAddressFromLatLng(context: Context?, lat: Double, longi: Double): String? {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(lat, longi, 1)
            addresses[0].locality + ", " + addresses[0].adminArea
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }




//    private fun setupPermissions() {
//        val permission = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_FINE_LOCATION)
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "Permission to record denied")
//            makeRequest()
//        }
//    }
//
//    private fun makeRequest() {
//        requestPermissions(this,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
//            RECORD_REQUEST_CODE)
//    }

//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            RECORD_REQUEST_CODE -> {
//                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    toast("Denied")
//                    Log.i(TAG, "Permission has been denied by user")
//                } else {
//                    Log.i(TAG, "Permission has been granted by user")
//                    toast("Granted")
//                }
//            }
//        }
//    }
}