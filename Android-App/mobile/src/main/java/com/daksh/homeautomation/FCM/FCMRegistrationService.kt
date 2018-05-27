package com.daksh.homeautomation.FCM

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.util.Log
import com.daksh.homeautomation.FCM.Model.FCMModel
import com.daksh.homeautomation.RetroFit
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class FCMRegistrationService: FirebaseInstanceIdService(), Callback<FCMModel> {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        //Ready the RetroFit interface
        init()

        // Get updated InstanceID token.
        val refreshedToken: String? = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: $refreshedToken")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)

        //Notification channels are registered for devices running on API 26 and above.
        //Without this, the user has very little control over what notifications he wishes to see
        //and what not
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            registerNotificationChannel()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerNotificationChannel() {
        //Register a notification channel now | This is done here because we don't want to
        //execute the creation of notification channel everytime the app is used
        val mNotificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // The id of the channel.
        val id: String = application.packageName + ".USER_ACTION"
        // The user-visible name of the channel.
        val strName: CharSequence = "User Action Channel"
        // The user-visible description of the channel.
        val strDescription: String = "All notifications which require user action to perform the " +
                "very purpose of the application are posted on this channel. If permissions are taken away, " +
                "the application might have unintended behavior."
        val mChannel = NotificationChannel(id, strName, NotificationManager.IMPORTANCE_HIGH)
        // Configure the notification channel.
        mChannel.description = strDescription
        mChannel.enableLights(true)
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
//        mChannel.vibrationPattern = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}
        mNotificationManager.createNotificationChannel(mChannel)
    }

    @SuppressLint("HardwareIds")
    private fun sendRegistrationToServer(refreshedToken: String?) {
        val api: Call<FCMModel>? = apiInterface?.registerDevice(refreshedToken, android.os.Build.MODEL, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
        api?.enqueue(this@FCMRegistrationService)
    }

    override fun onFailure(call: Call<FCMModel>?, t: Throwable?) {
    }

    override fun onResponse(call: Call<FCMModel>?, response: Response<FCMModel>?) {
        Log.i(TAG, response?.body().toString())
    }

    companion object {

        //Tag for logging
        val TAG: String = FCMRegistrationService::class.java.simpleName

        /**
         * An API interface used in the main activity. It comprises of all services limited to the
         * Main Activity.
         */
        private var apiInterface: APIInterface? = null

        /**
         * A method to initiate and create the API interface for MainActivity.
         * @return
         */
        private fun init(): APIInterface? {
            return if(apiInterface == null) {
                apiInterface = RetroFit.getRetrofit().create(APIInterface::class.java)
                apiInterface
            } else
                apiInterface
        }
    }

    /**
     * The interface used by retrofit to define network calls
     */
    private interface APIInterface {

        //Segmented path goes here. For illustration purpose, we've used '/autocomplete' end point in
        //conjunction with the server address mentioned during retrofit initialization
        @FormUrlEncoded
        @POST("/api/registerDevice")
        fun registerDevice(
                @Field("fcmid") strFcmId:String?,
                @Field("devicename") strDeviceName: String?,
                @Field("uid") strUid: String?
        ): Call<FCMModel>
    }
}