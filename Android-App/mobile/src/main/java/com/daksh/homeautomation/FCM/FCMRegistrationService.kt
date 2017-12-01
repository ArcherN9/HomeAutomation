package com.daksh.homeautomation

import android.util.Log
import com.daksh.homeautomation.MainActivity.MainActivityController
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import retrofit2.Call
import retrofit2.http.POST

class FCMRegistrationService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        // Get updated InstanceID token.
        val refreshedToken: String? = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(refreshedToken: String?) {

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
        @POST("/api/registerDevice")
        fun getNodes(): Call<MutableList<>>
    }
}