package com.application.animalAlertApp.View.BusinessProfile.EditBussProfile

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Shops.ShopViewModel
import com.application.animalAlertApp.databinding.ActivityEditPortfolioBinding
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
class EditPortfolio : AppCompatActivity() {
    private val TAG: String = EditPortfolio::class.java.getSimpleName()
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
    private lateinit var binding: ActivityEditPortfolioBinding
    private var logofile: File? = null
    private var coverfile: File? = null
    private lateinit var portfoliofiles: ArrayList<File>
    private var imagestatus: String = "Logo"
    private lateinit var imagesport: ArrayList<ImageView>
    private val viewmodel: ShopViewModel by viewModels()
    private lateinit var portfolioimages: ArrayList<MultipartBody.Part>
    private lateinit var sharedPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=MySharedPreferences(this@EditPortfolio)
        portfoliofiles = ArrayList()
        imagesport = ArrayList()
        portfolioimages = ArrayList()

        imagesport.add(binding.img1)
        imagesport.add(binding.img2)
        imagesport.add(binding.img3)
        imagesport.add(binding.img4)



        binding.uploadLogo.setOnClickListener {
            imagestatus = "Logo"
            setImage()
        }

        binding.coverImage.setOnClickListener {
            imagestatus = "Cover"
            setImage()
        }

        binding.portfolio.setOnClickListener {
            imagestatus = "Portfolio"
            setImage()
        }

        binding.btComplete.setOnClickListener {
            if (checkForInternet(this)) {
                savedata()
            } else {
                toast("No Internet connection")
            }

        }
        getbusinessdata()

        if (checkForInternet(this)) {
            viewmodel.getbusinessdata(sharedPreferences.getUserid()!!)
        } else {
            toast("No Internet Connection")
        }
    }


    private fun validation(): Boolean {
        if (logofile == null) {
            toast("Please select Logo image")
            return false
        } else if (coverfile == null) {
            toast("Please select Cover image")
            return false
        } else if (portfoliofiles.size == 0) {
            toast("Please select Portfolio image")
            return false
        }
        return true
    }

    private fun getbusinessdata() {
        viewmodel.businessdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        if (it.value.getShop.size > 0) {
                            Glide.with(this)
                                .load(ApiService.SHOP_IMAGE_BASE_URL + it.value.getShop[0].uploadLogo)
                                .into(binding.imgLogo)
                            Glide.with(this)
                                .load(ApiService.SHOP_IMAGE_BASE_URL + it.value.getShop[0].coverPhoto)
                                .into(binding.imgCover)
                            if (it.value.getShop[0].portfolioLogo.size > 0) {
                                for (i in 0..it.value.getShop[0].portfolioLogo.size - 1) {
                                    Glide.with(this)
                                        .load(ApiService.SHOP_IMAGE_BASE_URL + it.value.getShop[0].portfolioLogo[i])
                                        .into(imagesport[i])
                                }
                            }
                        }
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })
    }


    private fun savedata() {
        var uploadLogo: MultipartBody.Part? = null
        var coverPhoto: MultipartBody.Part? = null

//        val shopId: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), shop_id)
        if (logofile != null) {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), logofile!!)
            uploadLogo =
                MultipartBody.Part.createFormData("uploadLogo", logofile?.name, requestFile)
        } else {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            uploadLogo = MultipartBody.Part.createFormData("uploadLogo", "", requestFile)
        }
        if (coverfile != null) {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), coverfile!!)
            coverPhoto =
                MultipartBody.Part.createFormData("coverPhoto", coverfile?.name, requestFile)
        } else {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            coverPhoto = MultipartBody.Part.createFormData("coverPhoto", "", requestFile)
        }
        portfolioimages.clear()
        for (i in 0..portfoliofiles.size - 1) {
            portfolioimages.add(prepareFilePart("portfolioLogo", portfoliofiles[i]))
        }
        val pd: Dialog = MyProgressBar.progress(this)
        viewmodel.addportfolio(uploadLogo, coverPhoto, portfolioimages)
        viewmodel.shoppicdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {
                        toast(it.value.message)
                        val intent = Intent()
                        intent.setAction("ChangePortfolioStatus")
                        sendBroadcast(intent)
                        finish()
                    }
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    toast("" + it.exception?.message)
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Empty -> {
                }
            }
        })
    }

    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestBody: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
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

    private fun cropImage(sourceUri: Uri) {
        val destinationUri =
            Uri.fromFile(
                File(
                    getCacheDir(),
                    queryName(getContentResolver()!!, sourceUri)!!
                )
            )
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.green))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.green))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.green))
        if (lockAspectRatio) options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y)
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)

        if (imagestatus.equals("Logo")) {
            UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(1f, 1f)
                .start(this)
        } else if (imagestatus.equals("Cover")) {
            UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(16f, 9f)
                .start(this)
        } else if (imagestatus.equals("Portfolio")) {
            UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(3f, 2f)
                .start(this)
        }

    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        setResultOk(resultUri)
    }

    private fun setResultOk(imagePath: Uri?) {
        if (imagestatus.equals("Logo")) {
            logofile = File(imagePath?.path!!)
            Glide.with(this).load(logofile).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgLogo)
        } else if (imagestatus.equals("Cover")) {
            coverfile = File(imagePath?.path!!)
            Glide.with(this).load(coverfile).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgCover)
        } else if (imagestatus.equals("Portfolio")) {
            if (portfoliofiles.size < 4) {
                portfoliofiles.add(File(imagePath?.path!!))
                for (i in 0 until portfoliofiles.size) {
                    Glide.with(this).load(portfoliofiles[i]).diskCacheStrategy(
                        DiskCacheStrategy.ALL
                    ).into(imagesport[i])
                }
            }
        }
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


}