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

    private fun initializeDB() {
        AppDatabase.getInstance(this@HomeApplication)
    }

    private fun initialize(): PubNub? {
        //Setup Pubnub
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = "sub-c-71e45c9a-71fc-11e8-b452-f6257dc827e2"
        pnConfiguration.publishKey = "pub-c-22b06265-0689-408c-8d6b-3532ddc609d3"

        //Using the configuration create a new PubNub object
        pubnub = PubNub(pnConfiguration)
        pubnub.addListener(PubNubHandler())
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
        val TAG_START: String = ">>>>>>>>>"
        val TAG_END: String = "<<<<<<<<<"

        //Log Thread
        fun log(operation: String) = Log.d(TAG, "$TAG_START Operation \"$operation\" executing on Thread ${Thread.currentThread().id} $TAG_END")
    }
}