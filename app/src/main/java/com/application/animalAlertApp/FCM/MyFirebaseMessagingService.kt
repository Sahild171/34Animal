package com.application.animalAlertApp.FCM

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.application.animalAlertApp.R
import com.application.animalAlertApp.View.Friends.ConnectionsActivity
import com.application.animalAlertApp.View.Home.Comments.PostCommentsActivity
import com.application.animalAlertApp.View.Main.HomeActivity
import com.application.animalAlertApp.View.Requsts.ReqeustActivity
import com.application.animalAlertApp.di.BaseApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = String::class.java.simpleName
    private var id: String = ""

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        // Check if message contains a data payload.
        remoteMessage.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            // Compose and show notification
            val msg: String = remoteMessage.data.get("message")!!
            val title: String = remoteMessage.data.get("title")!!
            val type: String = remoteMessage.data.get("type")!!
            if (remoteMessage.data.containsKey("ID")){
                id = remoteMessage.data.get("ID")!!
            }
            if (type.equals("ChatMessage")) {
                if (!BaseApp.sIsChatActivityOpen) {
                    sendNotification(msg, title, type)
                }
            } else {
                sendNotification(msg, title, type)
            }
        }
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            sendNotification(remoteMessage.notification?.body, "AAC", "body")
        }
    }

    private fun sendNotification(messageBody: String?, title: String?, type: String) {
        val i: Intent
        if (type.equals("ChatMessage")) {
            i = Intent(this, HomeActivity::class.java)
            i.putExtra("Screeentype", "Chatscreen")
            i.setAction(Intent.ACTION_MAIN)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (type.equals("Alert")) {
            i = Intent(this, HomeActivity::class.java)
            i.putExtra("Screeentype", "AlertScreen")
            i.setAction(Intent.ACTION_MAIN)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (type.equals("Comment")) {
            i = Intent(this, PostCommentsActivity::class.java)
            i.putExtra("postid", id)
            i.setAction(Intent.ACTION_MAIN)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (type.equals("Like")) {
            i = Intent(this, HomeActivity::class.java)
            i.setAction(Intent.ACTION_MAIN)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (type.equals("Comment Like")) {
            i = Intent(this, PostCommentsActivity::class.java)
            i.putExtra("postid", id)
            i.setAction(Intent.ACTION_MAIN)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (type.equals("Friend")) {
            i = Intent(this, ConnectionsActivity::class.java)
            i.setAction(Intent.ACTION_MAIN)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else {
            i = Intent(this, ReqeustActivity::class.java)
            i.setAction(Intent.ACTION_MAIN)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }


//        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
//        stackBuilder.addParentStack(HomeActivity::class.java)
//        stackBuilder.addNextIntent(i)
//        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, i, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        // https://developer.android.com/training/notify-user/build-notification#Priority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

}