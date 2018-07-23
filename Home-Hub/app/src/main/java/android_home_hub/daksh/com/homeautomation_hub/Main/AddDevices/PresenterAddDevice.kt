package android_home_hub.daksh.com.homeautomation_hub.Main.AddDevices

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android_home_hub.daksh.com.homeautomation_hub.AppDatabase
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import com.hcl.daksh.android_poc_camp.Dashboard.ContractAddDevices
import com.hcl.daksh.android_poc_camp.Dashboard.PresenterMain
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices

class PresenterAddDevice(private var view: ContractAddDevices.View): ContractAddDevices.Presenter {

    init {
        //Associate this presenter with the view
        view.presenter = this@PresenterAddDevice
    }

    //Handler for minimal tasks in BG
    override lateinit var mHandler: Handler
    override lateinit var mHandlerThread: HandlerThread

    override fun start() {
        //Create threads to do trivial background tasks with option to handle message in UI thread
        mHandlerThread = HandlerThread(HomeApplication.TAG)
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
    }

    //Registers a new device in the new DB
    override fun registerDevice(ipAddress: String, deviceName: String, deviceLocation: String, deviceType: Int, isDeviceSwitchedOn: Boolean) {
        if(!ipAddress.isEmpty() && !deviceName.isEmpty()) {
            val entity = EntityDevices(deviceName = deviceName, deviceIp = ipAddress, deviceLocation = deviceLocation, deviceType = deviceType, isDeviceSwitchedOn = isDeviceSwitchedOn, _id = null)

            //Create a new entry in the DB
            mHandler.post {
                HomeApplication.log("Saving new device DB: $entity")
                AppDatabase.getInstance(view.getAppContext()).getDeviceDao().insertDevice(entity)
                HomeApplication.log("Device saved to database with ID: ${AppDatabase.getInstance(view.getAppContext()).getDeviceDao().getDeviceByDeviceIp(deviceIpAddress = ipAddress)}")
            }

            view.dismissDialog()
        } else {
            HomeApplication.log("${HomeApplication.TAG_START} Device registration failed. Tried to store deviceName : $deviceName & ipAddress: $ipAddress ${HomeApplication.TAG_END}")
            view.invalidateInput()
        }
    }
}