package com.application.animalAlertApp.Fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.PhotosAdapter
import com.application.animalAlertApp.Interfaces.DeletePortfolio
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.BusinessProfile.BusinessViewModel
import com.application.animalAlertApp.databinding.FragmentPhotosBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PhotosFragment : Fragment(), DeletePortfolio {
    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_GALLERY_IMAGE = 1
    private val lockAspectRatio = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private val ASPECT_RATIO_X = 16.0f
    private var ASPECT_RATIO_Y = 9.0f
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80
    private lateinit var adapter: PhotosAdapter
    private lateinit var binding: FragmentPhotosBinding
    private val viewmodel: BusinessViewModel by activityViewModels()
    private lateinit var list: ArrayList<String>
    private var fileName: String? = null
    private var file: File? = null
    private var userid: String = ""
    private lateinit var sharedPreferences: MySharedPreferences
    private lateinit var pd: Dialog
    private var seletedpos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotosBinding.inflate(layoutInflater, container, false)
        sharedPreferences = MySharedPreferences(requireContext())
        pd = MyProgressBar.progress(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()

        viewmodel.businessdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        list.clear()
                        if (it.value.getShop.size > 0) {
                            userid = it.value.getShop[0].userId
                            if (!userid.equals(sharedPreferences.getUserid())) {
                                binding.addMore.visibility = View.GONE
                            }
                            list.addAll(it.value.getShop[0].portfolioLogo)
                            if (list.size > 0) {
                                binding.tvNoalert.visibility = View.GONE
                            } else {
                                binding.tvNoalert.visibility = View.VISIBLE
                            }
                            adapter = PhotosAdapter(
                                context,
                                list,
                                this,
                                userid,
                                sharedPreferences.getUserid()!!
                            )
                            binding.recyclerview.adapter = adapter
                        }
                    } else {
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                }
            }
        })


        binding.addMore.setOnClickListener {
            if (userid.equals(sharedPreferences.getUserid())) {
                setImage()
            }
        }
        getaddnewportfolio()
        deleteobserver()
    }


    private fun getaddnewportfolio() {
        viewmodel.editportfolio.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        list.add(it.value.portfolioLogo[0])
                        if (list.size > 0) {
                            binding.tvNoalert.visibility = View.GONE
                        } else {
                            binding.tvNoalert.visibility = View.VISIBLE
                        }
                        adapter = PhotosAdapter(
                            context,
                            list,
                            this,
                            userid,
                            sharedPreferences.getUserid()!!
                        )
                        binding.recyclerview.adapter = adapter
                    } else {
                        pd.dismiss()
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }


    private fun setImage() {
        val option = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context)
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
        Dexter.withActivity(requireActivity())
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
        Dexter.withActivity(activity)
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
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    cropImage(getCacheImagePath(fileName!!)!!)
                } else {
                    setResultCancelled()
                }
            REQUEST_GALLERY_IMAGE -> if (resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri: Uri = Uri.parse(data?.dataString)
                cropImage(imageUri)
            } else {
                setResultCancelled()
            }
            UCrop.REQUEST_CROP ->
                if (resultCode == AppCompatActivity.RESULT_OK) {
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
        val destinationUri = Uri.fromFile(
            File(context?.getCacheDir(), queryName(context?.getContentResolver()!!, sourceUri)!!))
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.green))
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.green))
        options.setActiveWidgetColor(ContextCompat.getColor(requireContext(), R.color.green))
        if (lockAspectRatio) options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y)
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)

        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(3f, 2f)
            .start(requireContext(), this)
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
        context?.toast("Cancelled")
    }

    private fun setResultOk(resultUri: Uri?) {
        file = File(resultUri!!.path)
        var portfolioLogo: MultipartBody.Part? = null
        if (file != null) {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), file!!)
            portfolioLogo =
                MultipartBody.Part.createFormData("portfolioLogo", file?.name, requestFile)
        } else {
            val requestFile = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            portfolioLogo = MultipartBody.Part.createFormData("portfolioLogo", "", requestFile)
        }
        viewmodel.editportfoio(portfolioLogo)

    }

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(context?.getExternalCacheDir(), "AnimalAlertCommunity")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            requireContext(),
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

    override fun ondelete(pos: Int, imagename: String) {
        deleteprotfolio(pos, imagename)
    }

    private fun deleteprotfolio(pos: Int, imagename: String) {
        seletedpos = pos
        viewmodel.deleteportfoliophoto(imagename)
    }

    private fun deleteobserver() {
        viewmodel.deleteportfolio.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        list.removeAt(seletedpos)
                        if (list.size > 0) {
                            binding.tvNoalert.visibility = View.GONE
                        } else {
                            binding.tvNoalert.visibility = View.VISIBLE
                        }

                        adapter = PhotosAdapter(
                            context,
                            list,
                            this,
                            userid,
                            sharedPreferences.getUserid()!!
                        )
                        binding.recyclerview.adapter = adapter
                    } else {
                        pd.dismiss()
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }

}