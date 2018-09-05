package android_home_hub.daksh.com.homeautomation_hub.Config

import android.os.Bundle
import android.os.Message
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import android_home_hub.daksh.com.homeautomation_hub.Main.MainActivity
import android_home_hub.daksh.com.homeautomation_hub.R
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult

class PubNubHandler(var application: HomeApplication): SubscribeCallback() {

    init {
        //Subscribe to Home automation master channel
        application.getPubNub()?.subscribe()?.channels(arrayListOf(
                application.getString(R.string.Home),
                application.getString(R.string.home_devices_actions)
        ))?.execute()
    }

    override fun status(pubnub: PubNub?, status: PNStatus?) {
        when (status?.operation) {
        // let's combine unsubscribe and subscribe handling for ease of use
            PNOperationType.PNSubscribeOperation ->
                //Log the subscription
                HomeApplication.log("HomeHub subscription request for ${status.affectedChannels} returned with the status code ${status.statusCode}")
            PNOperationType.PNUnsubscribeOperation ->
                // note: subscribe statuses never have traditional
                // errors, they just have categories to represent the
                // different issues or successes that occur as part of subscribe
                when (status.category) {
                    PNStatusCategory.PNConnectedCategory ->
                        HomeApplication.log(status.toString())
                // this is expected for a subscribe, this means there is no error or issue whatsoever
                    PNStatusCategory.PNReconnectedCategory->
                        HomeApplication.log(status.toString())
                // this usually occurs if subscribe temporarily fails but reconnects. This means
                // there was an error but there is no longer any issue
                    PNStatusCategory.PNDisconnectedCategory ->
                        HomeApplication.log(status.toString())
                // this is the expected category for an unsubscribe. This means there
                // was no error in unsubscribing from everything
                    PNStatusCategory.PNUnexpectedDisconnectCategory ->
                        HomeApplication.log(status.toString())
                // this is usually an issue with the internet connection, this is an error, handle appropriately
                    PNStatusCategory.PNAccessDeniedCategory ->
                        HomeApplication.log(status.toString())
                // this means that PAM does allow this client to subscribe to this
                // channel and channel group configuration. This is another explicit error
                    else ->
                        HomeApplication.log(status.toString())
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

    /**
     * This method is executed whenever a new message is received from the server. All messages are received here
     * they need to be manually router to MainActivity
     */
    override fun message(pubnub: PubNub?, message: PNMessageResult?) {
        HomeApplication.log("A new message has been received on ${message?.channel} channel sent by ${message?.publisher}.\n Message : ${message?.message}")

        //Based on what channel the message was received on, figure out how to handle the message
        message?.apply {

            when(channel) {
            // If the message received is from the actions channel. Ex Actions
            // 1. A device was on state was toggled
            // At this point, only switching something on & off is supported
                application.getString(R.string.home_devices_actions) -> {
                    //Check if the mainHandler on MainActivity is not null.
                    //If it is not null, the activity is visible and a message needs to be sent to the activity
                    //to update it
                    MainActivity.mainHandler?.let {
                        //Obtain the message of the Handler
                        val handlerMessage: Message = it.obtainMessage()
                        //Create a new bundle to pass on the data received
                        val bundle = Bundle()
                        bundle.putString(MainActivity.EXTRAS_ACTIONS_DEVICE, message.message.toString())
                        //Store the bundle in the data field
                        handlerMessage.data = bundle
                        //Send message to the handler
                        it.sendMessage(handlerMessage)
                    }
                }

                else -> {
                    //Do nothing
                }
            }
        }
    }

}