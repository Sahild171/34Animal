package com.application.animalAlertApp.View.Chats

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.application.animalAlertApp.Model.Chat
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityQuestionsBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kaopiz.kprogresshud.KProgressHUD
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
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class QuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionsBinding
    private val viewmodel: QuestionsViewModel by viewModels()
    private var fileName: String? = null
    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_GALLERY_IMAGE = 1
    private val lockAspectRatio = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private val ASPECT_RATIO_X = 16.0f
    private var ASPECT_RATIO_Y = 9.0f
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80

    private lateinit var imageviewlist: ArrayList<ImageView>
    private lateinit var imgcrosslist: ArrayList<ImageView>
    private lateinit var imagefilelist: ArrayList<File>
    private lateinit var bitmaplist: ArrayList<Bitmap>
    private lateinit var petImg: ArrayList<MultipartBody.Part>
    private lateinit var progress: KProgressHUD
    private lateinit var alert_id: String
    private lateinit var chat_room: String
    private lateinit var Receverid: String
    private lateinit var Recevername: String
    private lateinit var ReceverImage: String
    private var ReciverDevicetoken:String=""
    private var myRef: DatabaseReference? = null
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var userprefrence: UserPreferences
    private var myimage: String = ""
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAUL5s0KE:APA91bHZVpp7cukVkmm2pniP-CxGnKheuZfxrOpmb37knWIoVc-WmXcYVEp-x3yrLWedkBr2-irWzUFEfEEp2T5rZRdIVZPuoTn8bYsx_78t_TbY1ekaZFO_hFDyWD0RM4S96VbfAyUa"
    private val contentType = "application/json"

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_questions)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alert_id = intent.getStringExtra("alertid").toString()
        chat_room = intent.getStringExtra("ChatRoom").toString()
        Receverid = intent.getStringExtra("ReceverId").toString()
        Recevername = intent.getStringExtra("ReceverName").toString()
        ReceverImage = intent.getStringExtra("ReceverImage").toString()
        ReciverDevicetoken= intent.getStringExtra("devicetoken").toString()

        mySharedPreferences = MySharedPreferences(this)
        userprefrence = UserPreferences(this)


        imageviewlist = ArrayList()
        bitmaplist = ArrayList()
        imagefilelist = ArrayList()
        imgcrosslist = ArrayList()
        petImg = ArrayList()


        imageviewlist.add(binding.img1)
        imageviewlist.add(binding.img2)
        imageviewlist.add(binding.img3)
        imageviewlist.add(binding.img4)
        imgcrosslist.add(binding.imgCross1)
        imgcrosslist.add(binding.imgCross2)
        imgcrosslist.add(binding.imgCross3)
        imgcrosslist.add(binding.imgCross4)

        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")

        deleteimages()

        userprefrence.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                myimage = it
            }
        })

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.btSend.setOnClickListener {
            if (validation())
                savedata()
        }
        binding.uploadimage.setOnClickListener {
            setImage()
        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(this@QuestionsActivity, ChatActivity::class.java)
            intent.putExtra("ReceverId", Receverid)
            intent.putExtra("ReceverName", Recevername)
            intent.putExtra("ReceverImage", ReceverImage)
            intent.putExtra("ChatRoom", chat_room)
            intent.putExtra("devicetoken",ReciverDevicetoken)
            startActivity(intent)
            finish()
        }
    }

    private fun savedata() {
        val alertid: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), alert_id)
        val wherePetLastSeen: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etLastseen.text.toString().trim()
        )
        val whenPetLastSeen: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etWhenLastSeen.text.toString().trim()
        )
        val friendlyToApproach: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etPetFriendly.text.toString().trim()
        )
        val contactDetail: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etSharecontact.text.toString().trim()
        )
        val otherComment: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.etOthercomment.text.toString().trim()
        )
        petImg.clear()
        for (i in 0..imagefilelist.size - 1) {
            petImg.add(prepareFilePart("petImg", imagefilelist.get(i)))
        }
        lifecycleScope.launchWhenStarted {
            viewmodel.addquestions(
                alertid,
                wherePetLastSeen,
                whenPetLastSeen,
                friendlyToApproach,
                contactDetail,
                otherComment,
                petImg
            ).catch { e ->
                toast("exception==" + e)
                Log.e("exception", "" + e)
                progress.dismiss()
            }.onStart {
                progress.show()
            }.collect { data ->
                progress.dismiss()
                if (data.status == 200) {
                    if (data.docs.petImg.size > 0) {
                        saveQuestionsOnFirebase(ApiService.PET_IMAGE_BASE_URL + data.docs.petImg[0])
                    } else {
                        saveQuestionsOnFirebase("")
                    }
                } else {
                    toast("" + data.message)
                }
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.etLastseen.text.toString().trim().isEmpty()) {
            binding.etLastseen.error = "Empty"
            return false
        } else if (binding.etWhenLastSeen.text.toString().trim().isEmpty()) {
            binding.etWhenLastSeen.error = "Empty"
            return false
        } else if (binding.etPetFriendly.text.toString().trim().isEmpty()) {
            binding.etPetFriendly.error = "Empty"
            return false
        } else if (binding.etSharecontact.text.toString().trim().isEmpty()) {
            binding.etSharecontact.error = "Empty"
            return false
        }
        return true
    }

    private fun saveQuestionsOnFirebase(petimage: String) {
        val text = "Where was the pet last seen? <br>" +
                "<b>" + binding.etLastseen.text.toString().trim() + "</b>" +
                "<br>" +
                "When was the pet last seen?<br>" +
                "<b>" + binding.etWhenLastSeen.text.toString().trim() + "</b>" +
                "<br>" +
                "Is the pet friendly to approach?" +
                "<br>" +
                "<b>" + binding.etPetFriendly.text.toString().trim() + "</b>" +
                "<br>" +
                "If you are comfortable then your<br>" + "contact details?" +
                "<br>" +
                "<b>" + binding.etSharecontact.text.toString().trim() + "</b>" +
                "<br>" +
                "Other comments <br>" +
                "<b>" + binding.etOthercomment.text.toString().trim() + "</b>"


        val databaseReference = FirebaseDatabase.getInstance().reference.child("User")
        val chat = Chat(
            "Questions",
            "",
            mySharedPreferences.getUsername()!!,
            Recevername,
            mySharedPreferences.getUserid()!!,
            Receverid,
            "" + System.currentTimeMillis(),
            "", petimage, text
        )
        databaseReference.child("ChatRoom").child(chat_room)
            .child(System.currentTimeMillis().toString()).setValue(chat).addOnCompleteListener {
            }

        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put("type", "Message")
        hashMap.put("msg", "Questions")
        hashMap.put("name", Recevername)
        hashMap.put("timestamp", "" + System.currentTimeMillis())
        hashMap.put("image", ReceverImage)
        hashMap.put("id", Receverid)
        databaseReference.child("ChatRoomList").child(mySharedPreferences.getUserid()!!)
            .child("userid").child(Receverid)
            .setValue(hashMap as Map<String, Any>).addOnCompleteListener {
            }

        val hashMap1: HashMap<String, String> = HashMap<String, String>()
        hashMap1.put("type", "Message")
        hashMap1.put("msg", "Questions")
        hashMap1.put("name", mySharedPreferences.getUsername()!!)
        hashMap1.put("timestamp", "" + System.currentTimeMillis())
        hashMap1.put("image", myimage)
        hashMap1.put("id", mySharedPreferences.getUserid()!!)
        databaseReference.child("ChatRoomList").child(Receverid).child("userid")
            .child(mySharedPreferences.getUserid()!!)
            .setValue(hashMap1 as Map<String, Any>).addOnCompleteListener {
                sendnotification("Someone react on your alert")
            }
        val intent = Intent(this@QuestionsActivity, ChatActivity::class.java)
        intent.putExtra("ReceverId", Receverid)
        intent.putExtra("ReceverName", Recevername)
        intent.putExtra("ReceverImage", ReceverImage)
        intent.putExtra("ChatRoom", chat_room)
        intent.putExtra("devicetoken",ReciverDevicetoken)
        startActivity(intent)
        finish()
    }


    private fun sendnotification(notifymessage: String) {
        val notification = JSONObject()
        val notifcationBody = JSONObject()
      //  Log.e("devicetoken", ReciverDevicetoken)

        try {
            notifcationBody.put("title", "New Message")
            notifcationBody.put("type", "ChatMessage")
            notifcationBody.put("ID","123456")
            notifcationBody.put("message", notifymessage)   //Enter your notification message
            notification.put("to", ReciverDevicetoken)
            notification.put("data", notifcationBody)
            sendNotificationwwe(notification)
        } catch (e: JSONException) {
            Log.e("TAG", "onCreate: " + e.message)
        }

    }

    private fun sendNotificationwwe(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
                //  toast("sent")
                // msg.setText("")
            },
            Response.ErrorListener {
                Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
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
                Log.e("Error", "Crop error: $cropError")
                setResultCancelled()
            }
            else -> setResultCancelled()
        }
    }




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_IMAGE_CAPTURE) {
//                var myBitmap = BitmapFactory.decodeFile(file!!.absolutePath)
//                val imageRotation: Int = getImageRotation(file!!)
//                binding.constraintImages.visibility = View.VISIBLE
//                if (imageRotation != 0)
//                    myBitmap = getBitmapRotatedByDegree(
//                        myBitmap, imageRotation
//                    )
//
//                setimages1(myBitmap, file!!)
//
//            } else if (requestCode == REQUEST_PICK_IMAGE) {
//                val uri = data?.getData()
//                try {
//                    binding.constraintImages.visibility = View.VISIBLE
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                    val tempUri = getImageUri(applicationContext, bitmap)
//                    val finalFile = File(getRealPathFromURI(tempUri))
//                    setimages1(bitmap, finalFile)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

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




}