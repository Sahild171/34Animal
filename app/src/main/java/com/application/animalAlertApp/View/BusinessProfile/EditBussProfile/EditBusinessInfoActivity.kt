package com.application.animalAlertApp.View.BusinessProfile.EditBussProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityEditBusinessInfoBinding
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class EditBusinessInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBusinessInfoBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var PERMISSION_ID = 44
    private val viewmodel: EditBusinessViewModel by viewModels()
    private lateinit var pd: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBusinessInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd= MyProgressBar.progress(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val intent = intent
        if (intent.hasExtra("name")) {
            val name = intent.getStringExtra("name").toString()
            val location = intent.getStringExtra("location").toString()
            val mobile = intent.getStringExtra("mobile").toString()
            val description=intent.getStringExtra("description").toString()
            binding.etPetname.setText(name)
            binding.etBusmobile.setText(mobile)
            binding.etBusloc.setText(location)
            binding.etDescription.setText(description)
        }

        binding.btContinue.setOnClickListener {
            if (validations()) {
                viewmodel.editbusiness(
                    binding.etPetname.text.toString().trim(),
                    binding.etDescription.text.toString().trim(),
                    binding.etBusmobile.text.toString().trim(),
                    binding.etBusloc.text.toString().trim())
                viewmodel.editbussiness.observe(
                    this@EditBusinessInfoActivity,
                    androidx.lifecycle.Observer {
                        when (it) {
                            is Resource.Success -> {
                                if (it.value.status == 200) {
                                    pd.dismiss()
                                    val intent = Intent()
                                    intent.putExtra("name", it.value.businessName)
                                    intent.putExtra("businessdesc", it.value.businessDescription)
                                    intent.putExtra("location", it.value.location)
                                    intent.putExtra("mobile", it.value.mobileNo)
                                    intent.putExtra("description",it.value.businessDescription)
                                    setResult(1, intent)
                                    finish()
                                } else {
                                    pd.dismiss()
                                    toast("" + it.value.message)
                                }
                            }
                            is Resource.Failure -> {
                                pd.dismiss()
                                toast("" + it.exception!!.message)
                            }
                            is Resource.Loading -> {
                                pd.show()
                            }
                        }
                    })
            }
        }

        binding.tvLocation.setOnClickListener {
            getLastLocation()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun validations(): Boolean {
        if (binding.etPetname.text.toString().trim().isEmpty()) {
            binding.etPetname.error = "Empty"
            return false
        } else if (binding.etBusmobile.text.toString().trim().isEmpty()) {
            binding.etBusmobile.error = "Empty"
            return false
        } else if (binding.etBusloc.text.toString().trim().isEmpty()) {
            binding.etBusloc.error = "Empty"
            return false
        }else if (binding.etDescription.text.toString().trim().isEmpty()){
            binding.etDescription.error = "Empty"
            return false
        }
        return true
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
                        binding.etBusloc.setText(
                            getAddressFromLatLng(
                                this,
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show()
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
            binding.etBusloc.setText(
                getAddressFromLatLng(
                    this@EditBusinessInfoActivity,
                    mLastLocation!!.latitude,
                    mLastLocation.longitude
                )
            )
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }


    private fun getAddressFromLatLng(context: Context?, lat: Double, longi: Double): String? {
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


}