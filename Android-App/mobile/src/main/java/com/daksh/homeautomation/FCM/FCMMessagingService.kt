package com.daksh.homeautomation.FCM

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.daksh.homeautomation.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.i(TAG, "Message received from ${remoteMessage?.from} : ${remoteMessage?.data}")

        //Get the notification service
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = NotificationCompat.Builder(this@FCMMessagingService, application.packageName + ".USER_ACTION")
                .setContentTitle(remoteMessage?.data?.getValue("title"))
                .setContentText(remoteMessage?.data?.getValue("message"))
                .setSmallIcon(R.drawable.home_automation_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_home_automation_big))
                .build()

        //Send notification
        notificationManager.notify(1, notification)
    }

    companion object {

        private val TAG: String? = FCMMessagingService::class.java.simpleName
    }
}