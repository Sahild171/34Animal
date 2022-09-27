package com.application.animalAlertApp.View.Pet


import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.application.animalAlertApp.Interfaces.AddPetInteface
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Main.HomeActivity
import com.application.animalAlertApp.databinding.ActivityAddPetBinding
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import android.util.Log
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.jaredrummler.materialspinner.MaterialSpinner
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import java.io.*
import com.jaredrummler.materialspinner.MaterialSpinner.OnNothingSelectedListener
import java.io.File
import java.util.*
import android.provider.MediaStore.Images
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.selectpettype
import com.application.animalAlertApp.data.Response.MessageXX
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.ArrayList
import okhttp3.MultipartBody


@AndroidEntryPoint
class AddPetActivity : AppCompatActivity(), AddPetInteface {
    var addpet: String? = null
    private val viewModel: AddPetViewModel by viewModels()
    private lateinit var binding: ActivityAddPetBinding
    private lateinit var spinnerList: ArrayList<String>
    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_GALLERY_IMAGE = 1
    private val lockAspectRatio = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private val ASPECT_RATIO_X = 16.0f
    private var ASPECT_RATIO_Y = 9.0f
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80
    private var list: ArrayList<String>? = null
    private lateinit var progress: KProgressHUD
    private lateinit var listbtimap: ArrayList<Bitmap>
    private lateinit var listimageview: ArrayList<ImageView>
    private lateinit var listcross: ArrayList<ImageView>
    private lateinit var listfilename: ArrayList<File>
    private lateinit var petImages: ArrayList<MultipartBody.Part>

    //EDit case list
    private lateinit var peteditlist: ArrayList<String>
    private var petId: String = ""
    private var pettype: String? = null
    private var fileName: String? = null
    private var pet_list: ArrayList<MessageXX>? = null
    private lateinit var pd: Dialog
    private lateinit var popupMenu: PopupMenu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get type for Addpet
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pet_list = ArrayList()
        peteditlist = ArrayList()

        pd = MyProgressBar.progress(this)

        // Get Petlist for Edit
        addpet = intent.getStringExtra("Screentype")
        if (addpet.isNullOrEmpty()) {
            val intent = intent
            val args = intent.getBundleExtra("BUNDLE")
            pet_list = args!!.getSerializable("pet_list") as ArrayList<MessageXX>
        } else {
            if (addpet.equals("AddAlert")) {
                binding.tvSkip.visibility = View.GONE
            }
        }

//        if (addpet!!.isNotEmpty()){
//            if (addpet.equals("AddAlert")){
//                binding.tvSkip.visibility=View.GONE
//            }
//        }


        spinnerList = ArrayList()
        listbtimap = ArrayList()
        listimageview = ArrayList()
        listcross = ArrayList()
        listfilename = ArrayList()
        petImages = ArrayList()

        list = ArrayList()

        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")



        showdialogupload()
        skip()
        AddPetContinue()
        getPetType()

        listimageview.add(binding.img1)
        listimageview.add(binding.img2)
        listimageview.add(binding.img3)
        listimageview.add(binding.img4)

        listcross.add(binding.imgCross1)
        listcross.add(binding.imgCross2)
        listcross.add(binding.imgCross3)
        listcross.add(binding.imgCross4)


        deleteimages()

        // Edit pet Only
        ConvertoEdit()
        deletepetimageObserver()
        editpetimages()
        editdetailObserver()





        viewModel.petprofileimage.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {
                        toast("" + it.value.message)
                        val intent = Intent()
                        intent.setAction("RefreshPets")
                        sendBroadcast(intent)
                        finish()
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }

