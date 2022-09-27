package com.application.animalAlertApp.View.Profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Locations.MapsActivity
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.util.*
import okhttp3.MultipartBody

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_GALLERY_IMAGE = 1
    private val lockAspectRatio = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private val ASPECT_RATIO_X = 16.0f
    private var ASPECT_RATIO_Y = 9.0f
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80
    private var fileName: String? = null
    private var file: File? = null
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var PERMISSION_ID = 44
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var userPreferences: UserPreferences
    private val viewmodel: EditProfileViewModel by viewModels()
    private lateinit var pd: Dialog
    private var longi=""
    private var lati=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mySharedPreferences = MySharedPreferences(this)
        userPreferences = UserPreferences(this)
        pd = MyProgressBar.progress(this)

        setdata()

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgChange.setOnClickListener {
            setImage()
        }
        binding.btSave.setOnClickListener {
            if (checkForInternet(this)) {
                saveuserdata()
            } else {
                toast("No Internet connection")
            }
        }

        binding.tvLocation.setOnClickListener {
            if (checkPermissions())
            getLastLocation()

        }


        binding.etLoc.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("EditLocation","edit")
            startActivityForResult(intent,2)
        }
        getprofilepic()
    }

    private fun getprofilepic() {
        viewmodel.editpicdata.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        GlobalScope.launch(Dispatchers.Main) {
                            updateimage(it.value.image)
                        }
                    } else {
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

    suspend fun updateimage(image: String) {
        userPreferences.saveimage(image)
    }

    private fun saveuserdata() {
        lifecycleScope.launchWhenStarted {
            viewmodel.editprofile(
                binding.etName.text.toString().trim(),
                binding.etMobile.text.toString().trim(),
                binding.etLoc.text.toString().trim(),
                binding.etPetemail.text.toString().trim(), lati,longi
            ).catch { e ->
                toast("exception==" + e)
                toast(""+e.message)
                Log.e("exception", "" + e.message)
                pd.dismiss()
            }.onStart {
                pd.show()
            }.collect { data ->
                if (data.status == 200) {
                    pd.dismiss()
                    //   toast("" + data.message)
                    userPreferences.saveemail(data.email)
                    userPreferences.savephone(data.phoneNo)
                    userPreferences.savename(data.name)
                    userPreferences.savelocation(data.location)
                    toast("Edit profile Sucessfully")
                } else {
                    toast("" + data.message)
                    pd.dismiss()
                }
            }
        }
    }


    private fun savedata() {
        var image: MultipartBody.Part? = null
        if (file != null) {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), file!!)
            image = MultipartBody.Part.createFormData("image", file?.name, requestFile)
        } else {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            image = MultipartBody.Part.createFormData("image", "", requestFile)
        }
        viewmodel.editprofilepic1(image)
    }

    private fun setdata() {
        userPreferences.getname.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.etName.setText(it)
        })
        userPreferences.getemail.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.etPetemail.setText(it)
        })
        userPreferences.getphone.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.etMobile.setText(it)
        })

        userPreferences.getlocation.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.etLoc.setText(it)
        })
        userPreferences.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            Glide.with(this).load(ApiService.IMAGE_BASE_URL + it)
                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
                .into(binding.imgProfile)
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
                        binding.etLoc.setText(
                            getAddressFromLatLng(
                                this@EditProfileActivity,
                                location.latitude,
                                location.longitude))
                        lati=location.latitude.toString()
                        longi=location.longitude.toString()
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
            binding.etLoc.setText(
                getAddressFromLatLng(
                    this@EditProfileActivity,
                    mLastLocation!!.latitude,
                    mLastLocation.longitude
                )
            )
            lati=mLastLocation.latitude.toString()
            longi=mLastLocation.longitude.toString()
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

//    override fun onResume() {
//        super.onResume()
//        if (checkPermissions()) {
//            getLastLocation()
//        }
//    }


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

    private fun setImage() {
        val option = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Add Photo !")
        builder.setCancelable(false)
        builder.setItems(option) { dialog, i ->
            if (option[i] == "Take Photo") {
                takeCameraImage()
            } else if (option[i] == "Choose from Gallery") {
                chooseImageFromGallery()
            } else {
                dialog.cancel()
            }
        }
        builder.show()
    }


    private fun takeCameraImage() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fileName = System.currentTimeMillis().toString() + ".jpg"
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            getCacheImagePath(fileName!!))
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun chooseImageFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE ->
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(fileName!!)!!)
                } else {
                    setResultCancelled()
                }
            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                val imageUri: Uri = Uri.parse(data?.dataString)
                cropImage(imageUri)
            } else {
                setResultCancelled()
            }
            UCrop.REQUEST_CROP ->
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data)
                } else {
                    setResultCancelled()
                }
            UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(data!!)
                Log.e("BusinessProfile", "Crop error: $cropError")
                setResultCancelled()
            }
            2 -> {
                if (data!=null) {
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
            else -> setResultCancelled()
        }
    }

    private fun cropImage(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(getCacheDir(), queryName(getContentResolver()!!, sourceUri)!!)
        )
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        options.setToolbarColor(ContextCompat.getColor(this, R.color.green))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.green))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.green))
        if (lockAspectRatio) options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y)
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(1f, 1f)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        setResultOk(resultUri)
    }

    private fun setResultCancelled() {
        toast("Cancelled")
    }

    private fun setResultOk(resultUri: Uri?) {
        file = File(resultUri!!.path)

        if (checkForInternet(this)) {
            savedata()
        } else {
            toast("No Internet connection")
        }

    }

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(getExternalCacheDir(), "AnimalAlertCommunity")
        if (!path.exists()) {
            path.mkdirs()
        }
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            this,
            "com.application.animalAlertApp" + ".provider",
            image
        )
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_IMAGE_CAPTURE) {
//                var myBitmap = BitmapFactory.decodeFile(file!!.absolutePath)
//                val imageRotation: Int = getImageRotation(file!!)
//                if (imageRotation != 0)
//                    myBitmap = getBitmapRotatedByDegree(
//                        myBitmap, imageRotation
//                    )
//                binding.imgProfile.setImageBitmap(myBitmap)
//                savedata()
//
//            } else if (requestCode == REQUEST_PICK_IMAGE) {
//                val uri = data?.getData()
//                try {
//                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                    val tempUri = getImageUri(applicationContext, bitmap)
//                    file = File(getRealPathFromURI(tempUri))
//                    // setimages1(bitmap, finalFile)
//                    //    Glide.with(this).load(file!!).into(binding.imgProfile)
//                    binding.imgProfile.setImageBitmap(bitmap)
//                    savedata()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }


}
