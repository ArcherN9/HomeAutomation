package com.daksh.homeautomation.FCM.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FCMModel {

    @Expose
    @SerializedName("message")
    var strMessage: String? = null

    @Expose
    @SerializedName("success")
    var isSuccess: Boolean? = false

    override fun toString(): String {
        return "Data received from server : $strMessage"
    }
}