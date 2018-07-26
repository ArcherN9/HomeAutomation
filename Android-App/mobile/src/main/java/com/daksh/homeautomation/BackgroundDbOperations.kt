package com.daksh.homeautomation

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Message
import com.daksh.homeautomation.MainActivity.MainActivity
import com.google.gson.Gson
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices

class BackgroundDbOperations : IntentService("BackgroundDbOperations") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SAVE -> {
                val param1 = intent.getStringExtra(EXTRA_DEVICE)

                handleSaveIntent(param1)
            }
        }
    }

    /**
     * method is executed when ACTION_SAVE is received from the intent.
     */
    private fun handleSaveIntent(device: String?) {
        //Convert String to POJO
        val gson = Gson()
        val entity = gson.fromJson(device, EntityDevices::class.java)

        //Save to DB
        AppDatabase.getInstance(baseContext)
                .getDeviceDao()
                .insertDevice(entity)

        MainActivity.mHandler?.apply {
            val handlerMessage: Message = obtainMessage()
            val bundle = Bundle()
            bundle.putString("Message", device)
            handlerMessage.data = bundle
            sendMessage(handlerMessage)
        }
    }

    companion object {

        const val ACTION_SAVE = "com.daksh.homeautomation.action.save"
        const val EXTRA_DEVICE = "com.daksh.homeautomation.extra.device"
    }
}
