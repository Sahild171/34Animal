package com.application.animalAlertApp.View.Shops

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.FragmentCreateShopFragement1Binding
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*


@AndroidEntryPoint
class CreateShopFragement1 : Fragment() {
    private lateinit var binding: FragmentCreateShopFragement1Binding
    private val viewmodel: ShopViewModel by activityViewModels()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var PERMISSION_ID = 44
    private lateinit var prefrence: MySharedPreferences
    private lateinit var pd: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateShopFragement1Binding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        prefrence = MySharedPreferences(requireContext())
        pd = MyProgressBar.progress(context)

        binding.btContinue.setOnClickListener {
            if (validations()) {
                createshop()
            }
        }
        binding.tvLocation.setOnClickListener {
            getLastLocation()
        }

        getbusinessdata()


    }

    private fun getbusinessdata() {
        viewmodel.businessdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        if (it.value.getShop.size > 0) {
                            binding.etDescription.setText(it.value.getShop[0].businessDescription)
                            binding.etBusloc.setText(it.value.getShop[0].location)
                            binding.etPetname.setText(it.value.getShop[0].businessName)
                            binding.etBusmobile.setText(it.value.getShop[0].mobileNo)
                        }
                    } else {
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })
    }


    private fun validations(): Boolean {
        if (binding.etPetname.text.toString().trim().isEmpty()) {
            binding.etPetname.error = "Empty"
            return false
        } else if (binding.etDescription.text.toString().trim().isEmpty()) {
            binding.etDescription.error = "Empty"
            return false
        } else if (binding.etBusmobile.text.toString().trim().isEmpty()) {
            binding.etBusmobile.error = "Empty"
            return false
        } else if (binding.etBusloc.text.toString().trim().isEmpty()) {
            binding.etBusloc.error = "Empty"
            return false
        }
        return true
    }


    private fun createshop() {
        viewmodel.addshop(
            binding.etPetname.text.toString().trim(),
            binding.etDescription.text.toString().trim(),
            binding.etBusmobile.text.toString().trim(),
            binding.etBusloc.text.toString().trim()
        )
        viewmodel.shopdata.observe(requireActivity(), Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {

                        val intent = Intent()
                        intent.setAction("ChangeBusinessInfoStatus")
                        context?.sendBroadcast(intent)

                        (getActivity() as CreateShopActivity?)!!.changescreens()
                    } else {
                        context?.toast("" + it.value.message)
                        (getActivity() as CreateShopActivity?)!!.changescreens()
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception?.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Empty -> {
                    pd.dismiss()
                }
            }
        })
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
                                context,
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(context, "Please turn on" + " your location...", Toast.LENGTH_LONG)
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
                    context,
                    mLastLocation!!.latitude,
                    mLastLocation.longitude
                )
            )
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private fun requestPermissions() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), PERMISSION_ID
            )
        }
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager =
            context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
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