package android_home_hub.daksh.com.homeautomation_hub

import android.app.Application
import android.util.Log
import android_home_hub.daksh.com.homeautomation_hub.Config.PubNubHandler
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub

class HomeApplication: Application() {

    // the Pubnub instance
    private lateinit var pubnub: PubNub

    override fun onCreate() {
        super.onCreate()

        initializeDB()
        initialize()
    }

    private fun initializeDB() = AppDatabase.getInstance(this@HomeApplication)

    private fun initialize(): PubNub? {
        //Setup Pubnub
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = getString(R.string.Key_Pubnub_subscriber)
        pnConfiguration.publishKey = getString(R.string.Key_Pubnub_publisher)

        //Using the configuration create a new PubNub object
        pubnub = PubNub(pnConfiguration)
        pubnub.addListener(PubNubHandler(this@HomeApplication))
        return pubnub
    }

    /**
     * returns the pubnub instance
     */
    fun getPubNub(): PubNub? {
        return if(::pubnub.isInitialized)
            pubnub
        else
            initialize()
    }

    companion object {

        //TAGs For logging
        val TAG: String = HomeApplication::class.java.simpleName
        val TAG_START: String = ">>>>>>>>>>>>>>>>>>"
        val TAG_END: String = "<<<<<<<<<<<<<<<<<<"

        //Log Thread
        fun log(operation: String) = Log.d(TAG, "$TAG_START $operation. Executing on Thread ${Thread.currentThread().id} $TAG_END")
    }
}