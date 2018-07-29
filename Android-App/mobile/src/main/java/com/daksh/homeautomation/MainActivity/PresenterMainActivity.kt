package com.daksh.homeautomation.MainActivity

import android.os.Handler
import android.os.HandlerThread
import com.daksh.homeautomation.ElsaApplication
import com.daksh.homeautomation.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus

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

    override fun toggleSwitch(changedEntity: EntityDevices) {
        //Send a broadcast to the home hub and inform to switch the switch
        val type = object: TypeToken<EntityDevices>(){}.type
        view.getElsaApplication().pubnub.publish()
                .message(Gson().toJson(changedEntity, type))
                .channel(view.getAppResources().getString(R.string.home_devices_actions))
                .async(object: PNCallback<PNPublishResult>() {

                    override fun onResponse(result: PNPublishResult?, status: PNStatus?) {
                        ElsaApplication.log(status.toString())
                    }
                })
    }

    /** *********************** Extension functions *********************** */
}