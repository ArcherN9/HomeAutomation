package com.daksh.homeautomation.MainActivity

import com.daksh.homeautomation.MainActivity.Model.Model
import com.daksh.homeautomation.MainActivity.Model.NodeModel
import com.daksh.homeautomation.RetroFit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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
     * Connects to the micro service and gets the updated list of Nodes
     */
    fun getNodes(listener: Listener) {
        //Get the call interface
        val call = apiInterface?.getNodes()
        //Execute the call
        call?.enqueue(object: Callback<MutableList<NodeModel>> {

            override fun onFailure(call: Call<MutableList<NodeModel>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<MutableList<NodeModel>>?, response: Response<MutableList<NodeModel>>?) {
                if(response?.isSuccessful!! && response.body() != null)
                    listener.onReceived(response.body()!!)
            }
        })
    }

    /**
     * Connects to the micro service to get the latest status of the lamp
     */
    fun updateStatus(listener: Listener) {
        val call = apiInterface?.getStatus("5a043eeece31f7367996a795")
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

    fun toggleSwitch(status: Boolean, nodeId: String?, listener: Listener) {
        val call = apiInterface?.toggleSwitch(status, nodeId!!)
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
        fun getStatus(
                @Query("nodeId") strMNodeId: String
        ): Call<Model>

        @GET("/api/toggleSwitch")
        fun toggleSwitch(
                @Query("status") strStatus: Boolean,
                @Query("nodeId") strNodeId: String
        ): Call<Model>

        @GET("/api/getAllNodes")
        fun getNodes(): Call<MutableList<NodeModel>>
    }
}