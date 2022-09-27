package com.application.animalAlertApp.Locations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.*
import com.application.animalAlertApp.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.skyfishjy.library.RippleBackground
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    //location
    private var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLAstKnownLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private val DEFAULT_ZOOM = 17f

    //places
    private lateinit var placesClient: PlacesClient
    private var predictionList: List<AutocompletePrediction>? = null

    //views

    private var mapView: View? = null
    private val rippleBg: RippleBackground? = null


    //variables
    private var addressOutput: String? = null
    private var addressResultCode = 0
    private var isSupportedArea = false
    private var currentMarkerPosition: LatLng? = null

    //receiving
    private var mApiKey = "AIzaSyCNKN05SZtNP88AkIjW1N8VTuDwZVAkThI"
    private var mSupportedArea = arrayOf<String>()
    private var mCountry = "India"
    private var mLanguage = "en"

    private val TAG: String = MapsActivity::class.java.getSimpleName()
    private lateinit var binding: ActivityMapsBinding
    private var screenType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  setContentView(R.layout.activity_maps)

        screenType = intent.getStringExtra("EditLocation").toString()

        Handler().postDelayed({ revealView(binding.icPin) }, 1000)
        receiveIntent()
        initMapsAndPlaces()
        binding.submitLocationButton.setOnClickListener {
            submitResultLocation()
        }


    }

    private fun receiveIntent() {
        val intent = intent
        if (intent.hasExtra(API_KEY)) {
            mApiKey = intent.getStringExtra(API_KEY)!!
        }
        if (intent.hasExtra(COUNTRY)) {
            mCountry = intent.getStringExtra(COUNTRY)!!
        }
        if (intent.hasExtra(LANGUAGE)) {
            mLanguage = intent.getStringExtra(LANGUAGE)!!
        }
        if (intent.hasExtra(SUPPORTED_AREAS)) {
            mSupportedArea = intent.getStringArrayExtra(SUPPORTED_AREAS)!!
        }
    }

    private fun submitResultLocation() {
        // if the process of getting address failed or this is not supported area , don't submit
        if (addressResultCode == FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText(this, "Location Search Failed", Toast.LENGTH_SHORT)
                .show()
        } else {
            //   toast(""+addressOutput)
            if (screenType.equals("edit")) {
                val data = Intent()
                data.putExtra("Address", addressOutput)
                data.putExtra("Lat", "" + currentMarkerPosition!!.latitude)
                data.putExtra("Long", "" + currentMarkerPosition!!.longitude)
                setResult(2, data)
                finish()
            } else {
                val data = Intent()
                data.putExtra("Address", addressOutput)
                data.putExtra("Lat", "" + currentMarkerPosition!!.latitude)
                data.putExtra("Long", "" + currentMarkerPosition!!.longitude)
                setResult(3, data)
                finish()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap!!.isMyLocationEnabled = true
        //enable location button
        //enable location button
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.uiSettings.isCompassEnabled = false

        //move location button to the required position and adjust params such margin

        //move location button to the required position and adjust params such margin
        if (mapView != null && mapView!!.findViewById<View?>("1".toInt()) != null) {
            val locationButton =
                (mapView!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 60, 500)
        }

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        //if task is successful means the gps is enabled so go and get device location amd move the camera to that location

        //if task is successful means the gps is enabled so go and get device location amd move the camera to that location
        task.addOnSuccessListener { getDeviceLocation() }

        //if task failed means gps is disabled so ask user to enable gps

        //if task failed means gps is disabled so ask user to enable gps
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, 51)
                } catch (e1: SendIntentException) {
                    e1.printStackTrace()
                }
            }
        }
        mMap!!.setOnMyLocationButtonClickListener {
            if (binding.searchBar.isSuggestionsVisible) {
                binding.searchBar.clearSuggestions()
            }
            if (binding.searchBar.isSearchEnabled) {
                binding.searchBar.disableSearch()
            }
            false
        }

        mMap!!.setOnCameraIdleListener {
            binding.smallPin.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            Log.i("Maps", "changing address")
            //                ToDo : you can use retrofit for this network call instead of using services
            //hint: services is just for doing background tasks when the app is closed no need to use services to update ui
            //best way to do network calls and then update user ui is Retrofit .. consider it
            startIntentService()
        }
    }

    protected fun startIntentService() {
        currentMarkerPosition = mMap!!.cameraPosition.target
        val resultReceiver: MapsActivity.AddressResultReceiver =
            AddressResultReceiver(
                Handler()
            )
        val intent = Intent(this, FetchAddressIntentService::class.java)
        intent.putExtra(RECEIVER, resultReceiver)
        intent.putExtra(LOCATION_LAT_EXTRA, currentMarkerPosition!!.latitude)
        intent.putExtra(LOCATION_LNG_EXTRA, currentMarkerPosition!!.longitude)
        intent.putExtra(LANGUAGE, mLanguage)
        startService(intent)
    }

    inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            addressResultCode = resultCode
            if (resultData == null) {
                return
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(RESULT_DATA_KEY)
            if (addressOutput == null) {
                addressOutput = ""
            }
            updateUi()
        }
    }

    private fun updateUi() {
        binding.tvDisplayMarkerLocation.setVisibility(View.VISIBLE)
        binding.progressBar.visibility = View.GONE
        mMap!!.clear()
        if (addressResultCode == SUCCESS_RESULT) {
            //check for supported area
            if (isSupportedArea(mSupportedArea)) {
                //supported
                addressOutput = addressOutput!!.replace("Unnamed Road,", "")
                addressOutput = addressOutput!!.replace("Unnamed Road،", "")
                addressOutput = addressOutput!!.replace("Unnamed Road New,", "")
                binding.smallPin.visibility = View.VISIBLE
                isSupportedArea = true
                binding.tvDisplayMarkerLocation.setText(addressOutput)
            } else {
                //not supported
                binding.smallPin.visibility = View.GONE
                isSupportedArea = false
                binding.tvDisplayMarkerLocation.setText("Not Supported")
            }
        } else if (addressResultCode == FAILURE_RESULT) {
            binding.smallPin.visibility = View.GONE
            binding.tvDisplayMarkerLocation.setText(addressOutput)
        }
    }

    private fun isSupportedArea(supportedAreas: Array<String>): Boolean {
        if (supportedAreas.size == 0) return true
        var isSupported = false
        for (area in supportedAreas) {
            if (addressOutput!!.contains(area)) {
                isSupported = true
                break
            }
        }
        return isSupported
    }

    private fun revealView(view: View) {
        val cx = view.width / 2
        val cy = view.height / 2
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        view.visibility = View.VISIBLE
        anim.start()
    }


    private fun initMapsAndPlaces() {
        binding.searchBar.visibility = View.GONE
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(this, mApiKey)
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        val mapFragment = fragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.view
        binding.searchBar.setOnSearchActionListener(object : OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    binding.searchBar.disableSearch()
                    binding.searchBar.clearSuggestions()
                }
            }
        })
        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry(mCountry)
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()
                placesClient.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse = task.result
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.autocompletePredictions
                                val suggestionsList: MutableList<String?> =
                                    ArrayList()
                                for (i in predictionList!!.indices) {
                                    val prediction = predictionList!!.get(i)
                                    suggestionsList.add(prediction.getFullText(null).toString())
                                }
                                binding.searchBar.updateLastSuggestions(suggestionsList)
                                Handler().postDelayed({
                                    if (!binding.searchBar.isSuggestionsVisible) {
                                        binding.searchBar.showSuggestionsList()
                                    }
                                }, 1000)
                            }
                        } else {
                            toast("" + task.exception)
                            Log.e("exception", "" + task.exception)
                            Log.i(
                                "Maps",
                                "prediction fetching task unSuccessful"
                            )
                        }
                    }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.searchBar.setSuggestionsClickListener(object :
            SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(position: Int, v: View) {
                if (position >= predictionList!!.size) {
                    return
                }
                val selectedPrediction = predictionList!![position]
                val suggestion = binding.searchBar.lastSuggestions[position].toString()
                binding.searchBar.text = suggestion
                Handler().postDelayed({ binding.searchBar.clearSuggestions() }, 1000)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    binding.searchBar.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                val placeId = selectedPrediction.placeId
                val placeFields =
                    Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener { fetchPlaceResponse ->
                        val place = fetchPlaceResponse.place
                        Log.i(
                            "Maps",
                            "place found " + place.name + place.address
                        )
                        val latLng = place.latLng
                        if (latLng != null) {
                            mMap!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    DEFAULT_ZOOM
                                )
                            )
                        }
                        rippleBg!!.startRippleAnimation()
                        Handler().postDelayed({ rippleBg.stopRippleAnimation() }, 2000)
                    }
                    .addOnFailureListener { e ->
                        if (e is ApiException) {
                            val apiException = e
                            apiException.printStackTrace()
                            val statusCode = apiException.statusCode
                            Log.i(
                                "Maps", "place not found" + e.message
                            )
                            Log.i(
                                "Maps",
                                "status code : $statusCode"
                            )
                        }
                    }
            }

            override fun OnItemDeleteListener(position: Int, v: View) {}
        })
    }

    /**
     * is triggered whenever we want to fetch device location
     * in order to get device's location we use FusedLocationProviderClient object that gives us the last location
     * if the task of getting last location is successful and not equal to null ,
     * apply this location to mLastLocation instance and move the camera to this location
     * if the task is not successful create new LocationRequest and LocationCallback instances and update lastKnownLocation with location result
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLAstKnownLocation = task.result
                    if (mLAstKnownLocation != null) {
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    mLAstKnownLocation!!.getLatitude(),
                                    mLAstKnownLocation!!.getLongitude()
                                ), DEFAULT_ZOOM
                            )
                        )
                    } else {
                        val locationRequest = LocationRequest.create()
                        locationRequest.interval = 1000
                        locationRequest.fastestInterval = 5000
                        locationRequest.priority =
                            LocationRequest.PRIORITY_HIGH_ACCURACY
                        locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                if (locationResult.equals(null)) {
                                    return
                                }
                                mLAstKnownLocation = locationResult.lastLocation
                                mMap!!.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            mLAstKnownLocation!!.getLatitude(),
                                            mLAstKnownLocation!!.getLongitude()
                                        ), DEFAULT_ZOOM
                                    )
                                )
                                //remove location updates in order not to continues check location unnecessarily
                                mFusedLocationProviderClient!!.removeLocationUpdates(
                                    locationCallback!!
                                )
                            }
                        }
                        mFusedLocationProviderClient!!.requestLocationUpdates(
                            locationRequest,
                            null!!
                        )
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Unable to get last location ",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }
}