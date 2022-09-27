package com.application.animalAlertApp.View.BusinessProfile

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.BusinessProPagerAdapter
import com.application.animalAlertApp.Fragments.AboutFragment
import com.application.animalAlertApp.Fragments.PhotosFragment
import com.application.animalAlertApp.Fragments.ServiceFragment
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityBusinessProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@AndroidEntryPoint
class BusinessProfileActivity : AppCompatActivity() {
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
    private var coverfile: File? = null
    private var logofile: File? = null
    private var imagestatus: String = ""
    private val viewmodel: BusinessViewModel by viewModels()
    private lateinit var binding: ActivityBusinessProfileBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var sharedPreferences: MySharedPreferences
    private var coverphoto: String = ""
    private var logo: String = ""
    private var userid: String = ""
    private var phoneno: String = ""
    private var email: String = ""
    private lateinit var pd: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessProfileBinding.inflate(layoutInflater)
        //  setContentView(R.layout.activity_business_profile)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        sharedPreferences = MySharedPreferences(this)
        pd = MyProgressBar.progress(this)

        val intent = intent
        if (intent.hasExtra("userid")) {
            binding.imgEditcover.visibility = View.GONE
            userid = intent.getStringExtra("userid").toString()
            if (userid.equals(sharedPreferences.getUserid())) {
                binding.imageView19.visibility = View.GONE
                binding.imageView20.visibility = View.GONE
                binding.imgEditcover.visibility = View.VISIBLE
                binding.tvShopstatus.visibility = View.VISIBLE
            }
            if (checkForInternet(this)) {
                viewmodel.getbusinessdata(userid)
            } else {
                toast("No Internet Connection")
            }

        } else {
            if (checkForInternet(this)) {
                binding.imageView19.visibility = View.GONE
                binding.imageView20.visibility = View.GONE
                binding.tvShopstatus.visibility = View.VISIBLE
                viewmodel.getbusinessdata(sharedPreferences.getUserid()!!)
            } else {
                toast("No Internet Connection")
            }
        }

        setPager()
        getbusinessdata()
        changecoverprofile()
        changelogo()

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgEditcover.setOnClickListener {
            if (!coverphoto.equals("")) {
                imagestatus = "Cover"
                setImage()
                //  viewmodel.editcoverphoto()
            }
        }

        binding.imgCircle.setOnClickListener {
            if (userid.equals("")) {
                if (!logo.equals("")) {
                    imagestatus = "Logo"
                    setImage()
                }
            } else {
                if (userid.equals(sharedPreferences.getUserid())) {
                    if (!logo.equals("")) {
                        imagestatus = "Logo"
                        setImage()
                    }
                }
            }
        }

        ///call image click
        binding.imageView19.setOnClickListener {
            if (!userid.equals("")) {
                if (!userid.equals(sharedPreferences.getUserid())) {
                    if (!phoneno.equals("")) {
                        val i = Intent(Intent.ACTION_DIAL)
                        i.data = Uri.parse("tel:" + phoneno)
                        startActivity(i)
                    }
                }
            }
        }
        ///Email image click
        binding.imageView20.setOnClickListener {
            if (!userid.equals("")) {
                if (!userid.equals(sharedPreferences.getUserid())) {
                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Animal Alert Community")
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Animal Alert Community")
                    startActivity(Intent.createChooser(emailIntent, "Chooser Title"))
                }
            }
        }


