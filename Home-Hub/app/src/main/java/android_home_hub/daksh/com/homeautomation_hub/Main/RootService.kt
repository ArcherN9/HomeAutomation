package android_home_hub.daksh.com.homeautomation_hub.Main

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication

class RootService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //The service has been started | Register for pubnub notifications here
        (application as HomeApplication).getPubNub()
        return START_STICKY
    }
}