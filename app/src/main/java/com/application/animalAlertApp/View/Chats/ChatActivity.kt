package com.application.animalAlertApp.View.Chats

import android.Manifest
import android.content.ContentResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.application.animalAlertApp.Adapters.ChatAdapter
import com.application.animalAlertApp.Model.Chat
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityChatBinding
import com.google.firebase.database.*
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.asLiveData
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.application.animalAlertApp.Interfaces.Iteminterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.di.BaseApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kaopiz.kprogresshud.KProgressHUD
import kotlin.collections.ArrayList
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.yalantis.ucrop.UCrop
import org.json.JSONException
import org.json.JSONObject


class ChatActivity : AppCompatActivity(), Iteminterface {
    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_GALLERY_IMAGE = 1
    private val lockAspectRatio = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private val ASPECT_RATIO_X = 16.0f
    private var ASPECT_RATIO_Y = 9.0f
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80
    private lateinit var binding: ActivityChatBinding
    private var query: Query? = null
    private var adapter: ChatAdapter? = null
    private var myRef: DatabaseReference? = null
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var list: ArrayList<Chat>
    private var storage: FirebaseStorage? = null
    private var share: Boolean = true
    private lateinit var progress: KProgressHUD
    private lateinit var chat_room: String
    private lateinit var Receverid: String
    private lateinit var Recevername: String
    private lateinit var ReceverImage: String
    private lateinit var Reciverdevicetoken: String
    private var fileName: String? = null
    private var myimage: String = ""
    private lateinit var userPreferences: UserPreferences
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + ""
    private val contentType = "application/json"

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_chat)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        mySharedPreferences = MySharedPreferences(this)

        chat_room = intent.getStringExtra("ChatRoom").toString()
        Receverid = intent.getStringExtra("ReceverId").toString()
        Recevername = intent.getStringExtra("ReceverName").toString()
        ReceverImage = intent.getStringExtra("ReceverImage").toString()
        Reciverdevicetoken = intent.getStringExtra("devicetoken").toString()
        binding.tvName.text = Recevername

        list = ArrayList()


        Glide.with(this).load(ApiService.IMAGE_BASE_URL + ReceverImage).diskCacheStrategy(
            DiskCacheStrategy.ALL
        ).placeholder(R.drawable.placeholder).into(binding.imgProfile)

        userPreferences.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            myimage = it!!
        })

        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")

        val database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        myRef = database.getReference("User").child("ChatRoom")

        if (chat_room != null) {
            query = myRef?.child(chat_room)?.limitToLast(30)
            query?.addChildEventListener(childEventListener)
        }

        binding.btSend.setOnClickListener {
            if (binding.etMsg.text.toString().isNotEmpty()) {
                saveChatDatabase(
                    "Message",
                    binding.etMsg.text.toString().trim(),
                    Recevername,
                    Receverid,
                    mySharedPreferences.getUsername()!!,
                    mySharedPreferences.getUserid()!!,
                    "" + System.currentTimeMillis(),
                    chat_room,
                    "",
                    mySharedPreferences.getdevicetoken()!!,
                    Reciverdevicetoken
                )
                binding.etMsg.setText("")
            }
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.gallery.setOnClickListener {
            chooseImageFromGallery()
        }
        binding.camera.setOnClickListener {
            takeCameraImage()
        }

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
                Log.e("BusinessProfile", "Crop error: $cropError")
                setResultCancelled()
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

        // applying UI theme
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
        val file = File(resultUri!!.path)
        if (file != null) {
            binding.constraintShare.visibility = View.GONE
            val storageRef: StorageReference =
                storage?.getReferenceFromUrl("gs://animal-alert-community.appspot.com")!!
                    .child(file.name)
            sendFileFirebase(storageRef, resultUri)
        }
    }

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(getExternalCacheDir(), "AnimalAlertCommunity")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            this,
            "" +
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


    private fun toggle(show: Boolean) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.constraintShare)
        TransitionManager.beginDelayedTransition(binding.parent, transition)
        binding.constraintShare.visibility = if (show) View.VISIBLE else View.GONE
    }


    private fun sendFileFirebase(storageReference: StorageReference?, file: Uri) {
        val task: UploadTask = storageReference!!.putFile(file)
        task.addOnSuccessListener { t ->
            storageReference.downloadUrl.addOnSuccessListener { url ->
                progress.dismiss()
                val downloadUrl: Uri = url
                saveChatDatabase(
                    "Image",
                    "",
                    Recevername,
                    Receverid,
                    mySharedPreferences.getUsername()!!,
                    mySharedPreferences.getUserid()!!,
                    "" + System.currentTimeMillis(), chat_room, downloadUrl.toString(),
                    mySharedPreferences.getdevicetoken()!!,
                    Reciverdevicetoken
                )
            }
        }.addOnFailureListener { e ->
            toast("" + e.message)
            progress.dismiss()
        }.addOnProgressListener {
            progress.show()
        }
    }


    private fun saveChatDatabase(
        type: String,
        msg: String,
        receiverName: String,
        receiverUID: String,
        senderName: String,
        senderUID: String,
        timestamp: String,
        ChatRoom: String?,
        image: String,
        mydevicetoken: String,
        recdevtoken: String
    ) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("User")
        val chat = Chat(
            type,
            msg,
            senderName,
            receiverName,
            senderUID,
            receiverUID,
            timestamp,
            image, "", "", mydevicetoken, recdevtoken
        )
        databaseReference.child("ChatRoom").child(chat_room)
            .child(System.currentTimeMillis().toString()).setValue(chat).addOnCompleteListener {
                //                    toast("sent")
            }

        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap["type"] = type
        hashMap["msg"] = msg
        hashMap["name"] = receiverName
        hashMap["timestamp"] = "" + System.currentTimeMillis()
        hashMap["image"] = ReceverImage
        hashMap["id"] = receiverUID
        hashMap["devicetoken"] = recdevtoken
        databaseReference.child("ChatRoomList").child(senderUID).child("userid").child(receiverUID)
            .setValue(hashMap as Map<String, Any>).addOnCompleteListener {

//                    toast("sent")

            }
        val hashMap1: HashMap<String, String> = HashMap<String, String>()
        hashMap1["type"] = type
        hashMap1["msg"] = msg
        hashMap1["name"] = senderName
        hashMap1["timestamp"] = "" + System.currentTimeMillis()
        hashMap1["image"] = myimage
        hashMap1["id"] = senderUID
        hashMap1["devicetoken"] = mydevicetoken
        databaseReference.child("ChatRoomList").child(receiverUID).child("userid").child(senderUID)
            .setValue(hashMap1 as Map<String, Any>).addOnCompleteListener {
                if (type.equals("Image")) {
                    sendnotification("Image")
                } else {
                    sendnotification(msg)
                }

            }
    }

    private fun sendnotification(notifymessage: String) {
        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notifcationBody.put("title", "New Message")
            notifcationBody.put("type", "ChatMessage")
            notifcationBody.put("ID","123456")
            notifcationBody.put("message", notifymessage) //Enter your notification message
            notification.put("to", Reciverdevicetoken)
            notification.put("data", notifcationBody)
            sendNotificationwwe(notification)
        } catch (e: JSONException) {
            Log.e("TAG", "onCreate: " + e.message)
        }

    }


    private var childEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val userChat = dataSnapshot.getValue(Chat::class.java)
            list.add(userChat!!)
            if (list.size > 0) {
                adapter = ChatAdapter(
                    this@ChatActivity,
                    list,
                    mySharedPreferences.getUserid()!!, this@ChatActivity)
                binding.recyclerview.adapter = adapter
                binding.recyclerview.scrollToPosition(adapter!!.itemCount - 1)
            } else {
                Toast.makeText(this@ChatActivity, "No data found", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }


    override fun onitemclick() {

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

    override fun onStart() {
        super.onStart()
        BaseApp.sIsChatActivityOpen = true
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApp.sIsChatActivityOpen = false
    }

}