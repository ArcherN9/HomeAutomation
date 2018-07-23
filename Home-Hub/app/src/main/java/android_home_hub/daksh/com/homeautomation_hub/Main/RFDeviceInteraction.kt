package android_home_hub.daksh.com.homeautomation_hub.Main

import android_home_hub.daksh.com.homeautomation_hub.Config.RetroFit
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import android_home_hub.daksh.com.homeautomation_hub.Main.Model.ModelDevice
import retrofit2.Call
import retrofit2.http.*

object RFDeviceInteraction {

    /**
     * An API interface used in the main activity. It comprises of all services limited to the
     * Main Activity.
     */
    internal var apiInterface: APIInterface = RetroFit.retrofitClient.create(APIInterface::class.java)

    /**
     * The method reconfigures Retrofit with a new server URL
     */
    internal fun server(strServerUrl: String?): RFDeviceInteraction {
        RetroFit.reconfigureServer(strBaseUrl = strServerUrl)

        //Remake API Interface
        apiInterface = RetroFit.retrofitClient.create(APIInterface::class.java)
        return this@RFDeviceInteraction
    }

    /**
     * The interface used by retrofit to define network calls
     */
    interface APIInterface {

        //Segmented path goes here.
        @GET("/action")
        fun flip(
                //Queries are key=value pairs sent in the URL.
                @Query("status") strAction: String,
                @Query("Id") strId: Long
        ): Call<ModelDevice>
    }
}