    private fun editdetailObserver() {
        viewModel.editpetdata.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        val intent = Intent()
                        intent.setAction("RefreshPets")
                        sendBroadcast(intent)
                        finish()
                    } else {
                        toast("" + it.value.message)
                        pd.dismiss()
                    }
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
            }
        })
    }

    private fun editpetimages() {
        viewModel.editpetimage.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        peteditlist.add(it.value.petImages[0])
                        refresheditimages()
                    } else {
                        toast("" + it.value.message)
                        pd.dismiss()
                    }
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
            }
        })


    }

    private fun ConvertoEdit() {
        if (pet_list != null) {
            if (pet_list!!.size > 0) {
                petId = pet_list!![0]._id
                pettype = pet_list!![0].petType
                binding.tvSkip.visibility = View.GONE
                binding.tvTitle.setText("Edit Pet")
                binding.etPetname.setText(pet_list!![0].petName)
                binding.etDescription.setText(pet_list!![0].description)
                binding.etColorpet.setText(pet_list!![0].petColor)
                binding.etBreed.setText(pet_list!![0].petBreed)
                if (pet_list!![0].petImages.size > 0) {
                    binding.constraintImages.visibility = View.VISIBLE
                    for (i in 0..pet_list!![0].petImages.size - 1) {
                        Glide.with(this)
                            .load(ApiService.PET_IMAGE_BASE_URL + pet_list!![0].petImages[i])
                            .into(listimageview[i])
                        peteditlist.add(pet_list!![0].petImages[i])
                        listcross[i].visibility = View.VISIBLE
                    }
                    refresheditimages()
                }
            }
        }
    }


    private fun getPetType() {
        viewModel.gettype()
        lifecycleScope.launchWhenStarted {
            viewModel.petData.collect {
                binding.apply {
                    when (it) {
                        is PetTypeState.Success -> {
                            for (i in 0..it.data.getData.size - 1) {
                                spinnerList.add(it.data.getData.get(i).petType)
                            }
                            setspinner(spinnerList)
                        }
                        is PetTypeState.Failure -> {
                            toast("" + it.msg)
                        }
                        is PetTypeState.Loading -> {
                            //    progress.show()
                        }
                        is PetTypeState.Empty -> {

                        }
                    }
                }
            }
        }
    }

    private fun setspinner(spinnerList: ArrayList<String>) {
        binding.etTypepet.setItems(spinnerList)
        binding.etTypepet.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            // toast("" + item)
            pettype = item
        })
        binding.etTypepet.setOnNothingSelectedListener(OnNothingSelectedListener { spinner ->

        })
        //Edit pet part only
        if (pet_list != null) {
            if (pet_list!!.size > 0) {
                binding.etTypepet.selectedIndex = selectpettype(spinnerList, pet_list!![0].petType)
            }
        }

    }

    private fun AddPetContinue() {
        binding.apply {
            btSave.setOnClickListener {
                if (pet_list!!.isNotEmpty()) {
                    if (pet_list!!.size > 0) {
                        if (checkForInternet(this@AddPetActivity)) {
                            viewModel.editpetdetail(
                                etPetname.text.toString().trim(),
                                pettype!!,
                                etColorpet.text.toString().trim(),
                                etBreed.text.toString().trim(),
                                etDescription.text.toString().trim(),
                                petId
                            )
                        } else {
                            toast("No Internet Connection")
                        }
                    }
                } else {
                    if (validation()) {
                        save()
                    }
                }
            }
        }
    }


    private fun validation(): Boolean {
        if (binding.etPetname.text.toString().isEmpty()) {
            toast("Please Enter Name")
            return false
        } else if (binding.etColorpet.text.toString().isEmpty()) {
            toast("Please Enter color")
            return false
        } else if (binding.etBreed.text.toString().isEmpty()) {
            toast("Please Enter Breed")
            return false
        } else if (binding.etDescription.text.toString().isEmpty()) {
            toast("Please Enter Discription")
            return false
        }
        return true
    }


    private fun save() {
        val petName: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etPetname.text.toString().trim()
        )
        val petType: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pettype!!)
        val petColor: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etColorpet.text.toString().trim()
        )
        val petBreed: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etBreed.text.toString().trim()
        )
        val description: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etDescription.text.toString().trim()
        )

        petImages.clear()
        for (i in 0..listfilename.size - 1) {
            petImages.add(prepareFilePart("petImages", listfilename.get(i)))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addpet(
                petName, petType, petColor, petBreed, description, petImages
            ).catch { e ->
                toast("exception==" + e)
                //    Log.e("exception", "" + e)
                progress.dismiss()
            }.onStart {
                progress.show()
            }.collect { data ->
                if (data.status == 200) {
                    progress.dismiss()
                    toast("" + data.message)
                    if (addpet.equals("AddAlert")) {
                        finish()
//                        val dialog = MyDialog()
//                        dialog.Dialog_Add_Pet(this@AddPetActivity, this@AddPetActivity)
                    } else if (addpet.equals("Prefrences")) {
                        val intent = Intent(this@AddPetActivity, HomeActivity::class.java)
                        intent.putExtra("Screentype", "Pet")
                        startActivity(intent)
                    } else {

                    }
                } else {
                    toast("" + data.message)
                    progress.dismiss()
                }
            }
        }
    }

    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestBody: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    private fun skip() {
        binding.tvSkip.setOnClickListener {
            val intent = Intent(this@AddPetActivity, HomeActivity::class.java)
            intent.putExtra("Screentype", "Pet")
            startActivity(intent)
        }
    }

    private fun showdialogupload() {
        binding.etUploadphotos.setOnClickListener {
            if (pet_list!!.isNotEmpty()) {
                if (pet_list!!.size > 0) {
                    if (peteditlist.size< 4){
                        setImage()
                    }else {
                        toast("No More Photos Upload")
                    }
                }
            } else {
                if (listfilename.size < 4) {
                    setImage()
                } else {
                    toast("No More Photos Upload")
                }
            }
        }
    }


    fun CloseAddPet(view: android.view.View) {
        finish()
    }


    //Interface functions
    override fun OnContinue() {
        finish()
    }

    override fun OnAddMore() {
        binding.etPetname.setText("")
        binding.etDescription.setText("")
        binding.etTypepet.setText("")
        listbtimap.clear()
        listfilename.clear()
        refreshimages1()
    }


    private fun setImage() {
        val option = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@AddPetActivity)
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
                            Images.Media.EXTERNAL_CONTENT_URI
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
        if (pet_list!!.isNotEmpty()) {
            if (pet_list!!.size > 0) {
                binding.constraintImages.visibility = View.VISIBLE
                // val bitmap = Images.Media.getBitmap(getContentResolver(), imagePath)
                val file = File(imagePath?.path!!)
                editnewimage(file)
            }
        } else {
            binding.constraintImages.visibility = View.VISIBLE
            val bitmap = Images.Media.getBitmap(getContentResolver(), imagePath)
            setimages1(bitmap, File(imagePath?.path!!))
        }

    }

    private fun editnewimage(img_file: File) {
        if (checkForInternet(this)) {
            val id: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                petId
            )
            var petImages: MultipartBody.Part? = null
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), img_file)
            petImages =
                MultipartBody.Part.createFormData("petImages", img_file.name, requestFile)
            viewModel.editpetsphotos(id, petImages)
        } else {
            toast("No Internet Connection")
        }
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
                Log.e("Addpet", "Crop error: $cropError")
                setResultCancelled()
            }
            else -> setResultCancelled()
        }
    }


    private fun setimages1(bitmap: Bitmap, file: File) {
        if (listbtimap.size < 4) {
            listbtimap.add(bitmap)
            listfilename.add(file)
            refreshimages()
        }
    }

    private fun refresheditimages() {
        for (i in 0..peteditlist.size - 1) {
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist.get(i))
                .into(listimageview[i])
            //  listimageview[i].setImageBitmap(listfilename.get(i))
            listcross[i].visibility = View.VISIBLE
        }
    }

    private fun refreshimages() {
        for (i in 0..listfilename.size - 1) {
            Glide.with(this).load(listfilename.get(i)).into(listimageview[i])
            //  listimageview[i].setImageBitmap(listfilename.get(i))
            listcross[i].visibility = View.VISIBLE
        }
    }

    private fun deleteimages() {
        binding.apply {
            imgCross1.setOnClickListener {
                showpopupmenu(imgCross1, 0)
            }
            imgCross2.setOnClickListener {
                showpopupmenu(imgCross2, 1)
            }
            imgCross3.setOnClickListener {
                showpopupmenu(imgCross3, 2)
            }
            imgCross4.setOnClickListener {
                showpopupmenu(imgCross4, 3)
            }
        }
    }

    private fun showpopupmenu(dots: ImageView, pos: Int) {
        if (pet_list!!.isNotEmpty()) {
            if (pet_list!!.size > 0) {
                popupMenu = PopupMenu(this, dots, Gravity.START, 0, R.style.MyPopupMenu)
                popupMenu.inflate(R.menu.editmenu)
                popupMenu.show()
            }
        } else {
            popupMenu = PopupMenu(this, dots, Gravity.START, 0, R.style.MyPopupMenu)
            popupMenu.inflate(R.menu.add_menu)
            popupMenu.show()
        }
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.delete -> {
                    if (pet_list!!.isNotEmpty()) {
                        if (pet_list!!.size > 0) {
                            deletepetimage(peteditlist[pos], petId)
                            if (pos == 0) {
                                binding.img1.setImageBitmap(null)
                                binding.imgCross1.visibility = View.GONE
                            } else if (pos == 1) {
                                binding.img2.setImageBitmap(null)
                                binding.imgCross2.visibility = View.GONE
                            } else if (pos == 2) {
                                binding.img3.setImageBitmap(null)
                                binding.imgCross3.visibility = View.GONE
                            } else if (pos == 3) {
                                binding.img4.setImageBitmap(null)
                                binding.imgCross4.visibility = View.GONE
                            }
                            peteditlist.removeAt(pos)
                            refresheditimages1()
                        }
                    } else {
                        // Add Case
                        if (pos == 0) {
                            binding.img1.setImageBitmap(null)
                            binding.imgCross1.visibility = View.GONE
                        } else if (pos == 1) {
                            binding.img2.setImageBitmap(null)
                            binding.imgCross2.visibility = View.GONE
                        } else if (pos == 2) {
                            binding.img3.setImageBitmap(null)
                            binding.imgCross3.visibility = View.GONE
                        } else if (pos == 3) {
                            binding.img4.setImageBitmap(null)
                            binding.imgCross4.visibility = View.GONE
                        }
                        listbtimap.removeAt(pos)
                        listfilename.removeAt(pos)
                        refreshimages1()
                    }
                }
                R.id.mp -> {
                    makepetprofilepic(pos)
                }
            }
            true
        })
    }

    private fun makepetprofilepic(position: Int) {
        if (position == 0) {
            if (pet_list!![0].petImages.size > 0)
                viewModel.petProfile(pet_list!![0]._id, pet_list!![0].petImages[0])
            else
                toast("Please save image to make it Profile image")
        } else if (position == 1) {
            if (pet_list!![0].petImages.size > 1)
                viewModel.petProfile(pet_list!![0]._id, pet_list!![0].petImages[1])
            else
                toast("Please save image to make it Profile image")
        } else if (position == 2) {
            if (pet_list!![0].petImages.size > 2)
                viewModel.petProfile(pet_list!![0]._id, pet_list!![0].petImages[2])
            else
                toast("Please save image to make it Profile image")
        } else if (position == 3) {
            if (pet_list!![0].petImages.size > 3)
                viewModel.petProfile(pet_list!![0]._id, pet_list!![0].petImages[3])
            else
                toast("Please save image to make it Profile image")
        }
    }


    private fun refreshimages1() {
        if (listfilename.size == 1) {
            listcross[0].visibility = View.VISIBLE
            listimageview[0].setImageBitmap(listbtimap.get(0))

            listcross[1].visibility = View.GONE
            listimageview[1].setImageBitmap(null)

        } else if (listfilename.size == 2) {

            listcross[0].visibility = View.VISIBLE
            listimageview[0].setImageBitmap(listbtimap.get(0))

            listcross[1].visibility = View.VISIBLE
            listimageview[1].setImageBitmap(listbtimap.get(1))

            listcross[2].visibility = View.GONE
            listimageview[2].setImageBitmap(null)

            listcross[3].visibility = View.GONE
            listimageview[3].setImageBitmap(null)

        } else if (listfilename.size == 3) {

            listcross[0].visibility = View.VISIBLE
            listimageview[0].setImageBitmap(listbtimap.get(0))

            listcross[1].visibility = View.VISIBLE
            listimageview[1].setImageBitmap(listbtimap.get(1))

            listcross[2].visibility = View.VISIBLE
            listimageview[2].setImageBitmap(listbtimap.get(2))

            listcross[3].visibility = View.GONE
            listimageview[3].setImageBitmap(null)
        }
    }

    private fun refresheditimages1() {
        if (peteditlist.size == 1) {
            listcross[0].visibility = View.VISIBLE
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist[0])
                .into(listimageview[0])

            listcross[1].visibility = View.GONE
            listimageview[1].setImageBitmap(null)

        } else if (peteditlist.size == 2) {
            listcross[0].visibility = View.VISIBLE
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist[0])
                .into(listimageview[0])

            listcross[1].visibility = View.VISIBLE
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist[1])
                .into(listimageview[1])


            listcross[2].visibility = View.GONE
            listimageview[2].setImageBitmap(null)

            listcross[3].visibility = View.GONE
            listimageview[3].setImageBitmap(null)
        } else if (peteditlist.size == 3) {
            listcross[0].visibility = View.VISIBLE
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist[0])
                .into(listimageview[0])

            listcross[1].visibility = View.VISIBLE
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist[1])
                .into(listimageview[1])

            listcross[2].visibility = View.VISIBLE
            Glide.with(this).load(ApiService.PET_IMAGE_BASE_URL + peteditlist[2])
                .into(listimageview[2])

            listcross[3].visibility = View.GONE
            listimageview[3].setImageBitmap(null)
        }
    }


    fun deletepetimage(imagename: String, id: String) {
        if (checkForInternet(this)) {
            viewModel.deletepetimages(imagename, id)
        } else {
            toast("No Internet Connection")
        }
    }

    private fun deletepetimageObserver() {
        viewModel.deletePetimage.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        toast("" + it.value.message)
                        pd.dismiss()
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