package com.hcl.daksh.android_poc_camp.Dashboard

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.Toast
import android_home_hub.daksh.com.homeautomation_hub.AppDatabase
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import android_home_hub.daksh.com.homeautomation_hub.Main.AddDevices.AddNewFragment
import android_home_hub.daksh.com.homeautomation_hub.Main.DeviceRecyclerViewAdapter
import android_home_hub.daksh.com.homeautomation_hub.Main.Model.ModelDevice
import android_home_hub.daksh.com.homeautomation_hub.Main.RFDeviceInteraction
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresenterMain(private var view: ContractMain.View): ContractMain.Presenter, Callback<ModelDevice> {
    init {
        //Associate this presenter with the view
        view.presenter = this@PresenterMain
    }

    // The recyclerView adapter that is attached to the activity
    private var recyclerViewAdapter: DeviceRecyclerViewAdapter? = null

    // Handler for minimal tasks in BG
    override lateinit var mHandler: Handler
    override lateinit var mHandlerThread: HandlerThread
    private lateinit var mMainHandler: Handler

    override fun start() {
        //Create threads to do trivial background tasks with option to handle message in UI thread
        mHandlerThread = HandlerThread(HomeApplication.TAG)
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
        mMainHandler = Handler(Looper.myLooper())
    }

    override fun loadList() {
        var deviceList: MutableList<EntityDevices>?

        //Create a new RecyclerView Adapter
        if(recyclerViewAdapter == null)
            recyclerViewAdapter = DeviceRecyclerViewAdapter(this@PresenterMain)

        //Show list
        view.showList(recyclerViewAdapter)

        //Load the device list from the database
        mHandler.post {
            deviceList = AppDatabase.getInstance(view.getAppContext())
                    .getDeviceDao()
                    .getAllDevices()
            //Log
            HomeApplication.log("Items received from the DB : $deviceList")

            //Pass the device list
            recyclerViewAdapter?.setNodeList(deviceList)

            mMainHandler.post {
                // If the item list is empty, show a help card on the main activity |
                // Please add items to the list
                deviceList?.apply {
                    if(this.isEmpty())
                    //Tell the user to add a few items to the screen
                        view.showEmptyControl(true)
                    else {
                        view.showEmptyControl(false)
                        recyclerViewAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onSwitchExecuted(isChecked: Boolean, intPosition: Int, nodeId: Long?) {
        nodeId?.let {
            val call: Call<ModelDevice>? = RFDeviceInteraction
                    .server(recyclerViewAdapter?.getAdapterItemById(it)?.deviceIp)
                    .apiInterface
                    ?.flip(isChecked, nodeId)
            call?.enqueue(this@PresenterMain)
        }
    }

    //Displays the add new fragment
    override fun showAddNewFragment() {
        val fragment = AddNewFragment()
        fragment.show(view.getActivityFragmentManager(), AddNewFragment::class.java.simpleName)
        HomeApplication.log("Accessing create new device")
    }

    override fun onFailure(call: Call<ModelDevice>?, t: Throwable?) {
        HomeApplication.log("The API call failed with the message : ${t?.message}.")
        //Failed Tell adapter to go back to previous setup
        mHandler.post {
            //Show a display message
            Toast.makeText(view.getAppContext(), "Unable to connect to IoT Device", Toast.LENGTH_SHORT).show()
            //Reset the list
            refreshWithDB()
        }
    }

    /**
     * Refreshes the adapter list with items from the database
     */
    private fun refreshWithDB() {
//        mHandler.post {
//            HomeApplication.log("Refreshing User adapter list with data from DB")
//            val deviceList = AppDatabase.getInstance(view.getAppContext())
//                    .getDeviceDao()
//                    .getAllDevices()
//            //Log
//            HomeApplication.log("Items received from the DB : $deviceList")
//            HomeApplication.log("Items on the adapterList : ${recyclerViewAdapter?.getAdapterItems()}")
//
//            recyclerViewAdapter?.setNodeList(deviceList)
//        }

        //Call notify to refresh the list
        mMainHandler.post { recyclerViewAdapter?.notifyDataSetChanged() }

    }

    override fun onResponse(call: Call<ModelDevice>?, response: Response<ModelDevice>?) {
        //Load the device list from the database
        response?.let {
            if (it.isSuccessful)
                it.body()?.let {
                    mHandler.post {

                        //Save response in a separate variable
                        val responseBody = it

                        AppDatabase.getInstance(view.getAppContext())
                                .getDeviceDao()
                                .updateDevice(responseBody.status, responseBody._id!!)
                        //Log
                        HomeApplication.log("Data updated in DB for device ID ${responseBody._id} to ${responseBody.status!!}")

                        call?.let {
                            //Figure out the ID of the modified device
                            val deviceId = it.request().url().queryParameterValues("Id")[0].toLong()

                            //Update the isSwitchedOn value in the adapter
                            recyclerViewAdapter?.getAdapterItemById(deviceId)?.isDeviceSwitchedOn = responseBody.status

                            //Call notify to refresh the list
                            mMainHandler.post {
                                recyclerViewAdapter?.let {
                                    it.notifyItemChanged(it.getAdapterPositionById(deviceId))
                                }
                            }
                        }
                    }
                }
            else {
                onFailure(call, Throwable(it.message()))
            }
        }
    }
}