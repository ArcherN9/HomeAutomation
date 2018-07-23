package android_home_hub.daksh.com.homeautomation_hub.Config

import android.util.Log
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult

class PubNubHandler: SubscribeCallback() {

    init {
        //Subscribe to Home automation master channel
//        pubnub.subscribe().channels(arrayListOf("Home/LivingRoom/FloorLamp")).execute()
    }

    override fun status(pubnub: PubNub?, status: PNStatus?) {
        when (status?.operation) {
            // let's combine unsubscribe and subscribe handling for ease of use
            PNOperationType.PNSubscribeOperation ->
                Log.i("","")
            PNOperationType.PNUnsubscribeOperation ->
                // note: subscribe statuses never have traditional
                // errors, they just have categories to represent the
                // different issues or successes that occur as part of subscribe
                when (status.category) {
                    PNStatusCategory.PNConnectedCategory ->
                        Log.i("","")
                        // this is expected for a subscribe, this means there is no error or issue whatsoever
                    PNStatusCategory.PNReconnectedCategory->
                        Log.i("","")
                        // this usually occurs if subscribe temporarily fails but reconnects. This means
                        // there was an error but there is no longer any issue
                    PNStatusCategory.PNDisconnectedCategory ->
                        Log.i("","")
                        // this is the expected category for an unsubscribe. This means there
                        // was no error in unsubscribing from everything
                    PNStatusCategory.PNUnexpectedDisconnectCategory ->
                        Log.i("","")
                        // this is usually an issue with the internet connection, this is an error, handle appropriately
                    PNStatusCategory.PNAccessDeniedCategory ->
                        Log.i("","")
                        // this means that PAM does allow this client to subscribe to this
                        // channel and channel group configuration. This is another explicit error
                    else ->
                        Log.i("","")
                        // More errors can be directly specified by creating explicit cases for other
                        // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                }

            PNOperationType.PNHeartbeatOperation ->
                // heartbeat operations can in fact have errors, so it is important to check first for an error.
                // For more information on how to configure heartbeat notifications through the status
                // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                if (status.isError) {
                    // There was an error with the heartbeat operation, handle here
                } else {
                    // heartbeat operation was successful
                }
            else -> {
                // Encountered unknown status type
            }
        }
    }

    override fun presence(pubnub: PubNub?, presence: PNPresenceEventResult?) {

    }

    override fun message(pubnub: PubNub?, message: PNMessageResult?) {
    }

}