        val filter = IntentFilter()
        filter.addAction("Com.Update.Services")
        registerReceiver(reciver, filter)


    }

    val reciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (checkForInternet(this@BusinessProfileActivity)) {
                viewmodel.getbusinessdata(sharedPreferences.getUserid()!!)
            } else {
                toast("No Internet Connection")
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(reciver)
    }

    private fun changelogo() {
        viewmodel.editlogo.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        toast("" + it.value.message)
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Loading -> {
                    pd.show()

                }
                is Resource.Failure -> {
                    pd.dismiss()
                    toast("" + it.exception!!.message)
                }
            }
        })
    }

    private fun setImage() {
        val option = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
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


    private fun getbusinessdata() {
        viewmodel.businessdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        if (it.value.getShop != null) {
                            if (it.value.getShop.size > 0) {
                                if (it.value.getShop[0].isActive) {
                                    binding.tvShopstatus.setText("Active")
                                } else {
                                    binding.tvShopstatus.setTextColor(resources.getColor(R.color.red))
                                    binding.tvShopstatus.setText("Inactive")
                                }
                                binding.tvUsername.setText(it.value.getShop[0].userName)
                                coverphoto = it.value.getShop[0].coverPhoto
                                logo = it.value.getShop[0].uploadLogo
                                phoneno = it.value.getShop[0].mobileNo
                                email = it.value.getShop[0].userEmail
                                Glide.with(this)
                                    .load(ApiService.SHOP_IMAGE_BASE_URL + it.value.getShop[0].coverPhoto)
                                    .fitCenter().centerCrop()
                                    .diskCacheStrategy(
                                        DiskCacheStrategy.ALL
                                    ).placeholder(R.drawable.image_placeholder)
                                    .into(binding.imgBanner)
                                Glide.with(this)
                                    .load(ApiService.SHOP_IMAGE_BASE_URL + it.value.getShop[0].uploadLogo)
                                    .diskCacheStrategy(
                                        DiskCacheStrategy.ALL
                                    ).placeholder(R.drawable.placeholder).into(binding.imgCircle)
                                binding.tvDescription.setText(it.value.getShop[0].businessDescription)
                                binding.tvLocation.setText(it.value.getShop[0].location)
                            }
                        }
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }

            }
        })
    }


    private fun changecoverprofile() {
        viewmodel.editcover.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        toast("" + it.value.message)
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    toast("" + it.exception!!.message)
                }
            }
        })
    }

    private fun setPager() {
        val adapter = BusinessProPagerAdapter(supportFragmentManager)
        adapter.addFragment(AboutFragment(), "About")
        adapter.addFragment(ServiceFragment(), "Services")
        adapter.addFragment(PhotosFragment(), "Photos")
        binding.viewpager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)
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
            else -> setResultCancelled()
        }
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
            .withAspectRatio(16f, 9f)
            .withOptions(options)
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

    private fun setResultOk(resultUri: Uri?) {
        if (imagestatus.equals("Cover")) {
            coverfile = File(resultUri?.path!!)
            Glide.with(this).load(coverfile).fitCenter().centerCrop().into(binding.imgBanner)
            editcoverphot()
        } else if (imagestatus.equals("Logo")) {
            logofile = File(resultUri?.path!!)
            Glide.with(this).load(logofile).fitCenter().centerCrop().into(binding.imgCircle)
            editlogo()
        }
    }

    private fun editcoverphot() {
        var coverPhoto: MultipartBody.Part? = null
        if (coverfile != null) {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), coverfile!!)
            coverPhoto =
                MultipartBody.Part.createFormData("coverPhoto", coverfile?.name, requestFile)
        } else {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            coverPhoto = MultipartBody.Part.createFormData("coverPhoto", "", requestFile)
        }
        viewmodel.editcoverphoto(coverPhoto)
    }

    private fun editlogo() {
        var uploadLogo: MultipartBody.Part? = null
        if (logofile != null) {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), logofile!!)
            uploadLogo =
                MultipartBody.Part.createFormData("uploadLogo", logofile?.name, requestFile)
        } else {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            uploadLogo = MultipartBody.Part.createFormData("uploadLogo", "", requestFile)
        }
        viewmodel.editshoplogo(uploadLogo)
    }

    private fun setResultCancelled() {
        Log.e("bussprofile", "cancelled")
        // toast("Cancelled")
    }

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(getExternalCacheDir(), "AnimalAlertCommunity")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            this,
            "com.application.animalAlertApp" + ".provider", image
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
}