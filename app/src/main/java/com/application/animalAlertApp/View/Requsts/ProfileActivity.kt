package com.application.animalAlertApp.View.Requsts

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.OtherUserProfile.UserProfile
import com.application.animalAlertApp.databinding.ActivityProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import com.google.android.gms.maps.SupportMapFragment
import android.widget.Toast
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.animalAlertApp.Adapters.AddedPetAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Adapters.InfoWndowAdapter
import com.application.animalAlertApp.Interfaces.PetsUpdateInterface
import com.application.animalAlertApp.Interfaces.SeePetInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.MyDialog
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.data.Response.MessageXX
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.LatLngBounds


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, SeePetInterface {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: UserProfileViewModel by viewModels()
    private val requestViewModel: SendRequestViewModel by viewModels()
    private var requestStatus: String = ""
    private lateinit var userid: String
    private var map: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var currentLocation: Location? = null
    private var marker: Marker? = null
    private var mylatitude: Double? = 0.0
    private var mylongitude: Double? = 0.0
    private var otherlatitude: Double? = 0.0
    private var otherlongitude: Double? = 0.0
    private lateinit var adapter: AddedPetAdapter
    private lateinit var pet_list: ArrayList<MessageXX>
    private var see_size: Int? = null
    private lateinit var pd: Dialog
    var PERMISSION_ID = 44
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var userPreferences: UserPreferences
    private var myimage = ""
    private var mylocation = ""
    private var otheruserimage = ""
    private var otheruserlocation = ""
    private lateinit var sharedPreferences: MySharedPreferences
    var locationVisibile = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_profile)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userid = intent.getStringExtra("userid").toString()
        userPreferences = UserPreferences(this)
        sharedPreferences = MySharedPreferences(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        pd = MyProgressBar.progress(this)
        pet_list = ArrayList()

        intialize()

        userPreferences.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            myimage = it!!
        })
        userPreferences.getlocation.asLiveData().observe(this, androidx.lifecycle.Observer {
            mylocation = it!!
        })

        getuserdata(userid)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btConnect.setOnClickListener {
            if (checkForInternet(this)) {
                if (requestStatus.isEmpty() || requestStatus.equals("Pending")) {
                    sendrequest()
                }
            } else {
                toast("No Internet Connection")
            }
        }
        intializemap()
        getFCMToken()
    }

    private fun intializemap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("ExceptionFCM", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.e("FCMToken", "==" + token)
            })
    }

    private fun sendrequest() {
        lifecycleScope.launchWhenStarted {
            val pd: Dialog = MyProgressBar.progress(this@ProfileActivity)
            binding.apply {
                requestViewModel.sendrequest(userid, "").catch { e ->
                    toast("" + e.message)
                    pd.dismiss()
                }.onStart {
                    pd.show()
                }.collect {
                    if (it.status == 200) {
                        toast("Sent Request Successfully")
                        binding.btConnect.setText("Sent")
                        pd.dismiss()
                    } else {
                        toast("" + it.message)
                        pd.dismiss()
                    }
                }
            }
        }
    }


    private fun getuserdata(userid: String?) {
        if (checkForInternet(this)) {
            viewModel.getuserprofile(userid!!)
        } else {
            toast("No Internet Connection")
        }
        viewModel.userprofiledata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        requestStatus = it.value.friendRequest
                        setuserdata(it.value.userProfile)
                        pd.dismiss()

                        var mylat = ""
                        var mylng = ""
                        mylat = it.value.lat
                        mylng = it.value.long

                        mylatitude = mylat.toDouble()
                        mylongitude = mylng.toDouble()

//                        if (!sharedPreferences.getlocationvisibility()){
//                            mylatitude=0.0
//                            mylongitude=0.0
//                        }

                        var othlat = ""
                        var othlng = ""
                        othlat = it.value.userProfile[0].lat
                        othlng = it.value.userProfile[0].long
                        otherlatitude = othlat.toDouble()
                        otherlongitude = othlng.toDouble()

                        if (!it.value.userProfile[0].locationVisibility) {
                            otherlatitude = 0.0
                            otherlongitude = 0.0
                            locationVisibile = false
                        }

                        otheruserimage = it.value.userProfile[0].image
                        otheruserlocation = it.value.userProfile[0].location

                        pet_list.addAll(it.value.findPet)
                        adapter =
                            AddedPetAdapter(this, pet_list, pet_list.size, this)
                        binding.recyclerview.adapter = adapter

                        if (pet_list.size == 0) {
                            binding.recyclerview.visibility = View.GONE
                            binding.text.visibility = View.GONE
//                            binding.tvNopet.visibility = View.VISIBLE
                        }

                        setprofileborder(it.value.userProfile[0].perference.toString())
                        setmapview()
                        if (it.value.friendRequest.equals("Accepted")) {
                            binding.tvAddress.visibility = View.VISIBLE
                            binding.tvNo.visibility = View.VISIBLE
                            binding.imageView16.visibility = View.VISIBLE
                        }
                    } else {
                        toast("" + it.value.message)
                        pd.dismiss()
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

    private fun setprofileborder(data: String) {
        if (data.contains("petowner") && data.contains("petlover") && data.contains("volunteer")) {
            binding.circleProgressview.setBackgroundResource(R.drawable.three_color)
        } else if (data.contains("petowner") && data.contains("petlover")) {
            binding.circleProgressview.setBackgroundResource(R.drawable.two_color)
        } else if (data.contains("petlover") && data.contains("volunteer")) {
            binding.circleProgressview.setBackgroundResource(R.drawable.orangle_purple)
        } else if (data.contains("petowner") && data.contains("volunteer")) {
            binding.circleProgressview.setBackgroundResource(R.drawable.green_purple)
        } else if (data.contains("petowner") && !data.contains("petlover") && !data.contains("volunteer")) {
            binding.circleProgressview.setBackgroundResource(R.drawable.one_color)
        } else if (!data.contains("petowner") && data.contains("petlover") && !data.contains(
                "volunteer")) {
            binding.circleProgressview.setBackgroundResource(R.drawable.orange_color)
        } else if (!data.contains("petowner") && !data.contains("petlover") && data.contains(
                "volunteer"))
            binding.circleProgressview.setBackgroundResource(R.drawable.purple_color)
        else {
            binding.circleProgressview.setBackgroundResource(R.drawable.one_color)
        }
    }

    private fun intialize() {
        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerview.layoutManager = mLayoutManager
        binding.recyclerview.itemAnimator = DefaultItemAnimator()
    }

    private fun setmapview() {
        var user1: LatLng? = null
        var user2: LatLng? = null

        map?.setOnMapLoadedCallback {
            val markerInfoWindowAdapter = InfoWndowAdapter(this)
            map?.setInfoWindowAdapter(markerInfoWindowAdapter)
            map?.clear()
            val builder = LatLngBounds.Builder()
            if (mylatitude!! > 0.0 && mylongitude!! > 0.0) {
                user1 = LatLng(mylatitude!!, mylongitude!!)
                map?.animateCamera(CameraUpdateFactory.newLatLng(user1!!))
                marker = map?.addMarker(
                    MarkerOptions().position(user1!!).title(mylocation)
                        .snippet(ApiService.IMAGE_BASE_URL + myimage)
                )
              //  marker?.showInfoWindow()
                builder.include(marker!!.getPosition())
            }
            if (otherlatitude!! > 0.0 && otherlongitude!! > 0.0) {
                user2 = LatLng(otherlatitude!!, otherlongitude!!)
                map?.animateCamera(CameraUpdateFactory.newLatLng(user2!!))
                marker = map?.addMarker(
                    MarkerOptions().position(user2!!).title(otheruserlocation)
                        .snippet(ApiService.IMAGE_BASE_URL + otheruserimage)
                )
               // marker?.showInfoWindow()
                builder.include(marker!!.getPosition())
            }
            map?.setOnInfoWindowClickListener(this)
            if (mylongitude!! > 0.0 && otherlongitude!! > 0.0) {
                map?.addPolyline(
                    PolylineOptions().clickable(true)
                        .add(user1, user2).color(Color.RED).width(5f)
                )
                val bounds = builder.build()
                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
                map?.animateCamera(cu)
            } else {
                if (!locationVisibile) {
                    val position = CameraPosition.builder()
                        .target(LatLng(mylatitude!!, mylongitude!!)).zoom(16f).build()
                    map!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), null)
                } else {
                    moveToMyLocation()
                }
            }
        }
    }

    private fun setuserdata(userProfile: List<UserProfile>) {
        if (userProfile.size > 0) {
            binding.tvName.setText(userProfile[0].name)
            binding.tvAddress.setText(userProfile[0].location)
            binding.tvNo.setText(userProfile[0].phoneNo)
            Glide.with(this)
                .load(ApiService.IMAGE_BASE_URL + userProfile[0].image)
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).placeholder(R.drawable.placeholder).into(binding.imgProfile)
        }

