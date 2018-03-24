package com.daksh.homeautomation.MainActivity

import android.widget.Toast
import com.daksh.homeautomation.ElsaApplication
import com.daksh.homeautomation.MainActivity.Model.NodeModel
import com.daksh.homeautomation.MainActivity.Model.NodeModel_
import com.daksh.homeautomation.RetroFit
import io.objectbox.Box
import io.objectbox.Property
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.ConnectException

class MainActivityController private constructor() {

    //The reference to the attached activity | This is used to access context and stuff
    private lateinit var mainActivity: MainActivity
    //The BoxStore is like DAO for the NodeModel class. Use it for all interactions with this
    //object
    private lateinit var boxStore: Box<NodeModel>
    //A premade query to be executed everytime getAllnodes API is requested. It speeds up the process
    //of requesting from DB
    private lateinit var getAllNodesQuery: io.objectbox.query.Query<NodeModel>

    private lateinit var callbackListener: Listener

    constructor(mainActivity: MainActivity): this() {
        //Accept the Activity reference
        this@MainActivityController.mainActivity = mainActivity

        //Instantiate the listener
        callbackListener = mainActivity as Listener

        //Create the DB if it doesn't exist
        boxStore = (mainActivity.application as ElsaApplication)
                .objectBox
                .boxFor(NodeModel::class.java)

        //Setup the query during initialization and execute later
        getAllNodesQuery = boxStore.query().build()

        //Setup an observer for this
        getAllNodesQuery
                .subscribe()
                .observer { data -> mainActivity.onReceived(data) }

        //initialize RetroFit
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
    fun getNodes() {

        //Return all nodes from the DB immediately | do a background silent call to the server
        //and update if necessary
        val lsModel: MutableList<NodeModel> = getAllNodesQuery.find()
        //Pass on to the activity to call the RecyclerView
        callbackListener.onReceived(lsModel)

        //Get the call interface
        val call = apiInterface?.getNodes()
        //Execute the call
        call?.enqueue(object: Callback<MutableList<NodeModel>> {

            override fun onFailure(call: Call<MutableList<NodeModel>>?, t: Throwable?) {
                //Show error message
                if(t is ConnectException)
                    Toast.makeText(mainActivity.baseContext, "Could not reach server. Application is offline.", Toast.LENGTH_SHORT).show()

                //No update sent to UI | Not required
            }

            override fun onResponse(call: Call<MutableList<NodeModel>>?, response: Response<MutableList<NodeModel>>?) {
                if(response?.isSuccessful!! && response.body() != null)
                //Iterate over the JSON response's body
                    for(jsModel in response.body()!!) {
                        //Check if the model from JSON is already present in our ObjectBox
                        val dbList = boxStore.query()
                                .equal(NodeModel_.nodeId, jsModel.nodeId!!)
                                .build()
                                .find()

                        //If it is, iterate over it and update the ID in the JSON body so that
                        //proper updation can be done in ObjectBox
                        for(dbModel in dbList)
                            jsModel.id = dbModel.id
                    }

                //Forward all data to the ObjectBox
                boxStore.put(response.body())
            }
        })
    }

    fun toggleSwitch(status: Boolean, nodeId: String?) {
        val call = apiInterface?.toggleSwitch(status, nodeId!!)
        call?.enqueue(object: Callback<NodeModel> {

            override fun onResponse(call: Call<NodeModel>?, response: Response<NodeModel>?) {
                if(response?.isSuccessful!! && response.body() != null) {
                    //Check if the model from JSON is already present in our ObjectBox
                    val dbList = boxStore.query()
                            .equal(NodeModel_.nodeId, response.body()!!.nodeId)
                            .build()
                            .find()

                    //If it is, iterate over it and update the ID in the JSON body so that
                    //proper updation can be done in ObjectBox
                    for (dbModel in dbList) {
                        response.body()!!.id = dbModel.id
                        response.body()!!.nodeName = dbModel.nodeName
                        response.body()!!.nodeDescription = dbModel.nodeDescription
                    }

                    //Update same to Database
                    boxStore.put(response.body()!!)
                }
            }

            override fun onFailure(call: Call<NodeModel>?, t: Throwable?) {

                //Show error message
                if(t is ConnectException)
                    Toast.makeText(mainActivity.baseContext, "Could not reach server. Please check your internet connection", Toast.LENGTH_SHORT).show()

                //Refresh data from DB to reset updated views
                callbackListener.onReceived(getAllNodesQuery.find())
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
        ): Call<NodeModel>

        @GET("/api/toggleSwitch")
        fun toggleSwitch(
                @Query("status") strStatus: Boolean,
                @Query("nodeId") strNodeId: String
        ): Call<NodeModel>

        @GET("/api/getAllNodes")
        fun getNodes(): Call<MutableList<NodeModel>>
    }
}