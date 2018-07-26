package com.daksh.homeautomation.MainActivity

import android.os.Handler
import android.os.HandlerThread
import android.widget.Toast
import com.daksh.homeautomation.R
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException

class PresenterMainActivity(private var view: ContractMain.View) : ContractMain.Presenter {

    //The reference to the attached activity | This is used to access context and stuff
    private lateinit var mainActivity: MainActivity

    //Handler for minimal tasks in BG
    override lateinit var mHandler: Handler
    override lateinit var mHandlerThread: HandlerThread

    override fun start() {
        //Create threads to do trivial background tasks with option to handle message in UI thread
        if(!::mHandlerThread.isInitialized) {
            mHandlerThread = HandlerThread(TAG)
            mHandlerThread.start()
        }

        if(!::mHandler.isInitialized)
            mHandler = Handler(mHandlerThread.looper)
    }

    companion object {

        //Tag for logging
        private val TAG: String = PresenterMainActivity::class.java.simpleName
    }

    /**
     * Returns a json object to request for all devices in the DB
     */
    private fun CreateJsonDeviceSwitch(nodeId: Long?, status: Boolean):JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("_id", nodeId)
        jsonObject.put("status", status)
        return jsonObject
    }

    /**
     * Connects to the micro service and gets the updated list of Nodes
     */
    override fun loadList() {

        //Return all nodes from the DB immediately | do a background silent call to the server
        //and update if necessary
        if(::mHandler.isInitialized)
            mHandler.post {
                val lsModel: MutableList<EntityDevices>? = view.getDeviceDB().getAllDevices()
                //Pass on to the activity to call the RecyclerView
                view.showDeviceList(lsModel)
            }
    }

    override fun toggleSwitch(status: Boolean, nodeId: Long?) {

        //Send a broadcast to get device list
        view.getElsaApplication().pubnub.publish()
                .message(CreateJsonDeviceSwitch(nodeId, status))
                .channel(view.getAppResources().getString(R.string.home_devices_actions))
                .async(object: PNCallback<PNPublishResult>() {

                    override fun onResponse(result: PNPublishResult?, status: PNStatus?) {
//                        //Check if the model from JSON is already present in our ObjectBox
//                        val device = view.getDeviceDB().getDeviceById(response.body()?._id)
//
//                        //If it is, iterate over it and update the ID in the JSON body so that
//                        //proper updation can be done in ObjectBox
//                        device?._id?.apply {
//                            device.isDeviceSwitchedOn = response.body()?.isDeviceSwitchedOn!!
//                            mHandler.post { view.getDeviceDB().updateDevice(device = device) }
//                        }
                    }
                })
    }
}