package com.application.animalAlertApp.View.Post

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Shops.AddPortfolioFragment
import com.application.animalAlertApp.databinding.ActivityAddPostBinding
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


@AndroidEntryPoint
class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private val TAG: String = AddPortfolioFragment::class.java.getSimpleName()
    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_GALLERY_IMAGE = 1
    private val lockAspectRatio = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private val ASPECT_RATIO_X = 16.0f
    private var ASPECT_RATIO_Y = 9.0f
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80
    private var mycurrentlocation: String=""
    private lateinit var imageviewlist: ArrayList<ImageView>
    private lateinit var imgcrosslist: ArrayList<ImageView>
    private lateinit var imagefilelist: ArrayList<File>
    private lateinit var bitmaplist: ArrayList<Bitmap>
    private lateinit var postImg: ArrayList<MultipartBody.Part>
    private val viewModel: PostViewModel by viewModels()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var PERMISSION_ID = 44
    private lateinit var userprefrence: UserPreferences
    private var fileName: String? = null
    private lateinit var pd: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_post)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userprefrence = UserPreferences(this)
        pd = MyProgressBar.progress(this)
        setdata()



        imageviewlist = ArrayList()
        bitmaplist = ArrayList()
        imagefilelist = ArrayList()
        imgcrosslist = ArrayList()
        postImg = ArrayList()


        imageviewlist.add(binding.img1)
        imageviewlist.add(binding.img2)
        imageviewlist.add(binding.img3)
        imageviewlist.add(binding.img4)

        imgcrosslist.add(binding.imgCross1)
        imgcrosslist.add(binding.imgCross2)
        imgcrosslist.add(binding.imgCross3)
        imgcrosslist.add(binding.imgCross4)

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgCamera.setOnClickListener {
            if (imagefilelist.size < 4) {
                setImage()
            }else {
                toast("Only add 4 images")
            }
        }
        binding.tvPost.setOnClickListener {
            if (imagefilelist.size > 0) {
                if (checkForInternet(this)) {
                    addPost()
                } else {
                    toast("No Internet Connection")
                }
            } else {
                toast("Please Add Image")
            }

        }
        deleteimages()
    }

    private fun addPost() {
        val description: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etDescription.text.toString().trim()
        )
        val location: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            mycurrentlocation
        )
        postImg.clear()
        for (i in 0..imagefilelist.size - 1) {
            postImg.add(prepareFilePart("postImg", imagefilelist[i]))
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addpost(description, location, postImg).catch { e ->
                toast("" + e.message)
                pd.dismiss()
            }.onStart {
                pd.show()
            }.collect { data ->
                pd.dismiss()
                if (data.status == 200) {
                    finish()
                } else {
                    toast("" + data.message)
                }
            }
        }
    }


    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestBody: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    private fun setdata() {
        userprefrence.getname.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.tvName.setText(it)
        })

        userprefrence.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            Glide.with(this).load(ApiService.IMAGE_BASE_URL + it)
                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
                .into(binding.imgProfile)
        })


    }

    private fun setImage() {
        val option = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@AddPostActivity)
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
                            getCacheImagePath(fileName!!)
                        )
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
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
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


    private fun cropImage(sourceUri: Uri) {
        val destinationUri =
            Uri.fromFile(File(getCacheDir(), queryName(getContentResolver()!!, sourceUri)!!))
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.green))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.green))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.green))
        if (lockAspectRatio) options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y)
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(3f, 2f)
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

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(getExternalCacheDir(), "AnimalAlertCommunity")
        if (!path.exists()) path.mkdirs()
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


    private fun setResultOk(imagePath: Uri?) {
        binding.constraintImages.visibility = View.VISIBLE
        val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath)
        setimages1(bitmap, File(imagePath?.path!!))
    }


    /**
     * Calling this will delete the images from cache directory
     *  to usefulclear some memory
     */
    fun clearCache(context: Context) {
        val path = File(context.getExternalCacheDir(), "AnimalAlertCommunity")
        if (path.exists() && path.isDirectory()) {
            for (child in path.listFiles()!!) {
                child.delete()
            }
        }
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
                Log.e(TAG, "Crop error: $cropError")
                setResultCancelled()
            }
            else -> setResultCancelled()
        }
    }

    private fun setimages1(bitmap: Bitmap, file: File) {
        if (imagefilelist.size < 4) {
            imagefilelist.add(file)
            bitmaplist.add(bitmap)
            refreshimages()
        }
    }

    private fun refreshimages() {
        for (i in 0..imagefilelist.size - 1) {
            Glide.with(this).load(imagefilelist[i]).into(imageviewlist[i])
            imgcrosslist[i].visibility = View.VISIBLE
        }
    }

    private fun deleteimages() {
        binding.imgCross1.setOnClickListener {
            binding.img1.setImageBitmap(null)
            binding.imgCross1.visibility = View.GONE
            bitmaplist.removeAt(0)
            imagefilelist.removeAt(0)
            refreshimages1()
        }
        binding.imgCross2.setOnClickListener {
            binding.img2.setImageBitmap(null)
            binding.imgCross2.visibility = View.GONE
            bitmaplist.removeAt(1)
            imagefilelist.removeAt(1)
            refreshimages1()
        }
        binding.imgCross3.setOnClickListener {
            binding.img3.setImageBitmap(null)
            binding.imgCross3.visibility = View.GONE
            bitmaplist.removeAt(2)
            imagefilelist.removeAt(2)
            refreshimages1()
        }
        binding.imgCross4.setOnClickListener {
            binding.img4.setImageBitmap(null)
            binding.imgCross4.visibility = View.GONE
            bitmaplist.removeAt(3)
            imagefilelist.removeAt(3)
            refreshimages1()
        }
    }

    private fun refreshimages1() {
        if (imagefilelist.size == 1) {
            imgcrosslist[0].visibility = View.VISIBLE
            imageviewlist[0].setImageBitmap(bitmaplist.get(0))

            imgcrosslist[1].visibility = View.GONE
            imageviewlist[1].setImageBitmap(null)
        } else if (imagefilelist.size == 2) {
            imgcrosslist[0].visibility = View.VISIBLE
            imageviewlist[0].setImageBitmap(bitmaplist.get(0))

            imgcrosslist[1].visibility = View.VISIBLE
            imageviewlist[1].setImageBitmap(bitmaplist.get(1))

            imgcrosslist[2].visibility = View.GONE
            imageviewlist[2].setImageBitmap(null)

            imgcrosslist[3].visibility = View.GONE
            imageviewlist[3].setImageBitmap(null)
        } else if (imagefilelist.size == 3) {
            imgcrosslist[0].visibility = View.VISIBLE
            imageviewlist[0].setImageBitmap(bitmaplist.get(0))

            imgcrosslist[1].visibility = View.VISIBLE
            imageviewlist[1].setImageBitmap(bitmaplist.get(1))

            imgcrosslist[2].visibility = View.VISIBLE
            imageviewlist[2].setImageBitmap(bitmaplist.get(2))

            imgcrosslist[3].visibility = View.GONE
            imageviewlist[3].setImageBitmap(null)
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
                        mycurrentlocation = getAddressFromLatLng(
                            this@AddPostActivity,
                            location.latitude,
                            location.longitude
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
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            mycurrentlocation = getAddressFromLatLng(
                this@AddPostActivity,
                mLastLocation!!.latitude,
                mLastLocation.longitude
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


    fun getAddressFromLatLng(context: Context?, lat: Double, longi: Double): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(lat, longi, 1)
            addresses[0].locality + ", " + addresses[0].adminArea
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }
}