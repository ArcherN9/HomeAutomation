package com.daksh.homeautomation.FCM

import android.annotation.SuppressLint
import android.provider.Settings
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
        Log.d(TAG, "Refreshed token: " + refreshedToken)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
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