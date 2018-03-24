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
        Log.i(TAG, "Message received from ${remoteMessage?.from} : ${remoteMessage?.data} : ${remoteMessage?.notification?.body}")

        //Get the notification service
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: NotificationCompat.Builder = NotificationCompat.Builder(this@FCMMessagingService, application.packageName + ".USER_ACTION")
                .setSmallIcon(R.drawable.home_automation_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_home_automation_big))
                .setDefaults(Notification.DEFAULT_SOUND)

        if (remoteMessage?.data?.get("status") == "LOW") {
            notification
                    .setContentTitle(remoteMessage.data?.get("body"))
                    .setProgress(0, 0, true)
                    .setOngoing(true)
        } else {
            notification
                    .setContentTitle(remoteMessage?.data?.get("title"))
                    .setContentText(remoteMessage?.data?.get("body"))
                    .setProgress(0, 0, false)
                    .setOngoing(false)
        }

        //Send notification
        notificationManager.notify(1001, notification.build())
    }

    companion object {

        private val TAG: String? = FCMMessagingService::class.java.simpleName
    }
}