//        if (requestStatus.isNotEmpty()){
//            toast(requestStatus)
//        }

        if (requestStatus.isEmpty()) {
            binding.btConnect.setText("Add Friend")
        } else if (requestStatus.equals("Pending")) {
            binding.btConnect.setText("Pending")
        } else if (requestStatus.equals("Accepted")) {
            binding.btConnect.setText("Friend")
            binding.btConnect.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_friends,
                0,
                0,
                0)
            binding.btConnect.setBackgroundDrawable(resources.getDrawable(R.drawable.freind_background))
        } else if (requestStatus.equals("Rejected")) {
            binding.btConnect.setText("Add Friend")
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

    override fun onConnected(p0: Bundle?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
//            toast("Please allow ACCESS_COARSE_LOCATION persmission.")
            getLastLocation()
            return
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient!!)

        map?.isMyLocationEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.setOnMyLocationButtonClickListener {
            moveToMyLocation()
            false
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("Connection",""+p0.errorMessage)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMapLongClick(p0: LatLng) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        super.onStart()
        googleApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    fun moveToMyLocation() {
        if (currentLocation != null) {
            val position = CameraPosition.builder()
                .target(LatLng(currentLocation!!.latitude,
                        currentLocation!!.longitude)).zoom(16f).build()
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), null)
        } else {
            Toast.makeText(this, "Can not get user location!", Toast.LENGTH_LONG).show()
        }
    }


    override fun onInfoWindowClick(p0: Marker) {
        toast("Clicked")
    }

    override fun onseepet(pos: Int, data: MessageXX) {
        val dailog = MyDialog()
        dailog.see_pet_detail(this, data, "OtherUserPets", updateInterface)
    }

    val updateInterface = object : PetsUpdateInterface {
        override fun oneditpet() {

        }
        override fun ondeletepet() {

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
//                        val position = CameraPosition.builder()
//                            .target(
//                                LatLng(
//                                    location.latitude,
//                                    location.longitude
//                                )
//                            )
//                            .zoom(16f)
//                            .build()
//                        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), null)
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
            val position = CameraPosition.builder()
                .target(
                    LatLng(
                        mLastLocation!!.latitude,
                        mLastLocation.longitude
                    )
                )
                .zoom(16f)
                .build()
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), null)
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


}