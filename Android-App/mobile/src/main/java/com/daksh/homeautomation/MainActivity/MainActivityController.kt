package com.daksh.homeautomation.MainActivity

import com.daksh.homeautomation.MainActivity.Model.Model
import com.daksh.homeautomation.RetroFit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

class MainActivityController {

    constructor() {
        init()
    }

    companion object {

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
     * Connects to the micro service to get the latest status of the lamp
     */
    fun updateStatus(listener: Listener) {
        val call = apiInterface?.getStatus()
        call?.enqueue(object: Callback<Model> {

            override fun onResponse(call: Call<Model>?, response: Response<Model>?) {
                if(response?.isSuccessful!! && response.body() != null)
                    listener.onReceived(response.body())
            }

            override fun onFailure(call: Call<Model>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun toggleSwitch(listener: Listener) {
        val call = apiInterface?.toggleSwitch()
        call?.enqueue(object: Callback<Model> {

            override fun onResponse(call: Call<Model>?, response: Response<Model>?) {
                if(response?.isSuccessful!! && response.body() != null)
                    listener.onReceived(response.body())
            }

            override fun onFailure(call: Call<Model>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    /**
     * The interface used by retrofit to define network calls
     */
    private interface APIInterface {

        //Segmented path goes here. For illustration purpose, we've used '/autocomplete' end point in
        //conjunction with the server address mentioned during retrofit initialization
        @GET("/api/getStatus")
        fun getStatus(): Call<Model>

        @GET("/api/toggleSwitch")
        fun toggleSwitch(): Call<Model>
    }
}